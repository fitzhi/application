package fr.skiller.data.source;

import java.util.Date;

/**
 * Class for the contributor inside the repository.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class Contributor {

	/**
	 * Staff member identifier
	 */
	public int idStaff;
	
	/**
	 * Developer's first commit
	 */
	public Date firstCommit;
	
	/**
	 * Developer's last commit
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
	 * @param firstCommit date of first commit
	 * @param lastCommit date of last commit
	 * @param numberOfCommitsSubmitted number of commits submitted
	 * @param numberOfFiles number of files
	 */
	public Contributor(int idStaff, Date firstCommit, Date lastCommit, int numberOfCommitsSubmitted, int numberOfFiles) {
		super();
		this.idStaff = idStaff;
		this.firstCommit = firstCommit;
		this.lastCommit = lastCommit;
		this.numberOfCommitsSubmitted = numberOfCommitsSubmitted;
		this.numberOfFiles = numberOfFiles;
	}

	@Override
	public String toString() {
		return "Contributor [idStaff=" + idStaff + ", firstCommit=" + firstCommit + ", lastCommit=" + lastCommit
				+ ", numberOfCommitsSubmitted=" + numberOfCommitsSubmitted + ", numberOfFiles=" + numberOfFiles + "]";
	}

	
}
