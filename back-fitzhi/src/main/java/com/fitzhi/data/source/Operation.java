package com.fitzhi.data.source;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.data.internal.Staff;

import lombok.Data;

/**
 * <p>
 * Operation occurred.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Operation {

	/**
	 * <p>
	 * Identifier of the committer. 2 values are possible :
	 * <ul>
	 * <li>a real {@link Staff#getIdStaff() idStaff} of a registered user.</li>
	 * <li>-1 if this contributor is unknown for Techxhì, and identified
	 * only by his GIT {@code authName}.</li>
	 * </ul>
	 * </p>
	 */
	private int idStaff;
	
	/**
	 * Author name
	 */
	private String authorName;

	/**
	 * Author email
	 */
	private String authorEmail;

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
	public Operation(int idStaff, String authorName, String authorEmail, LocalDate dateCommit) {
		super();
		this.idStaff = idStaff;
		this.authorName = authorName;
		this.authorEmail = authorEmail;
		this.dateCommit = dateCommit;
	}
	
	/**
	 * <p>
	 * Generate a unique identifier base on the {@code idStaff} and the {@code dateCommit}.<br/>
	 * This method has been implemented at the beginning for the need of 
	 * {@link ProjectDashboardCustomizer#takeInAccountNewStaff(com.fitzhi.data.internal.Project, Staff)}
	 * </p>
	 * @return the searched identifier
	 */
	public String generateIdDataKey() {
		return getIdStaff() + "@" + getDateCommit();
	}
	
}
