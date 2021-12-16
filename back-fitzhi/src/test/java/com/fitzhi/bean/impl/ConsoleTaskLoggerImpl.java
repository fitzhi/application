package com.fitzhi.bean.impl;

import static com.fitzhi.Global.LN;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Task;
import com.fitzhi.exception.ApplicationException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Bean in charge of the collection containing all the active tasks.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service("Console")
@Primary
public class ConsoleTaskLoggerImpl implements AsyncTask {

	private final Map<String, Task> tasks = new HashMap<>();

	@Override
	public void addTask(String operation, String title, int id) throws ApplicationException {
		System.out.println(String.format("ADD %s %s for project ID %d", operation, title, id));
	}

	@Override
	public void removeTask(String operation, String title, int id) {
		System.out.println(String.format("REMOVE %s %s for project ID %d", operation, title, id));
	}

	@Override
	public boolean containsTask(String operation, String title, int id) {
		return true;
	}

	@Override
	public boolean hasActiveTask(String operation, String title, int id) {
		return true;
	}

	@Override
	public Task getTask(String operation, String title, int id) {
		return null;
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
							task.getCurrentProgressionPercentage())).append(LN);
				});
		return sb.toString();
	}

	@Override
	public boolean logMessage(String operation, String title, int id, String message, int progressionPercentage) {
		return logMessage(operation, title, id, 0, message, progressionPercentage);
	}

	@Override
	public boolean logMessage(String operation, String title, int id, int errorCode, String message,
			int progressionPercentage) {
		System.out.println(
				String.format("%s %s @ %d percents for project ID %d", operation, title, progressionPercentage, id));
		return true;
	}

	@Override
	public void completeTask(String operation, String title, int id) throws ApplicationException {
		System.out.println(String.format("COMPLETE %s %s for project ID %d", operation, title, id));
	}

	@Override
	public void completeTaskOnError(String operation, String title, int id) throws ApplicationException {
		System.out.println(String.format("ERROR in %s %s for project ID %d", operation, title, id));
	}

}
