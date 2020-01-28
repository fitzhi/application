package com.tixhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * Source files statistics per language. 
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class FilesStats implements Serializable {

	/**
	 * serialVersionUID for serialization purpose.
	 */
	private static final long serialVersionUID = 6937126988922326182L;

	private String language;
	
	private int numberOfFiles;

	/**
	 * Empty constructor for serialization purpose.
	 */
	public FilesStats() {
	}
	
	/**
	 * Constructor.
	 * @param language the language
	 * @param numberOfFiles the number of files for this language
	 */
	public FilesStats(String language, int numberOfFiles) {
		super();
		this.language = language;
		this.numberOfFiles = numberOfFiles;
	}
	
}
