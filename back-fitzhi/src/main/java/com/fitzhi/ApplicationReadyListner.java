package com.fitzhi;

import com.fitzhi.source.crawler.RepoScanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApplicationReadyListner implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	RepoScanner repoScanner;

	/**
	 * Number of days of inactivity before inactivation of a staff member.
	 */
	@Value("${reboot.code.analysis}")
	private boolean rebootCodeAnalysis;	
	

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (rebootCodeAnalysis) {
			try {
				repoScanner.generateAllAsync();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} else {
			if (log.isInfoEnabled()) {
				log.info("Code analysis at startup is disable");
			}
		}
	}
}
