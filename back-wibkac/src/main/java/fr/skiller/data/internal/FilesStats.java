package fr.skiller.data.internal;

import lombok.Data;

/**
 * <p>
 * Source files statistics per language. 
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class FilesStats {

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
