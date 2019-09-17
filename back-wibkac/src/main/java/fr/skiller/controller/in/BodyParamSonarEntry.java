package fr.skiller.controller.in;

import fr.skiller.data.internal.SonarProject;
import lombok.Data;

/**
 * <p>
 * Body of data containing parameters to a HTTP Post call.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamSonarEntry {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * A project declared in Sonar and linked to a glozhi project
	 */
	private SonarProject sonarProject;
	
	/**
	 * Empty constructor.
	 */
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
		this.sonarProject = sonarEntry;
	}
	
}
