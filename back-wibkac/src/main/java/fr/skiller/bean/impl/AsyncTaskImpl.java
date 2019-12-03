package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_MULTIPLE_TASK;
import static fr.skiller.Error.CODE_TASK_NOT_FOUND;
import static fr.skiller.Error.MESSAGE_MULTIPLE_TASK;
import static fr.skiller.Error.MESSAGE_TASK_NOT_FOUND;
import static fr.skiller.Global.LN;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import fr.skiller.bean.AsyncTask;
import fr.skiller.data.internal.Task;
import fr.skiller.data.internal.TaskLog;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

/**
 * Bean in charge of the collection containing all the active tasks.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service
@Scope("singleton")
public class AsyncTaskImpl implements AsyncTask {

	private final Map<String, Task> tasks = new HashMap<>();
	
	
	@Override
	public void addTask(String operation, String title, int id) throws SkillerException {
		
		Task t = new Task(operation, title, id);
		
		Task record = tasks.get(genKey(t));
		if ((record != null) && !record.isComplete()) {
			throw new SkillerException(CODE_MULTIPLE_TASK, MESSAGE_MULTIPLE_TASK);
		}
		
		t.setComplete(false);
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
		StringBuilder sb = new StringBuilder(LN);
		tasks.values().stream().forEach(task -> sb.append(task.toString()).append(LN));
		return sb.toString();
	}
	
	@Override
	public boolean logMessage(String operation, String title, int id, String message) {
		return logMessage(operation, title, id, -1, message);
	}
	
	@Override
	public boolean logMessage(String operation, String title, int id, int errorCode, String message) {
		Task task = getTask(operation, title, id);
		if (task != null) {
			task.getLogs().add(new TaskLog(errorCode, message));
			return true;
		} else {
			if (log.isWarnEnabled()) {
				log.warn(String.format("No more task '%d' for id %d", title, id));
			}
			return false;
		}		
	}
	
	@Override
	public void completeTask(String operation, String title, int id) throws SkillerException {
		Task task = getTask(operation, title, id);
		if (task == null) {
			throw new SkillerException(CODE_TASK_NOT_FOUND, String.format(MESSAGE_TASK_NOT_FOUND, operation, id));
		}
		task.setComplete(true);
	
		if (!task.getLogs().isEmpty()) {
			// We sort the logs saved for this task
			task.getLogs().sort(Comparator.comparing(TaskLog::getLogTime));
			
			// We affect the last log for this task
			task.setLastBreath(task.getLogs().get(task.getLogs().size()-1));
		
			// We clear the log
			task.getLogs().clear();
		}
		
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
