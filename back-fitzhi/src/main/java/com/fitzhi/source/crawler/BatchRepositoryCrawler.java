package com.fitzhi.source.crawler;

import com.fitzhi.ApplicationReadyListener;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.git.BatchGitCrawler;

/**
 * <p>
 * This Interface is in charge of the batch source code crawler.
 * </p>
 * <p>
 * This interface is used to launch a 
 * {@link RepoScanner#generateAsync(com.fitzhi.data.internal.Project, com.fitzhi.controller.in.SettingsGeneration) Project analysis}
 *  in an asynchronous mode for each project eligible.
 * </p>
 * @see ApplicationReadyListener
 * @see ScheduledCodeAnalysis
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface BatchRepositoryCrawler {

	/**
	 * <p>
	 * This method is a batch method in charge of the generation of all projects.
	 * It will iterate on each project, and execute {@link RepoScanner#generateAsync}.
	 * </p>
	 * <p><b>
	 * The underlying implementation {@link BatchGitCrawler} is hosting the annotation {@code @async}.
	 * </b></p>
	 * @throws ApplicationException thrown if any problem occurs during the generation
	 */
	void completeGeneration() throws ApplicationException;

}
