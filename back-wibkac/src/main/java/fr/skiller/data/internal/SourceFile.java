package fr.skiller.data.internal;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

public class SourceFile implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2579860319574877821L;

	/**
	 * File name of the source file
	 */
	private String filename;
	
	/**
	 * last date of commit for this filename
	 */
	private LocalDate lastCommit;
	
	/**
	 * Array of staff identifiers who are committed to this file. 
	 */
	private int[] idStaffs;

	/**
	 * @param filename File name of the source file.
	 * @param lastCommit Last date of commit for this filename.
	 * @param idStaffs Array of staff identifiers who are committed to this file.
	 */
	public SourceFile(String filename, LocalDate lastCommit, int[] idStaffs) {
		super();
		this.setFilename(filename);
		this.setLastCommit(lastCommit);
		this.setIdStaffs(idStaffs);
	}

	@Override
	public String toString() {
		return "SourceFile [filename=" + getFilename() + ", lastCommit=" + getLastCommit() + ", idStaffs="
				+ Arrays.toString(getIdStaffs()) + "]";
	}

	/**
	 * @return the filename of the source file
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename of source file to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the date of last Commit for this source file.
	 */
	public LocalDate getLastCommit() {
		return lastCommit;
	}

	/**
	 * @param the date of the last Commit for this source file.
	 */
	public void setLastCommit(LocalDate lastCommit) {
		this.lastCommit = lastCommit;
	}

	/**
	 * @return the array of staff identifiers who have submitted commits. 
	 */
	public int[] getIdStaffs() {
		return idStaffs;
	}

	/**
	 * @param the array of staff identifiers who have submitted commits to set.
	 */
	public void setIdStaffs(int[] idStaffs) {
		this.idStaffs = idStaffs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + Arrays.hashCode(idStaffs);
		result = prime * result + ((lastCommit == null) ? 0 : lastCommit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceFile other = (SourceFile) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (!Arrays.equals(idStaffs, other.idStaffs))
			return false;
		if (lastCommit == null) {
			if (other.lastCommit != null)
				return false;
		} else if (!lastCommit.equals(other.lastCommit))
			return false;
		return true;
	}
	
}
