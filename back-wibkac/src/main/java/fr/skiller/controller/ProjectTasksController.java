package fr.skiller.controller;

import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.CODE_TASK_NOT_FOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_TASK_NOT_FOUND;
import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;
import static fr.skiller.Global.PROJECT;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.Global;
import fr.skiller.bean.AsyncTask;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.data.internal.Task;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

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
@RequestMapping("/api/project/tasks")
public class ProjectTasksController {

	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * Asynchronous tasks list.
	 */
	@Autowired
	AsyncTask tasks;
	
	/**
	 * Read, and return the active task for a project
	 * @param operation the operation such as {@link Global#DASHBOARD_GENERATION}
	 * @param idProject the project identifier
	 * @return the HTTP Response with the retrieved project, or an empty one if the query failed.
	 */
	@GetMapping(value = "/{operation}/{id}")
	public ResponseEntity<Task> readTask(@PathVariable("operation") String operation, @PathVariable("id") int idProject) {

		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"GET command on /task/%s/%d", operation, idProject)); 
		}
		try { 
			if (!projectHandler.containsProject(idProject)) {
				throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
			}
			
			Task task = tasks.getTask(operation, PROJECT, idProject);
			if (task == null) {
				headers.set(BACKEND_RETURN_CODE, String.valueOf(CODE_TASK_NOT_FOUND));
				headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(MESSAGE_TASK_NOT_FOUND, operation, idProject));
				return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);	
			}
			
			ResponseEntity<Task> response = new ResponseEntity<>(task, headers, HttpStatus.OK);
			if (log.isDebugEnabled()) {
				log.debug(
						String.format("Project corresponding to the id %d has returned %s", 
								idProject, response.getBody()));
			}
			return response;
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}

	
}