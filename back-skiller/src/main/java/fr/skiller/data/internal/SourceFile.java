package fr.skiller.data.internal;

import java.util.Date;

public class SourceFile {

	/**
	 * File name of the source file
	 */
	public String filename;
	
	/**
	 * last date of commit for this filename
	 */
	public Date lastCommit;
	
	/**
	 * Array of staff identifiers who are committed to this file. 
	 */
	public int[] idStaffs;

	/**
	 * @param filename File name of the source file.
	 * @param lastCommit Last date of commit for this filename.
	 * @param idStaffs Array of staff identifiers who are committed to this file.
	 */
	public SourceFile(String filename, Date lastCommit, int[] idStaffs) {
		super();
		this.filename = filename;
		this.lastCommit = lastCommit;
		this.idStaffs = idStaffs;
	}
	
}
