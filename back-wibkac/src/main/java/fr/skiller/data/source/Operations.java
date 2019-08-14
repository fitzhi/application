/**
 * 
 */
package fr.skiller.data.source;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Map of timestamped commit on a project repository  
 */
public class Operations {

	final int idProject;
	
	final Map<Integer, Operation> operations = new HashMap<Integer, Operation>();

	/**
	 * @param idProject project identifier of this collection of commits.
	 */
	public Operations(int idProject) {
		super();
		this.idProject = idProject;
	}

	/**
	 * Add a commit log in the repository
	 * @param author Staff member's identifier as the author in charge of the commit
	 * @param email the author's email
	 * @param dateCommit the date of the commit
	 */
	void addCommit (final int author, final LocalDate dateCommit) {
		Operation operation = operations.get(author);
		if (operation == null) {
			operations.put(author, new Operation(author, dateCommit));
		} else {
			if (dateCommit.isAfter(operation.getDateCommit())) {
				operation.setDateCommit(dateCommit);
			}
		}
	}
}
