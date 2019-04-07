package fr.skiller.data.internal;

import java.util.Arrays;
import java.util.Date;

public class SourceFile {

	/**
	 * File name of the source file
	 */
	private String filename;
	
	/**
	 * last date of commit for this filename
	 */
	private Date lastCommit;
	
	/**
	 * Array of staff identifiers who are committed to this file. 
	 */
	private int[] idStaffs;

	/**
	 * @param filename File name of the source file.
	 * @param lastCommit Last date of commit for this filename.
	 * @param idStaffs Array of staff identifiers who are committed to this file.
	 */
	public SourceFile(String filename, Date lastCommit, int[] idStaffs) {
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
	public Date getLastCommit() {
		return lastCommit;
	}

	/**
	 * @param the date of the last Commit for this source file.
	 */
	public void setLastCommit(Date lastCommit) {
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
	
}
