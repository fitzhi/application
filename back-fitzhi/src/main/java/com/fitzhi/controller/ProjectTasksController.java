package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_TASK_NOT_FOUND;
import static com.fitzhi.Error.MESSAGE_TASK_NOT_FOUND;
import static com.fitzhi.Global.PROJECT;

import java.text.MessageFormat;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.external.ActivityLog;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Task;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.service.sse.ReactiveLogReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 
 * <p>
 * Controller in charge the collection of tasks registering current operations active for a project.<br/>
 * This collection is handled by the bean {@link AsyncTask}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
@Api(
	tags="Project Tasks controller API",
	description = "API endpoints of the asynchronous stream report."
)
public class ProjectTasksController {

	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * Asynchronous tasks list.
	 */
	@Autowired
	AsyncTask tasks;
	
	@Autowired
	ReactiveLogReport logReport;
	
	/**
	 * Read, and return the active task for a project.
	 * 
	 * @param idProject the project identifier
	 * @param operation the operation such as {@link Global#DASHBOARD_GENERATION}
	 * 
	 * @return the HTTP Response with the retrieved project, or an empty one if the query failed.
	 */
	@ResponseBody
	@ApiOperation(
		value = "Read and return the active task of a project operation.",
		notes = "The Tasks-handler manages multiple concurrent operations for multiple projects. " +
				"One task represents one occurence of an operation. e.g. it contains the progression percentage in the operation."
	)
	@GetMapping(value = "/{idProject}/tasks/{operation}")
	public Task readTask(
			@PathVariable("idProject") int idProject,
			@PathVariable("operation") String operation) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("GET command on /api/project/%d/task/%s", idProject, operation)); 
		}

		Project project = projectHandler.find(idProject);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Project %s", project.getName())); 
		}
			
		Task task = tasks.getTask(operation, PROJECT, idProject);
		if (task == null) {
			throw new NotFoundException(CODE_TASK_NOT_FOUND, MessageFormat.format(MESSAGE_TASK_NOT_FOUND, operation, idProject));
		}

		return task;
	}

	/**
	 * Emit distinct {@link ActivityLog} every second.
	 * 
	 * @param idProject the project identifier
	 * @param operation the current underlying operation
	 * @return a flux of {@link ActivityLog}
	 */
	@ApiOperation(
		value = "Emit a distinct Activity log every second."
	)
	@GetMapping(value = "/{idProject}/tasks/stream/{operation}", produces= {MediaType.TEXT_EVENT_STREAM_VALUE})
	public Flux<ActivityLog> emitTaskLog(@PathVariable("idProject") int idProject, @PathVariable("operation") String operation) {	    
		return this.logReport.sunburstGenerationLogNext(operation, idProject);
	}

}