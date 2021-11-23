package com.fitzhi.data.source;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.data.internal.StaffActivitySkill;

import lombok.Data;

/**
 * Class for the contributor inside the repository.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Contributor implements GitMetrics {

	/**
	 * Staff member identifier
	 */
	private int idStaff;
	
	/**
	 * Developer's first commit
	 */
	private LocalDate firstCommit;
	
	/**
	 * Developer's last commit
	 */
	private LocalDate lastCommit;

	/**
	 * Number of commits submitted by this developer
	 */
	private int numberOfCommits;
	
	/**
	 * Number of files modified
	 */
	private int numberOfFiles;

	/**
	 * Map of {@long StaffActivitySkill} indexed by skill identifier.
	 */
	private Map<Integer, StaffActivitySkill> staffActivitySkill = new HashMap<>();
	
	/**
	 * @param idStaff the given developer identifier
	 * @param firstCommit date of first commit
	 * @param lastCommit date of last commit
	 * @param numberOfCommits number of commits submitted
	 * @param numberOfFiles number of files
	 */
	public Contributor(int idStaff, LocalDate firstCommit, LocalDate lastCommit, int numberOfCommits, int numberOfFiles) {
		this.setIdStaff(idStaff);
		this.setFirstCommit(firstCommit);
		this.setLastCommit(lastCommit);
		this.setNumberOfCommits(numberOfCommits);
		this.setNumberOfFiles(numberOfFiles);
	}

	/**
	 * @param idStaff the given developer identifier
	 */
	public Contributor(int idStaff) {
		this(idStaff, LocalDate.EPOCH, LocalDate.EPOCH, 0, 0);
	}

	@Override
	public String toString() {
		return "Contributor [idStaff=" + getIdStaff() + ", firstCommit=" + getFirstCommit() + ", lastCommit=" + getLastCommit()
				+ ", numberOfCommits=" + getNumberOfCommits() + ", numberOfFiles=" + getNumberOfFiles() + "]";
	}

}
