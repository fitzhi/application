package fr.skiller.bean;

import fr.skiller.data.internal.AuditTopic;
import fr.skiller.exception.SkillerException;

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
	
}
