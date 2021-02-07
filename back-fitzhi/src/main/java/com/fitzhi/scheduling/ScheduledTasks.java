package com.fitzhi.scheduling;

import com.fitzhi.source.crawler.RepoScanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;


@EnableScheduling
@Slf4j
public class ScheduledTasks {

	@Autowired	
	private RepoScanner crawler;

	@Scheduled(cron = "${cron.code.analysis}")
	public void codeAnalysis() {
		try {
			crawler.generateAllAsync();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}