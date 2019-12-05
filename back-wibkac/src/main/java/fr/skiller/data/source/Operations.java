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
	
	final Map<String, Operation> operations = new HashMap<>();

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
	 * @param authorName the author's name
	 * @param dateCommit the date of the commit
	 */
	void addCommit (final int idStaff, String authorName, final LocalDate dateCommit) {
		
		String key = (idStaff > 0) ? String.valueOf(idStaff) : authorName;
		
		Operation operation = operations.get(key);
		if (operation == null) {
			operations.put(key, new Operation(idStaff, authorName, dateCommit));
		} else {
			if (dateCommit.isAfter(operation.getDateCommit())) {
				operation.setDateCommit(dateCommit);
			}
		}
	}
}
