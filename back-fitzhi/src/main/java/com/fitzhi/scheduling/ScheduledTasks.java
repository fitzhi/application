package com.fitzhi.scheduling;

import java.util.Calendar;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.source.crawler.BatchRepositoryCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is a scheduler in charge of the execution of the global code analysis in batch mode.
 * This scheduler is configured with the cron settings {@code cron.code.analysis} declared in the {@code application.properties} file. 
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
	AsyncTask asyncTask;

	/**
	 * Current minute of the execution of the medthiod {@link #tasksReport}.
	 * This property is used in a work-around 
	 */
	int minute = -1;

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
			projectHandler.updateStaffSkillLevel(projectHandler.processGlobalExperiences());
			if (log.isInfoEnabled()) {
				log.info("Peacefully terminate the experiences detection.");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Scheduled(cron = "${cron.tasks.report}")
	public void tasksReport() {
		try {
			// For an unknown, this method is executed multiple times when the cron is equal to '* 0/5 * * * ?'
			if (minute != Calendar.getInstance().get(Calendar.MINUTE)) {
				minute = Calendar.getInstance().get(Calendar.MINUTE);
				if (log.isInfoEnabled()) {
					log.info(Global.LN + "Current active tasks :" + Global.LN + asyncTask.trace() + Global.LN);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}