package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * Each task instance is representing a technical task running asynchronously in the JVM.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */

public @Data class Task {
	
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

	/**
	 * {@code true} if the task has been successfully completed, {@code false} otherwise.
	 */
	boolean complete;
	
	/**
	 * List of logs recorded by the asynchronous application.
	 */
	List<TaskLog> logs = new ArrayList<>();
	
	/**
	 * Last breath of the task before termination.
	 */
	TaskLog lastBreath;
}
