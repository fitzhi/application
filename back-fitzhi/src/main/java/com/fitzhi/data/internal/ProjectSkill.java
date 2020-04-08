/**
 * 
 */
package com.fitzhi.data.internal;

import java.io.Serializable;

import javax.annotation.Generated;

import lombok.Data;

/**
 * <p>
 * This object represents a skill detected in a project.
 * </p>
 * <p>
 * <font color="red">The declared method {@code equals} is limited to the property {@code idSkill}.</font>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
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
	
	@Generated ("eclipse")	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectSkill other = (ProjectSkill) obj;
		if (idSkill != other.idSkill)
			return false;
		return true;
	}

	@Generated ("eclipse")	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idSkill;
		return result;
	}

}
