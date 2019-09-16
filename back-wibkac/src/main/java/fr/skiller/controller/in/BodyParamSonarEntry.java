package fr.skiller.controller.in;

import fr.skiller.data.internal.SonarEntry;
import lombok.Data;

public @Data class BodyParamSonarEntry {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * A project declared in Sonar
	 */
	private SonarEntry sonarEntry;
	
	public BodyParamSonarEntry() {
		// Empty constructor declared for serialization / deserialization purpose 		
	}

	/**
	 * @param idProject the project identifier
	 * @param sonarEntry the entry
	 */
	public BodyParamSonarEntry(int idProject, SonarEntry sonarEntry) {
		super();
		this.idProject = idProject;
		this.sonarEntry = sonarEntry;
	}
	
}
