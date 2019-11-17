package fr.skiller.data.internal;

import java.util.ArrayList;

import lombok.Data;

/**
 * <p>
 * This class contains all information regarding a topic of an audit of the project
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class AuditProject {
	
	/**
	 * Topic identifier involved in the audit.
	 */
	int idTopic;

	/**
	 * Empty constructor for <u>serialization</u> / <u>de-serialization</u> purpose
	 */
	public AuditProject() {
		// Empty constructor for serialization / de-serialization purpose
	}

	/**
	 * AuditProject constructor.
	 * @param key identifier of the sonar project entry.
	 */
	public AuditProject(int idTopic) {
		this.idTopic = idTopic;
	}
	
}
