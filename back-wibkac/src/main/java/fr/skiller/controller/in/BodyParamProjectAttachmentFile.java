package fr.skiller.controller.in;

import fr.skiller.controller.ProjectAuditController;
import fr.skiller.data.internal.AttachmentFile;
import lombok.Data;

/**
 * <p>
 * Parameters passed to the controller method 
 * {@link ProjectAuditController#saveAttachmentFile(BodyParamProjectAttachmentFile)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class BodyParamProjectAttachmentFile {

	/**
	 * The given project identifier
	 */
	private int idProject;

	/**
	 * The given topic identifier
	 */
	private int idTopic;
	
	/**
	 * The attachment file on-boarded in the envelop.
	 */
	private AttachmentFile attachmentFile;
	
}
