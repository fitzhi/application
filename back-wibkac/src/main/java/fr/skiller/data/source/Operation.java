package fr.skiller.data.source;

import java.time.LocalDate;

import lombok.Data;

/**
 * <p>
 * Operation occurred.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Operation {

	/**
	 * Identifier of the committer
	 */
	public int idStaff;
	
	/**
	 * Author name
	 */
	private String authorName;

	/**
	 * last of date for this commit. 
	 */
	private LocalDate dateCommit;

	/**
	 * Empty construction for serialization purpose.
	 */
	public Operation() {
	}

	/**
	 * Public construction.
	 * @param idStaff Staff identifier
	 * @param authorName the author's name
	 * @param dateCommit the date of commit
	 */
	public Operation(int idStaff, String authorName, LocalDate dateCommit) {
		super();
		this.idStaff = idStaff;
		this.authorName = authorName;
		this.dateCommit = dateCommit;
	}
	
	
}
