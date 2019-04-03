package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_MULTIPLE_TASK;
import static fr.skiller.Error.MESSAGE_MULTIPLE_TASK;
import static fr.skiller.Global.LN;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Generated;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import fr.skiller.bean.AsyncTask;
import fr.skiller.exception.SkillerException;

class Task {
	/**
	 * Type of operation
	 */
	String operation;
	/**
	 * Title corresponding the entity id
	 */
	String title;
	/**
	 * Identifier. It might be a project, a staff, a skill...
	 */
	int id;
	
	/**
	 * @param operation Type of operation
	 * @param tile Title corresponding the entity identifier
	 * @param id Identifier. It might be a project, a staff, a skill...
	 */
	public Task(final String operation, final String title, final int id) {
		super();
		this.operation = operation;
		this.title = title;
		this.id = id;
	}

	@Override
	@Generated ("eclipse")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}


	@Override
	@Generated ("eclipse")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (id != other.id)
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else {
			if (!operation.equals(other.operation))
				return false;
		}
		if (title == null) {
			if (other.title != null)
				return false;
		} else {
			if (!title.equals(other.title))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Task [operation=" + operation + ", title=" + title + ", id=" + id + "]";
	}
	
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
