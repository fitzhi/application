package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * Each task instance is representing a technical task running asynchronously in the JVM.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */

public @Data class Task {
	
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
	 * </pd
	 */
	public void complete() {
		setComplete(true);
		if (this.activityLogs.isEmpty()) {
			// We sort the logs saved for this task
			this.activityLogs.sort(Comparator.comparing(TaskLog::getLogTime).reversed());
			
			// We affect the last log for this task
			setLastBreath(activityLogs.get(0));
		
			// We clear the log
			activityLogs.clear();
		}
	}

}
