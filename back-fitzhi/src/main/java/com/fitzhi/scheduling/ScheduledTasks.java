package com.fitzhi.scheduling;

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

	@Autowired
	BatchRepositoryCrawler batchRepoScanner;

	@Value("${cron.code.analysis}")
	private String cronCodeAnalysis;

	@Value("${cron.experiences.detection}")
	private String cronExperiencesDetection;
	
	@Autowired
	ProjectHandler projectHandler;

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

}