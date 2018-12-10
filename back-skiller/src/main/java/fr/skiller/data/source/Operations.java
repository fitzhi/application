/**
 * 
 */
package fr.skiller.data.source;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Map of timestamped commit on a project repository  
 */
public class Operations {

	final int idProject;
	
	final Map<String, Operation> operations = new HashMap<String, Operation>();

	/**
	 * @param idProject project identifier of this collection of commits.
	 */
	public Operations(int idProject) {
		super();
		this.idProject = idProject;
	}

	/**
	 * Add a commit log in the repository
	 * @param user the user in charge of the commit
	 * @param dateCommit the date of the commit
	 */
	void addCommit (final String user, final Date dateCommit) {
		Operation operation = operations.get(user);
		if (operation == null) {
			operations.put(user, new Operation(user, dateCommit));
		} else {
			if (dateCommit.after(operation.dateCommit)) {
				operation.dateCommit = dateCommit;
			}
		}
	}
}
