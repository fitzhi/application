package com.fitzhi.bean;

import java.util.List;

import com.fitzhi.data.internal.AttachmentFile;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.TopicWeight;
import com.fitzhi.exception.SkillerException;

/**
 * <p>
 * interface in charge of managing the audit information of a project.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface ProjectAuditHandler extends DataSaverLifeCycle {

	/**
	 * Add a topic in the Project audit.
	 * @param idProject the project identifier
	 * @param idTopic the given topic identifier
	 * @throws SkillerException thrown if any problem occurs such as <i>'project identifier unknown'</i>
	 */
	void addTopic(int idProject, int idTopic) throws SkillerException;
	
	/**
	 * Remove a topic from the Project audit. 
	 * @param idProject the project identifier
	 * @param idTopic the given topic identifier
	 * @param flagForce flag to force the removal of the topic, <b>event if this topic already contains audit data</b>. 
	 * If this flag is set to {@code true} all existing data for this topic will be removed.
	 * @throws SkillerException thrown if any problem occurs such as <i>'project identifier unknown'</i>
	 */
	void removeTopic(int idProject, int idTopic, boolean flagForce) throws SkillerException;
	
	/**
	 * Get a topic from the Project audit.
	 * @param idProject the project identifier
	 * @param idTopic the given topic identifier
	 * @return the audit project
	 * @throws SkillerException thrown if any problem occurs such as <i>'topic identifier unknown'</i>
	 */
	AuditTopic getTopic(int idProject, int idTopic) throws SkillerException;
	
	/**
	 * Set the evaluation attributed by an expert, for an audit topic 
	 * @param idProject the project identifier
	 * @param idTopic the given topic identifier
	 * @param evaluation the audit topic quality evaluation given by the expert.
	 * @throws SkillerException thrown if any problem occurs such as <i>'topic identifier unknown'</i>
	 */
	void saveEvaluation(int idProject, int idTopic, int evaluation) throws SkillerException;
	
	/**
	 * Save the executive summary given to this audit topic 
	 * @param idProject the project identifier
	 * @param idTopic the given topic identifier
	 * @param summaryReport the executive summary given to this audit topic
	 * @throws SkillerException thrown if any problem occurs such as <i>'topic identifier unknown'</i>
	 */
	void saveReport(int idProject, int idTopic, String executiveSummary) throws SkillerException;
	
	/**
	 * Set the weights for all topics in the project.<br/>
	 * The sum of these weights has to be equal to 100%.
	 * @param idProject the project identifier
	 * @param weights the list of {@link TopicWeight}
	 * @throws SkillerException thrown if any problem occurs such as <i>'topic identifier unknown'</i>
	 */
	void saveWeights(int idProject, List<TopicWeight> weights) throws SkillerException;

	/**
	 * Process and save the global evaluation given to the project, 
	 * based on the barycenter of subjects affected by their relative weight
	 * @param idProject the project identifier
	 * @throws SkillerException thrown if any problem occurs such as <i>'project identifier unknown'</i>
	 */
	void processAndSaveGlobalAuditEvaluation(int idProject) throws SkillerException;

	/**
	 * Add or update an attachment to the given topic.
	 * @param idProject the project identifier
	 * @param idTopic the topic identifier
	 * @param attachment the attachment to be added
	 * @throws SkillerException thrown if any problem occurs such as <i>'Project identifier unknown'</i>
	 */
	void updateAttachmentFile(int idProject, int idTopic, AttachmentFile attachment) throws SkillerException;

	/**
	 * Add or update an attachment to the given topic.
	 * @param idProject the project identifier
	 * @param idTopic the topic identifier
	 * @param idFileIdentifier the attachment file identifier within the topic
	 * @throws SkillerException thrown if any problem occurs such as <i>'Project identifier unknown'</i>
	 */
	void removeAttachmentFile(int idProject, int idTopic, int idFileIdentifier) throws SkillerException;

	/**
	 * Build the <b>unique</b> filename to store the attachment file on server.
	 * @param idProject the project identifier
	 * @param idTopic the topic identifier
	 * @param filename the original and local filename from the end-user desktop
	 * @return the <b>unique</b> filename to store the attachment file
	 */
	String buildAttachmentFileName(int idProject, int idTopic, String filename);
	
}