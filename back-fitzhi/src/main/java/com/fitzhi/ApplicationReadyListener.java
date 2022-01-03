package com.fitzhi;

import com.fitzhi.source.crawler.BatchRepositoryCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This is listening the startup of the application to launch a global code analysis in batch mode
 * if the setting {@code "startup.code.analysis"} in {@code application.properties} is set to {@code true}
 */
@Component
@Slf4j
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	BatchRepositoryCrawler batchRepoScanner;

	/**
	 * <p>
	 * Start the analysis after spring boot completion.
	 * </p>
	 * {@code true} we start the global generation
	 */
	@Value("${startup.code.analysis}")
	private boolean rebootCodeAnalysis;	

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (rebootCodeAnalysis) {
			try {
				batchRepoScanner.completeGeneration();
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
