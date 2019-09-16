package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_MULTIPLE_TASK;
import static fr.skiller.Error.MESSAGE_MULTIPLE_TASK;
import static fr.skiller.Global.LN;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import fr.skiller.bean.AsyncTask;
import fr.skiller.exception.SkillerException;
import lombok.Data;

@Data class Task {
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


}

/**
 * Bean in charge of the collection containing all the active tasks.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Scope("singleton")
public class AsyncTaskImpl implements AsyncTask {

	private final Set<Task> tasks = new HashSet<>();
	
	@Override
	public void addTask(String operation, String title, int id) throws SkillerException {
		
		Task t = new Task(operation, title, id);
		if (tasks.contains(t)) {
			throw new SkillerException(CODE_MULTIPLE_TASK, MESSAGE_MULTIPLE_TASK);
		}
		tasks.add(t);
	}

	@Override
	public void removeTask(String operation, String title, int id) {
		Task t = new Task(operation, title, id);
		tasks.remove(t);
	}

	@Override
	public boolean containsTask(String operation, String title, int id) {
		Task t = new Task(operation, title, id);
		return tasks.contains(t);
	}

	@Override
	public String trace() {
		StringBuilder sb = new StringBuilder(LN);
		tasks.stream().forEach(task -> sb.append(task.toString()).append(LN));
		return sb.toString();
	}
	
}
