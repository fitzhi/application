package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * This object represents a skill detected in a project.
 * </p>
 * <p>
 * <font color="red">The declared method {@code equals} is limited to the property {@code idSkill}.</font>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@EqualsAndHashCode
public @Data class ProjectSkill implements Serializable {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3208936902650229878L;

	/**
	 * The skill identifier
	 */
	private int idSkill;
	
	/**
	 * The number of files detected for this skill.
	 */
	private int numberOfFiles = 0;

	/**
	 * The size of all files.
	 */
	private long totalFilesSize = 0;
	
	/**
	 * Empty constructor for serialization purpose.
	 */
	public ProjectSkill() {
	}
	
	/**
	 * Public construction.
	 * @param idSkill the skill identifier
	 */
	public ProjectSkill(int idSkill) {
		this.idSkill = idSkill;
	}
	
	/**
	 * Public construction.
	 * @param idSkill the skill identifier
	 * @param numberOfFiles number of files detected
	 * @param totalFilesSize the size of all files.
	 */
	public ProjectSkill(int idSkill, int numberOfFiles, long totalFilesSize) {
		this.idSkill = idSkill;
		this.numberOfFiles = numberOfFiles;
		this.totalFilesSize = totalFilesSize;
	}
	
	/**
	 * Increment by ONE the number of files detected for this skill
	 * @return the {@link #numberOfFiles}
	 */
	public int incNumberOfFiles( ) {
		return ++numberOfFiles;
	}
	
	/**
	 * Add the size of a file into the global files size.
	 * @return the {@link #totalFilesSize}
	 */
	public long addFileSize(long fileSize) {
		this.totalFilesSize += fileSize;
		return this.totalFilesSize;
	}
}
