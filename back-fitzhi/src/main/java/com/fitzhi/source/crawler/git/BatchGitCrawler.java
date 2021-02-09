package com.fitzhi.source.crawler.git;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.BatchRepoScanner;
import com.fitzhi.source.crawler.RepoScanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Batch source code crawler. 
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service
public class BatchGitCrawler implements BatchRepoScanner {

	/**
	 * Service in charge of handling the projects.
	 */
	@Autowired
	ProjectHandler projectHandler;

	/**
	 * Collection of active tasks.
	 */
	@Autowired()
	AsyncTask tasks;

	/**
	 * Source code crawler
	 */
	@Autowired	
	@Qualifier("GIT")
	private RepoScanner crawler;

	@Override
	@Async
	public void completeGeneration() throws ApplicationException {
		if (log.isInfoEnabled()) {
			log.info( "Starting the analysis of projects in batch mode.");
		}
		for (Project project : projectHandler.getProjects().values()) {
			if (log.isInfoEnabled()) {
				log.info( String.format("Analyzing project %s.",project.getName()));
			}
			
			// We analyze each project if the project has a connection settings.
			if (project.getConnectionSettings() > 0) {
				// We invoke RepoScanner.generateAsync from inside this method 
				crawler.generateAsync(project, new SettingsGeneration(project.getId()));
			}

			if (log.isInfoEnabled()) {
				log.info( String.format("The project %s is analyzed.",project.getName()));
			}
		}
	}

}

