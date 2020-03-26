package com.fitzhi.service.sse;

import com.fitzhi.data.external.ActivityLog;

public interface LogReport {

	/**
	 * Return a log of activity for an asynchronous task.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id the given identifier <i>(might be a project, a staff or anything else)</i>
	 * @return a task report or {@code null} if the task has completed
	 */
	public ActivityLog currentLog(String operation, String title, int id);

}
