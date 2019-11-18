package fr.skiller.data.internal;

import lombok.Data;

/**
 * <p>
 * This class contains all information regarding a topic of an audit of the project
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class AuditTopic {
	
	/**
	 * Topic identifier involved in the audit.
	 */
	int id;

	/**
	 * Empty constructor for <u>serialization</u> / <u>de-serialization</u> purpose
	 */
	public AuditTopic() {
		// Empty constructor for serialization / de-serialization purpose
	}

	/**
	 * AuditTopic constructor.
	 * @param key identifier of the sonar project entry.
	 */
	public AuditTopic(int idTopic) {
		this.id = idTopic;
	}
	
}
