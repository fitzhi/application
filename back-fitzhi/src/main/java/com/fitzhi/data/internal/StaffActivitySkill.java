package com.fitzhi.data.internal;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

/**
 * <p>
 * This class represents the activity of a staff member in a skill
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class StaffActivitySkill implements Serializable {

	/**
	 * serialVersionUID for serialization.
	 */
	private static final long serialVersionUID = 204675087858400817L;

	/**
	 * Staff identifier, most probably a developer.
	 */
	private int idStaff;
	
	/**
	 * Skill identifier
	 */
	private int idSkill;
	
	/**
	 * Date of the first commit of a developer for the given skill. 
	 */
	private LocalDate firstCommit;

	/**
	 * Date of latest commit of the developer for the given skill
	 */
	private LocalDate lastCommit;

	/**
	 * Number of changes of changes submitted for this skill
	 */
	private int numberOfChanges = 0;

	/**
	* Empty constructor for serialization purpose.
	*/
	public StaffActivitySkill() {
		
	}
	
	/**
	 * Public construction
	 * @param idSkill the Skill identifier
	 * @param idStaff the Staff identifier
	 * @param firstCommit Date of <b>first</b> commit
	 * @param lastCommit Date of <b>last</b> commit
	 * @param numberOfChanges number of changes detected
	 */
	public StaffActivitySkill(int idSkill, int idStaff, LocalDate firstCommit, LocalDate lastCommit,
			int numberOfChanges) {
		super();
		this.idSkill = idSkill;
		this.idStaff = idStaff;
		this.firstCommit = firstCommit;
		this.lastCommit = lastCommit;
		this.numberOfChanges = numberOfChanges;
	}
	
	/**
	 * Increment the number of changes submitted for this skill
	 * @return the new changes count
	 */
	public int incChange() {
		return ++numberOfChanges;
	}

	/**
	 * <p>
	 * Take in account a date of commit.
	 * </p>
	 * @param dateCommit a date of commit to compare with the <b>first</b> and <b>last</b> commit for this skill 
	 */
	public void takeInAccount(LocalDate dateCommit) {
		if (dateCommit.isAfter(lastCommit)) {
			lastCommit = dateCommit;
		} else {
			if (dateCommit.isBefore(firstCommit)) {
				firstCommit = dateCommit;
			}
		}
	}

	@Override
	public String toString() {
		return "StaffActivitySkill [idStaff=" + idStaff + ", idSkill=" + idSkill + ", firstCommit=" + firstCommit
				+ ", lastCommit=" + lastCommit + ", numberOfChanges=" + numberOfChanges + "]";
	}

}
