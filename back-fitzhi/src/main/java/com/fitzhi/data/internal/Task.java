package com.fitzhi.data.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fitzhi.data.external.ActivityLog;

import lombok.Data;

/**
 * <p>
 * Each task instance is representing a technical task running asynchronously inside the Spring container.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */

public @Data class Task implements Serializable {
	
	/**
	 * serialVersionUID for {@code serializable} purpose.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Maximum number of logs to keep in the history collection.
	 */
	private final static int MAX_LOGS = 5;
	
	/**
	 * Type of operation
	 */
	final String operation;
	
	/**
	 * Title corresponding the entity id
	 */
	final String title;
	
	/**
	 * Identifier. It might be a project, a staff, a skill...
	 */
	final int id;	

	/**
	 * {@code true} if the task has been successfully completed, {@code false} otherwise.
	 */
	boolean complete;

	/**
	 * <ul>
	 * <li>
	 * {@code true} if the task has been completed <font color="red">WITH AN EXCEPTION</font>
	 * </li>
	 * <li>
	 * {@code false} otherwise.
	 * </li>
	 * </ul>
	 */
	boolean completeOnError = false;
	
	/**
	 * List of logs recorded by the asynchronous application.
	 */
	List<TaskLog> activityLogs = new ArrayList<>();
		
	/**
	 * Last breath of the task before termination.
	 */
	TaskLog lastBreath;

	/**
	 * <p>
	 * Record a task log in this tasks history.
	 * </p>
	 * @param taskLog the given task log
	 */
	public void addActivity(TaskLog taskLog) {
		if (activityLogs.size() == MAX_LOGS) {
			activityLogs.sort(Comparator.comparing(TaskLog::getLogTime));
			activityLogs.remove(0);
		}
		activityLogs.add(taskLog);
	}
	
	/**
	 * <p>
	 * The task is Complete.
	 * </p>
	 * @param successfuly {@code TRUE} if the task has been successfully completed, {@code FALSE} if an exception has been thrown
	 */
	public void complete(boolean successfuly) {
		setComplete(true);
		setCompleteOnError(!successfuly);
		if (!this.activityLogs.isEmpty()) {

			// We affect the last log for this task
			setLastBreath(latestLog());
		
			// We clear the log
			activityLogs.clear();
		}
	}

	/**
	 * @return the latest log 
	 */
	//TODO the latest log has to be stored in the task container and not periodically computed.
	TaskLog latestLog() {
		this.activityLogs.sort(Comparator.comparing(TaskLog::getLogTime).reversed());
		return activityLogs.get(0);
	}
	
	/**
	 * @return the latest log of this task in an {@link ActivityLog} format.
	 */
	public ActivityLog buildLastestLog() {
		TaskLog log = latestLog();
		ActivityLog activityLog = new ActivityLog(log.code, log.message, log.logTime, complete, completeOnError);
		return activityLog;
	}
}
