package com.fitzhi.service.sse;

import static com.fitzhi.Global.PROJECT;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.external.ActivityLog;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Task;
import com.fitzhi.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Repository
@Slf4j
public class ReactiveLogReport implements LogReport {

	/**
	 * Asynchronous tasks list.
	 */
	@Autowired
	@Qualifier("default")
	AsyncTask tasks;

	@Autowired
	ProjectHandler projectHandler;

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
	 * @param operation the type of operation recorded
	 * @param title the title related to the identifier
	 * @param id the given identifier <i>(might be a project, a staff or anything else)</i>
	 * @return a task report or {@code null} if the task has completed
	 */
	private ActivityLog currentLog(String operation, String title, int id) {
		final Task task = tasks.getTask(operation, PROJECT, id);
		if (task == null) {
			log.info(String.format("End for %s for %s : %d", operation, title, id));
			String projectName = projectName(id);
			return new ActivityLog(id, 0, String.format("Operation completed for %s !", projectName), 100, LocalDate.MIN.toEpochDay(), true, false);
		}
		return (!task.isComplete()) ? task.buildLastestLog(id) : new ActivityLog(id, task.getLastBreath(), true);
	}

	private String projectName( int idProject) {
		try {
			Project project = projectHandler.lookup(idProject);
			if (project == null) {
				log.warn(String.format("WTF : Project %d hasn't been found", idProject));
				throw new ApplicationException();
			}
			return project.getName();
		} catch (final ApplicationException ae) {
			return String.format("Project %d unknown", idProject);
		}
	}

	@Override
	public Flux<ActivityLog> sunburstGenerationLogNext(String operation, int idProject) {
	    
		if (log.isDebugEnabled()) {
			log.debug(String.format("Logs are listening for operation %s of project %d", operation, idProject));
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
