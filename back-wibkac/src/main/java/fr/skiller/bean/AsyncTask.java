/**
 * 
 */
package fr.skiller.bean;

import fr.skiller.data.internal.Task;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Interface in charge of storing an asynchronous task, to prevent multiple launch.
 */
public interface AsyncTask {

	/**
	 * Add a task in the asynchronous active tasks collection.
	 * @param operation type of operation recorded
	 * @param title title related to the id
	 * @param id identifier of the project
	 * @throws SkillerException thrown if any problem occurs. 
	 * <i>The most probable exception is if the task already.</i>
	 */
	void addTask (String operation, String title, int id) throws SkillerException;

	/**
	 * Remove a task in the asynchronous active tasks collection.
	 * @param operation type of operation recorded
	 * @param title title related to the id
	 * @param id identifier of the project
	 */
	void removeTask (String operation, String title, int id);
	
	/**
	 * Test the presence of a given operation in the tasks collection.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id identifier of the project
	 * @return {@code true} if this operation is already present in this collection, {@code false} otherwise
	 */
	boolean containsTask(String operation, String title, int id);
	
	/**
	 * Test the presence of an active operation in the tasks collection.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id identifier of the project
	 * @return {@code true} if this operation is already active in this collection, {@code false} otherwise
	 */
	boolean hasActiveTask(String operation, String title, int id);
	
	/**
	 * Test the presence of a given operation in the tasks collection.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id the given identifier <i>(might be a project, a staff or anything else)</i>
	 * @return the task stored for this operation on this project, or {@code null} if none is found
	 */
	Task getTask(String operation, String title, int id);
	
	/**
	 * Log a message for a running task.<br/>
	 * <font color="chocolate">The Log message will obtain an error code equal to -1</font>
	 * @param operation type of operation concerned
	 * @param title the title related to the identifier
	 * @param id the given identifier <i>(which might be a project, a staff or anything else)</i>
	 * @param message the given log message to log
	 * @return {@code true} id the log has been successfully recorded, {@code false} otherwise.
	 */
	boolean logMessage(String operation, String title, int id, String message);
	
	/**
	 * Log a message for a running task.
	 * @param operation type of operation concerned
	 * @param title the title related to the identifier
	 * @param id the given identifier <i>(which might be a project, a staff or anything else)</i>
	 * @param errorCode error code associated with this message
	 * @param message the given log message to log
	 * @return {@code true} id the log has been successfully recorded, {@code false} otherwise.
	 */
	boolean logMessage(String operation, String title, int id, int errorCode, String message);
	
	/**
	 * Complete the current task without error.
	 * @param operation type of operation recorded
	 * @param title title related to the id
	 * @param id identifier of the entity
	 * @throws SkillerException thrown if any problem occurs. 
	 * <i>The most probable exception is that the task does not exist.</i>
	 */
	void completeTask (String operation, String title, int id) throws SkillerException;
	
	/**
	 * Complete the current task <font color="red">ON AN ERROR</font>
	 * @param operation type of operation recorded
	 * @param title title related to the id
	 * @param id identifier of the entity
	 * @throws SkillerException thrown if any problem occurs. 
	 * <i>The most probable exception is that the task does not exist.</i>
	 */
	void completeTaskOnError (String operation, String title, int id) throws SkillerException;
	
	/**
	 * Trace the content of the collection.
	 * @return the content of the collection if String format.
	 */
	String trace();
}
