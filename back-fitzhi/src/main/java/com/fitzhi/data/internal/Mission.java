/**
 * 
 */
package com.fitzhi.data.internal;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * <p>
 * Mission of a developer inside a project.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Mission implements Serializable {

	/**
	 * For serialization purpose.
	 */
	private static final long serialVersionUID = 7291243543440703140L;

	/**
	 * The staff identifier.
	 */
	private int idStaff;
	
	/**
	 * The project identifier.
	 */
	private int idProject;

	/**
	 * The project name.
	 */
	private String name;
	
	/**
	 * Date of the first commit.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate firstCommit;

	/**
	 * Date of the latest commit.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate lastCommit;
	
	/**
	 * @return number of commit submitted by a developer inside the project.
	 */
	private int numberOfCommits;
	
	/**
	 * @return number of files modifier by a developer inside the project.
	 */
	private int numberOfFiles;

	/**
	 * Map of {@long StaffActivitySkill} representing activity of a developer during his mission, indexed by skill identifier.
	 */
	private Map<Integer, StaffActivitySkill> staffActivitySkill = new HashMap<>();	
	
	/**
	 * Empty constructor for (de)serialization usage.
	 */
	public Mission() {
	}
	
	/**
	 * @param idStaff staff identifier
	 * @param idProject the identifier of the project
	 * @param name the name of the project
	 * @param lastCommit the date/time of the last commit
	 * @param firstCommit the date/time of the last commit
	 * @param numberOfCommits the number of commits submitted
	 * @param numberOfFiles the number of files
	 */
	public Mission(final int idStaff, final int idProject, final String name, final LocalDate firstCommit, final LocalDate lastCommit, final int numberOfCommits, final int numberOfFiles) {
		this.idStaff = idStaff;
		this.idProject = idProject;
		this.name = name;
		this.lastCommit = lastCommit;
		this.firstCommit = firstCommit;
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}
	
	/**
	 * @param idProject the identifier of the project
	 * @param name the name of the project
	 */
	public Mission(final int idStaff, final int idProject, final String name) {
		this(idStaff, idProject, name, null, null, 0, 0);
	}


}
