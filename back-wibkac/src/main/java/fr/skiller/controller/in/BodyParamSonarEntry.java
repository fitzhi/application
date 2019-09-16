package fr.skiller.controller.in;

import fr.skiller.data.internal.SonarProject;
import lombok.Data;

public @Data class BodyParamSonarEntry {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * A project declared in Sonar
	 */
	private SonarProject sonarEntry;
	
	public BodyParamSonarEntry() {
		// Empty constructor declared for serialization / deserialization purpose 		
	}

	/**
	 * @param idProject the project identifier
	 * @param sonarEntry the entry
	 */
	public BodyParamSonarEntry(int idProject, SonarProject sonarEntry) {
		super();
		this.idProject = idProject;
		this.sonarEntry = sonarEntry;
	}
	
}
