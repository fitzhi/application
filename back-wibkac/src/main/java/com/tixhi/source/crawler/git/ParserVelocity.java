package com.tixhi.source.crawler.git;

import static com.tixhi.Global.DASHBOARD_GENERATION;
import static com.tixhi.Global.PROJECT;

import com.tixhi.bean.AsyncTask;

import lombok.Data;

/**
 * <p>
 * This class is used by {@link GitCrawler} to monitor the loading velocity of the application.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
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
	 * Build the parserVelocity for the given project
	 * @param idProject the project identifier
	 * @param tasks the asynchronous tasks manager
	 */
	public ParserVelocity(int idProject, AsyncTask tasks) {
		this.idProject = idProject;
		this.tasks = tasks;
	}
	
	public void increment() {
		sessionAdd++;
		totalAdd++;
		if (sessionAdd == SESSION_BREAK) {
			this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  idProject, 
				String.format("%d changes have been detected.", totalAdd));
			sessionAdd = 0;
		}
	}
		
	/**
	 * Finalize the measure taken for this evaluation session.
	 */
	public void finalize() {
		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  idProject, 
				String.format("Changes file is complete : %d changes  record.", totalAdd));		
		totalAdd = 0;
		sessionAdd = 0;
	}
}
