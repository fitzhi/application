package com.fitzhi.service.sse;

import static com.fitzhi.Global.PROJECT;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.external.ActivityLog;
import com.fitzhi.data.internal.Task;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

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
	 * The entity identifier, most probably the project.
	 */
	int id;
	
	/**
	 * Return a log of activity for an asynchronous task.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id the given identifier <i>(might be a project, a staff or anything else)</i>
	 * @return a task report or {@code null} if the task has completed
	 */
	private ActivityLog currentLog(String operation, String title, int id) {
		final Task task = tasks.getTask(operation, PROJECT, id);
		if (task == null) {
			return new ActivityLog(0, String.format("End for %s for %s : %d", operation, title, id), 
					LocalDate.MIN.toEpochDay(), true, false);
		}
		return (!task.isComplete()) ? task.buildLastestLog() : new ActivityLog(task.getLastBreath(), true);
	}

	@Override
	public Flux<ActivityLog> sunburstGenerationLogNext(String operation, int idProject) {
	    
		if (log.isDebugEnabled()) {
			log.debug(String.format("Activating logs listening for operation %s of project %d", operation, idProject));
		}
		//
		// Simulate data streaming every 1 second.
		//
        return Flux.interval(Duration.ofMillis(1000))
        		.map(l -> {
        			final ActivityLog actiLog =  this.currentLog(operation, PROJECT, idProject);
        			if (log.isDebugEnabled()) {
        				log.debug(String.format("Sending log %s", actiLog.toString()));
        			}
        			return actiLog;
        		})
        		.distinctUntilChanged(ActivityLog::hashCode)
        		.takeUntil((ActivityLog actiLog) -> actiLog.isComplete() || actiLog.isCompleteOnError())
        		.doOnComplete(() -> {this.tasks.removeTask(operation, PROJECT, idProject);})
 //       		.doOnCancel(() -> {this.tasks.removeTask(operation, PROJECT, idProject);})
        		.log();
	}

}
