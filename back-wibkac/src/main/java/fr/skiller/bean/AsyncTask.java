/**
 * 
 */
package fr.skiller.bean;

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
	 * @throws SkillerException thrown if a problem occurs. 
	 * The most probable exception is if the task already 
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
	 * Test the presence of this operation in the tasks collection.
	 * @param operation type of operation recorded
	 * @param title title related to the identifier
	 * @param id identifier of the project
	 * @return {@code true} if this operation is already present in this collection, {@code false} otherwise
	 */
	boolean containsTask(String operation, String title, int id);
	
	/**
	 * Trace the content of the collection.
	 * @return the content of the collection if String format.
	 */
	String trace();
}
