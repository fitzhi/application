package fr.skiller.data.source;

import java.util.Date;

public class Contributor {

	/**
	 * Staff member identifier
	 */
	public int idStaff;
	
	/**
	 * last commit of the developer.
	 */
	public Date lastCommit;
	
	/**
	 * Number of commits submitted by this developer
	 */
	public int numberOfCommitsSubmitted;
	
	/**
	 * Number of files modified
	 */
	public int numberOfFiles;

	/**
	 * @param idStaff developer identifier
	 * @param lastCommit date of last commit
	 * @param numberOfCommitsSubmitted number of commits submitted
	 * @param numberOfFiles number of files
	 */
	public Contributor(int idStaff, Date lastCommit, int numberOfCommitsSubmitted, int numberOfFiles) {
		super();
		this.idStaff = idStaff;
		this.lastCommit = lastCommit;
		this.numberOfCommitsSubmitted = numberOfCommitsSubmitted;
		this.numberOfFiles = numberOfFiles;
	}
	
	
}
