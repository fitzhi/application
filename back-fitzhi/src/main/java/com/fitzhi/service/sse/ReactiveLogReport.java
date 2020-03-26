package com.fitzhi.service.sse;

import static com.fitzhi.Global.PROJECT;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.external.ActivityLog;
import com.fitzhi.data.internal.Task;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ReactiveLogReport implements LogReport {

	/**
	 * Asynchronous tasks list.
	 */
	@Autowired
	AsyncTask tasks;

	/**
	 * Type of operation recorded
	 */
	String operation;

	/**
	 * Title related to the identifier
	 */
	String title;
	
	/**
	 * The identifier
	 */
	int id;
	
	int i = 0;
	
	/**
	 * Return a task.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id the given identifier <i>(might be a project, a staff or anything else)</i>
	 * @return a task report or {@code null} if the task has completed
	 */
	@Override
	public ActivityLog currentLog(String operation, String title, int id) {
		final Task task = tasks.getTask(operation, PROJECT, id);
		return (!task.isComplete()) ? task.buildLastestLog() : new ActivityLog(task.getLastBreath(), true);
	}
}
