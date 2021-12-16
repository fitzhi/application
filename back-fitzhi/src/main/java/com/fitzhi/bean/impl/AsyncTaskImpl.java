package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_MULTIPLE_TASK;
import static com.fitzhi.Error.MESSAGE_MULTIPLE_TASK;
import static com.fitzhi.Error.MESSAGE_TASK_NOT_FOUND;
import static com.fitzhi.Global.LN;
import static com.fitzhi.Global.NO_PROGRESSION;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Task;
import com.fitzhi.data.internal.TaskLog;
import com.fitzhi.exception.ApplicationException;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Bean in charge of the collection containing all the active tasks.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service("default")
public class AsyncTaskImpl implements AsyncTask {

	private final Map<String, Task> tasks = new HashMap<>();
	
	@Override
	public void addTask(String operation, String title, int id) throws ApplicationException {
		
		Task t = new Task(operation, title, id);
		
		Task record = tasks.get(genKey(t));
		if ((record != null) && !record.isComplete() && !record.isCompleteOnError()) {
			log.error(String.format("addTask(\"%s\", \"%s\", %d) : a record already exists.", operation, title, id));
			throw new ApplicationException(CODE_MULTIPLE_TASK, MESSAGE_MULTIPLE_TASK);
		}
		
		t.setComplete(false);
		t.setCompleteOnError(false);
		t.setLastBreath(null);
		
		tasks.put(genKey(t), t);
	}

	@Override
	public void removeTask(String operation, String title, int id) {
		Task t = new Task(operation, title, id);
		tasks.remove(genKey(t));
	}

	@Override
	public boolean containsTask(String operation, String title, int id) {
		Task t = new Task(operation, title, id);
		return tasks.containsKey(genKey(t));
	}

	
	@Override
	public boolean hasActiveTask(String operation, String title, int id) {
		Task task = getTask(operation, title, id);
		return (task != null) && !task.isComplete();
	}

	@Override
	public Task getTask(String operation, String title, int id) {
		Task t = new Task(operation, title, id);
		return tasks.get(genKey(t));
	}
	
	@Override
	public String trace() {
		StringBuilder sb = new StringBuilder();
		tasks.values().stream()
			.filter(t -> !t.isComplete())
			.sorted(Comparator.comparingInt(Task::getId))
			.forEach(task -> {
				sb.append(String.format("%s %s %d %d%%", 
					task.getOperation(), 
					task.getTitle(), 
					task.getId(), 
					task.getCurrentProgressionPercentage()) ).append(LN);
			});
		return sb.toString();
	}
	
	@Override
	public boolean logMessage(String operation, String title, int id, String message, int progressionPercentage) {
		return logMessage(operation, title, id, 0, message, progressionPercentage);
	}
	
	@Override
	public boolean logMessage(String operation, String title, int id, int errorCode, String message, int progressionPercentage) {
		Task task = getTask(operation, title, id);
		if (task != null) {
			if (progressionPercentage != NO_PROGRESSION) {
				task.setCurrentProgressionPercentage(progressionPercentage);
			}
			task.addActivity(new TaskLog(errorCode, message, task.getCurrentProgressionPercentage()));
			return true;
		} else {
			if (log.isWarnEnabled()) {
				log.warn("logMessage failed", new Exception(String.format("Task '%s' is not found for %s %d", operation, title, id)));
			}
			return false;
		}		
	}
	
	/**
	 * Complete task with or without error, called by
	 * <ul>
	 * <li>
	 * {@link #completeTask(String, String, int)}
	 * </li>
	 * <li>
	 * {@link #completeTaskOnError(String, String, int)}
	 * </li>
	 * </ul>
	 */
	private void completeTask(String operation, String title, int id, boolean successful) throws ApplicationException {
		Task task = getTask(operation, title, id);
		if (task == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format(MESSAGE_TASK_NOT_FOUND, operation, id));
			}
		} else {
			task.complete(successful);				
		}
	}
	
	@Override
	public void completeTask(String operation, String title, int id) throws ApplicationException {
		this.completeTask(operation, title, id, true);
	}

	@Override
	public void completeTaskOnError(String operation, String title, int id) throws ApplicationException {
		this.completeTask(operation, title, id, false);
	}

	/**
	 * Generate the key associated to this task.
	 * @param task the given task
	 * @return an unique identifier corresponding to this task, to used as a key for the tasks collection
	 */
	private String genKey(Task task) {
		return task.getOperation() + "@" + task.getId();
	}
	
}
