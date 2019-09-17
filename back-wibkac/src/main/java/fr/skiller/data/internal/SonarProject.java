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
public @Data class SonarProject {

	/**
	 * Key of a Sonar entry
	 */
	String key;

	/**
	 * Name of a Sonar entry
	 */
	String name;
	
	public SonarProject() {
		// Empty constructor for serialization / de-serialization purpose
	}

	/**
	 * Sonar entry.
	 * @param key identifier of the sonar project entry.
	 * @param name name of the sonar project entry.
	 */
	public SonarProject(String key, String name) {
		super();
		this.key = key;
		this.name = name;
	}
	
}
