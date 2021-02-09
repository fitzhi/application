package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;
import static com.fitzhi.Global.NO_PROGRESSION;

import com.fitzhi.bean.AsyncTask;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This class is used by {@link GitCrawler} to monitor the loading velocity of the application.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
public @Data class ParserVelocity {

	/**
	 * Reinitialize the session increment of ADDs where the increment reaches this value;
	 */
	private final int SESSION_BREAK = 1000;
	
	/**
	 * Current number of changes added in a session of {@link #SESSION_BREAK} records.
	 */
	private int sessionAdd = 0;

	/**
	 * Total number of changes added
	 */
	private int totalAdd = 0;
	
	/**
	 * Current active project.
	 */
	private final int idProject;
	
	/**
	 * Current active task.
	 */
	private final AsyncTask tasks;
	
	/**
	 * Total time spent in method {@link GitCrawler#fileGitHistory(com.fitzhi.data.internal.Project, org.eclipse.jgit.lib.Repository, String) fileGitHistory} in ms
	 */
	private long totalDurationInFileGitHistory = 0;

	/**
	 * Total time spent in method {@link GitCrawler#retrieveDiffEntry(String, org.eclipse.jgit.lib.Repository, org.eclipse.jgit.revwalk.RevCommit, org.eclipse.jgit.revwalk.RevCommit) retrieveDiffEntry} in ms
	 */
	private long totalDurationInRetrieveDiffEntry = 0;

	/**
	 * Build the parserVelocity for the given project
	 * @param idProject the project identifier
	 * @param tasks the asynchronous tasks manager
	 */
	public ParserVelocity(int idProject, AsyncTask tasks) {
		this.idProject = idProject;
		this.tasks = tasks;
	}
	
	/**
	 * Increment the variable {@link #sessionAdd} which aggregates the number of changes processed by the analyzer.  
	 */
	public void increment() {
		sessionAdd++;
		totalAdd++;
		if (sessionAdd == SESSION_BREAK) {
			this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  idProject, 
				String.format("%d changes in the GIT repository have been processed.", totalAdd), 
				NO_PROGRESSION);
			sessionAdd = 0;
		}
	}
		
	/**
	 * Finalize the measure taken for this evaluation session.
	 */
	public void complete() {
		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  idProject, 
				String.format("Changes file is complete : %d changes record.", totalAdd), NO_PROGRESSION);		
		totalAdd = 0;
		sessionAdd = 0;
	}

	/**
	 * <p>
	 * Log the duration of a single call in {@link GitCrawler#retrieveDiffEntry(String, org.eclipse.jgit.lib.Repository, org.eclipse.jgit.revwalk.RevCommit, org.eclipse.jgit.revwalk.RevCommit) retrieveDiffEntry}
	 * </p>
	 * @param durationInRetrieveDiffEntry the duration to call
	 */
	public void logDurationInRetrieveDiffEntry (long durationInRetrieveDiffEntry) {
		this.totalDurationInRetrieveDiffEntry += durationInRetrieveDiffEntry;
	}

	/**
	 * <p>
	 * Log the duration of a single call in {@link GitCrawler#fileGitHistory(com.fitzhi.data.internal.Project, org.eclipse.jgit.lib.Repository, String) fileGitHistory}
	 * </p>
	 * @param durationInFileGitHistory the duration to log
	 */
	public void logDurationInFileGitHistory (long durationInFileGitHistory) {
		this.totalDurationInFileGitHistory += durationInFileGitHistory;
	}


	/**
	 * Display the velocity results. 
	 */
	public void logReport() {
		log.info(String.format("Project numero %d", this.idProject));
		log.info(String.format("Duration in fileGitHistory %d", this.totalDurationInFileGitHistory));
		log.info(String.format("Duration in retrieveDiffEntry %d", this.totalDurationInRetrieveDiffEntry));
	}
}
