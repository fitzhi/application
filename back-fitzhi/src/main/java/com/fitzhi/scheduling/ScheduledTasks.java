package com.fitzhi.scheduling;

import com.fitzhi.source.crawler.BatchRepositoryCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is a scheduler in charge of the execution of the global code analysis in batch mode.
 * This scheduler is configured with the cron settings {@code cron.code.analysis} declared in the {@code application.properties} file. 
 */
@EnableScheduling
@Slf4j
public class ScheduledTasks {

	@Autowired
	BatchRepositoryCrawler batchRepoScanner;

	@Scheduled(cron = "${cron.code.analysis}")
	public void codeAnalysis() {
		try {
			batchRepoScanner.completeGeneration();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}