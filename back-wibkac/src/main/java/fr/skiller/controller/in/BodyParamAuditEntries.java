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
public @Data class BodyParamAuditEntries {

	/**
	 * Project identifier
	 */
	private int idProject;
	
	/**
	 * Array of Audit topics instances. <br/>
	 * Each instance of {@link AuditTopic} is used has a data envelope, and therefore is not complete. 
	 */
	private AuditTopic[] dataEnvelope;
	
	/**
	 * Empty constructor.
	 */
	public BodyParamAuditEntries() {
		// Empty constructor declared for serialization / de-serialization purpose 		
	}

	/**
	 * @param idProject the project identifier
	 * @param dataEnvelope containing an array of Audit topics instances
	 */
	public BodyParamAuditEntries(int idProject, AuditTopic[] dataEnvelope) {
		this.idProject = idProject;
		this.dataEnvelope = dataEnvelope;
	}
	
}
