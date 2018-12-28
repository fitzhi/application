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
	 * @param author the author in charge of the commit
	 * @param email the author's email
	 * @param dateCommit the date of the commit
	 */
	void addCommit (final String author, final String email, final Date dateCommit) {
		Operation operation = operations.get(author);
		if (operation == null) {
			operations.put(author, new Operation(author, email, dateCommit));
		} else {
			if (dateCommit.after(operation.dateCommit)) {
				operation.dateCommit = dateCommit;
			}
		}
	}
}
