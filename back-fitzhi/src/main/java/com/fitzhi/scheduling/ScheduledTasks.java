package com.fitzhi.scheduling;

import java.time.LocalDate;
import java.util.Calendar;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.source.crawler.BatchRepositoryCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is a scheduler in charge of the execution of the global code analysis in batch mode.
 * This scheduler is configured with the cron settings {@code cron.code.analysis}, {@code cron.experiences.detection} and
 * {@code cron.constellations.generation} declared in the {@code application.properties} file. 
 */
@EnableScheduling
@Slf4j
@Component
public class ScheduledTasks {

	@Value("${cron.code.analysis}")
	private String cronCodeAnalysis;
	
	@Value("${cron.experiences.detection}")
	private String cronExperiencesDetection;

	@Value("${cron.tasks.report}")
	private String cronTasksReport;

	@Autowired
	BatchRepositoryCrawler batchRepoScanner;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	DataHandler dataHandler;

	@Autowired
	AsyncTask asyncTask;

	/**
	 * Current minute of the execution of the medthiod {@link #tasksReport}.
	 * This property is used in a work-around 
	 */
	int minute = -1;

	private final Object lockObj = new Object();

	@Scheduled(cron = "${cron.code.analysis}")
	public void codeAnalysis() {
		try {
			if (log.isInfoEnabled()) {
				log.info("Starting the complete generation.");
			}
			batchRepoScanner.completeGeneration();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Scheduled(cron = "${cron.experiences.detection}")
	public void experiencesDetection() {
		try {
			if (log.isInfoEnabled()) {
				log.info("Starting the experiences detection.");
			}
			projectHandler.processProjectsExperiences();
			projectHandler.updateProjectStaffSkillLevel(projectHandler.processGlobalExperiences());
			if (log.isInfoEnabled()) {
				log.info("Peacefully terminate the experiences detection.");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * <strong>MONTHLY</strong> generation of the skills constellations.
	 * </p>
	 * This method is daily invoked to ensure that this generation is effectively processed,
	 * but the generation will be processed only once per month.
	 */
	@Scheduled(cron = "${cron.constellations.generation}")
	public void constellationsGeneration() {
		try {
			LocalDate month = LocalDate.now();
			if (!dataHandler.hasAlreadySavedSkillsConstellations(month)) {
				if (log.isInfoEnabled()) {
					log.info(String.format("Starting the generation of the constellations for month %d/%d.", 
						month.getMonthValue(), month.getYear()));
				}
				staffHandler.saveCurrentConstellations();
				if (log.isInfoEnabled()) {
					log.info("Peacefully terminate the generation of the constellations.");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Scheduled(cron = "${cron.tasks.report}")
	public void tasksReport() {
		try {
			// For an unknown reason, this method is executed multiple times when the cron is equal to '* 0/5 * * * ?'.
			// We use a minute based singleton.
			synchronized(lockObj) {
				if ((minute != Calendar.getInstance().get(Calendar.MINUTE)) && (minute != Calendar.getInstance().get(Calendar.MINUTE) - 1)) {
					minute = Calendar.getInstance().get(Calendar.MINUTE);
					if (log.isInfoEnabled() && !asyncTask.isEmpty()) {
						log.info(Global.LN + "Current active tasks :" + Global.LN + asyncTask.trace() + Global.LN);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}