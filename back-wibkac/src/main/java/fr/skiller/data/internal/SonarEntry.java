package fr.skiller.data.internal;

import lombok.Data;

/**
 * <p>
 * Class representing an entry of a project on Sonar
 *</p>
 * 
 * @author Frédéric VIDAL
 *
 */
public @Data class SonarEntry {

	/**
	 * Identifier of a Sonar entry
	 */
	String id;

	/**
	 * Name of a Sonar entry
	 */
	String name;
	
	public SonarEntry() {
		// Empty constructor for serialization / de-serialization purpose
	}

	/**
	 * Sonar entry.
	 * @param id identifier of the sonar project entry.
	 * @param name name of the sonar project entry.
	 */
	public SonarEntry(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
}
