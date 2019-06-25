package fr.skiller.data.source;

import java.time.LocalDate;
import java.util.Date;

/**
 * <p>Operation occured.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Operation {

	/**
	 *identifier of the committer
	 */
	public final int idStaff;
	
	/**
	 * last of date for this commit. 
	 */
	private LocalDate dateCommit;

	/**
	 * @param int idStaff Staff member identifier
	 * @param dateCommit date of commit
	 */
	public Operation(final int idStaff, LocalDate dateCommit) {
		super();
		this.idStaff = idStaff;
		this.setDateCommit(dateCommit);
	}

	/**
	 * @return the dateCommit
	 */
	public LocalDate getDateCommit() {
		return dateCommit;
	}

	/**
	 * @param dateCommit the dateCommit to set
	 */
	public void setDateCommit(LocalDate dateCommit) {
		this.dateCommit = dateCommit;
	}

	
}
