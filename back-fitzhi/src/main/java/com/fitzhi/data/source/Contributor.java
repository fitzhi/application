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
public @Data class Contributor {

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
	private int numberOfCommitsSubmitted;
	
	/**
	 * Number of files modified
	 */
	private int numberOfFiles;

	/**
	 * Map of {@long StaffActivitySkill} indexed by skill identifier.
	 */
	private Map<Integer, StaffActivitySkill> staffActivitySkill = new HashMap<>();
	
	/**
	 * @param idStaff developer identifier
	 * @param firstCommit date of first commit
	 * @param lastCommit date of last commit
	 * @param numberOfCommitsSubmitted number of commits submitted
	 * @param numberOfFiles number of files
	 */
	public Contributor(int idStaff, LocalDate firstCommit, LocalDate lastCommit, int numberOfCommitsSubmitted, int numberOfFiles) {
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

}
