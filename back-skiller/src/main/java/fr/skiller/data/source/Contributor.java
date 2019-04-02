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
	private int idStaff;
	
	/**
	 * Developer's first commit
	 */
	private Date firstCommit;
	
	/**
	 * Developer's last commit
	 */
	private Date lastCommit;

	/**
	 * Number of commits submitted by this developer
	 */
	private int numberOfCommitsSubmitted;
	
	/**
	 * Number of files modified
	 */
	private int numberOfFiles;

	/**
	 * @param idStaff developer identifier
	 * @param firstCommit date of first commit
	 * @param lastCommit date of last commit
	 * @param numberOfCommitsSubmitted number of commits submitted
	 * @param numberOfFiles number of files
	 */
	public Contributor(int idStaff, Date firstCommit, Date lastCommit, int numberOfCommitsSubmitted, int numberOfFiles) {
		super();
		this.setIdStaff(idStaff);
		this.setFirstCommit(firstCommit);
		this.setLastCommit(lastCommit);
		this.setNumberOfCommitsSubmitted(numberOfCommitsSubmitted);
		this.setNumberOfFiles(numberOfFiles);
	}

	@Override
	public String toString() {
		return "Contributor [idStaff=" + getIdStaff() + ", firstCommit=" + getFirstCommit() + ", lastCommit=" + getLastCommit()
				+ ", numberOfCommitsSubmitted=" + getNumberOfCommitsSubmitted() + ", numberOfFiles=" + getNumberOfFiles() + "]";
	}

	/**
	 * @return the staff identifier
	 */
	public int getIdStaff() {
		return idStaff;
	}

	/**
	 * @param idStaff the Staff identifier to set
	 */
	public void setIdStaff(int idStaff) {
		this.idStaff = idStaff;
	}

	/**
	 * @return the firstCommit
	 */
	public Date getFirstCommit() {
		return firstCommit;
	}

	/**
	 * @param firstCommit developer's date of first Commit
	 */
	public void setFirstCommit(Date firstCommit) {
		this.firstCommit = firstCommit;
	}

	/**
	 * @return the developer's date of last Commit
	 */
	public Date getLastCommit() {
		return lastCommit;
	}

	/**
	 * @param developer's date of last Commit the lastCommit to set
	 */
	public void setLastCommit(Date lastCommit) {
		this.lastCommit = lastCommit;
	}

	/**
	 * @return the numberOfCommitsSubmitted
	 */
	public int getNumberOfCommitsSubmitted() {
		return numberOfCommitsSubmitted;
	}

	/**
	 * @param numberOfCommitsSubmitted the numberOfCommitsSubmitted to set
	 */
	public void setNumberOfCommitsSubmitted(int numberOfCommitsSubmitted) {
		this.numberOfCommitsSubmitted = numberOfCommitsSubmitted;
	}

	/**
	 * @return the number of files
	 */
	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	/**
	 * @param numberOfFiles the number Of files to set
	 */
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	
}
