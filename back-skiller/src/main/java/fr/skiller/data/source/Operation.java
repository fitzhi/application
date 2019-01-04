package fr.skiller.data.source;

import java.time.LocalDate;
import java.util.Date;

public class Operation {

	/**
	 *identifier of the committer
	 */
	public final int idStaff;
	
	/**
	 * last of date for this commit. 
	 */
	public Date dateCommit;

	/**
	 * @param int idStaff Staff member identifier
	 * @param dateCommit date of commit
	 */
	public Operation(final int idStaff, Date dateCommit) {
		super();
		this.idStaff = idStaff;
		this.dateCommit = dateCommit;
	}

	
}
