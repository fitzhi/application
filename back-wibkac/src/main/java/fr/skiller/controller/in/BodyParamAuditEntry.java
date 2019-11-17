package fr.skiller.controller.in;

import fr.skiller.data.internal.AuditTopic;
import lombok.Data;

/**
 * <p>
 * Body of data containing parameters to a HTTP Post call.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamAuditEntry {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * A project declared in Audit and linked to a Techxhì project
	 */
	private AuditTopic auditTopic;
	
	/**
	 * Empty constructor.
	 */
	public BodyParamAuditEntry() {
		// Empty constructor declared for serialization / de-serialization purpose 		
	}

	/**
	 * @param idProject the project identifier
	 * @param sonarEntry the entry
	 */
	public BodyParamAuditEntry(int idProject, AuditTopic auditProject) {
		this.idProject = idProject;
		this.auditTopic = auditTopic;
	}
	
}
