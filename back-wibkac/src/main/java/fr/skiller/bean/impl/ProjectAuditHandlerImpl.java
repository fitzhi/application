package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_CANNOT_RETRIEVE_ATTACHMENTFILE;
import static fr.skiller.Error.CODE_PROJECT_INVALID_WEIGHTS;
import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.CODE_PROJECT_TOPIC_UNKNOWN;
import static fr.skiller.Error.LIB_CANNOT_RETRIEVE_ATTACHMENTFILE;
import static fr.skiller.Error.MESSAGE_PROJECT_INVALID_WEIGHTS;
import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_TOPIC_UNKNOWN;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.ProjectAuditHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.data.internal.AttachmentFile;
import fr.skiller.data.internal.AuditTopic;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.TopicWeight;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.StorageService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Main implementation for the interface {@link ProjectAuditHandler}.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@Service
public class ProjectAuditHandlerImpl extends AbstractDataSaverLifeCycleImpl implements ProjectAuditHandler {

	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * Storage service dedicated to upload/download the audit attachments.
	 */
	@Autowired
	@Qualifier("Attachment")
    StorageService storageService;
	
	@Override
	public void addTopic(int idProject, int idTopic) throws SkillerException {
		final Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}

		if (project.getAudit() == null) {
			project.setAudit(new HashMap<Integer, AuditTopic>());
		}
		
		// We do not add this topic if it already exists.
		if (!project.getAudit().containsKey(idTopic)) {		
			
			synchronized (lockDataUpdated) {
				project.getAudit().put(idTopic, new AuditTopic(idTopic));
				this.dataUpdated = true;
			}

			/*
			 * After the addition of the audit, we share the weights between all topics declared
			 */
			synchronized (lockDataUpdated) {
				this.shareWeights(project);
				this.dataUpdated = true;
			}
			
			/*
			 * And we process the global evaluation impacted by the new repartition of weights
			 */
			processAndSaveGlobalAuditEvaluation(project.getId());
		}
	}

	@Override
	public void removeTopic(int idProject, int idTopic, boolean flagForce) throws SkillerException {

		if (flagForce) {
			throw new SkillerRuntimeException("The flagForce behavior is not implemented yet!");
		}
		final Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		synchronized (lockDataUpdated) {
			project.getAudit().remove(idTopic);
			this.dataUpdated = true;
		}

		/*
		 * After the addition of the audit, we share the weights between all topics declared
		 */
		synchronized (lockDataUpdated) {
			this.shareWeights(project);
			this.dataUpdated = true;
		}
		
		/*
		 * And we process the global evaluation impacted by the new repartition of weights
		 */
		processAndSaveGlobalAuditEvaluation(project.getId());
	}

	@Override
	public AuditTopic getTopic(int idProject, int idTopic) throws SkillerException {

		final Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		if (project.getAudit() == null) {
			throw new SkillerException(CODE_PROJECT_TOPIC_UNKNOWN, MessageFormat.format(MESSAGE_PROJECT_TOPIC_UNKNOWN, idTopic, project.getName()));
		}
		
		final AuditTopic auditProject = project.getAudit().get(idTopic);
		if (auditProject == null) {
			throw new SkillerException(CODE_PROJECT_TOPIC_UNKNOWN, MessageFormat.format(MESSAGE_PROJECT_TOPIC_UNKNOWN, idTopic, project.getName()));
		}
		
		return auditProject;
	}

	@Override
	public void saveEvaluation(int idProject, int idTopic, int evaluation) throws SkillerException {
		
		AuditTopic auditTopic = getTopic(idProject, idTopic);

		synchronized (lockDataUpdated) {
			auditTopic.setEvaluation(evaluation);
			this.dataUpdated = true;
		}
		
	}

	@Override
	public void saveReport(int idProject, int idTopic, String executiveSummary) throws SkillerException {

		AuditTopic auditTopic = getTopic(idProject, idTopic);

		synchronized (lockDataUpdated) {
			auditTopic.setReport(executiveSummary);
			this.dataUpdated = true;
		}
	}
	
	@Override
	public void saveWeights(int idProject, List<TopicWeight> weights) throws SkillerException {
		
		final int totalWeights = weights.stream().mapToInt(tw -> tw.getWeight()).reduce(0, Integer::sum);
		if (totalWeights != 100) {
			throw new SkillerException(CODE_PROJECT_INVALID_WEIGHTS, MESSAGE_PROJECT_INVALID_WEIGHTS);
		}
		
		for (TopicWeight weight : weights) {
			AuditTopic auditTopic = getTopic(idProject, weight.getIdTopic());
			synchronized (lockDataUpdated) {
				auditTopic.setWeight(weight.getWeight());
				this.dataUpdated = true;
			}
		}
	}

	@Override
	public void processAndSaveGlobalAuditEvaluation(int idProject) throws SkillerException {
		
		final Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		int sum = project.getAudit().values().stream()
			.map(auditTopic  -> auditTopic.getEvaluation() * auditTopic.getWeight())
			.mapToInt(i -> i)
			.sum();
		
		final int auditEvaluation = (int) Math.floor(sum/100);
				
		if (log.isDebugEnabled()) {
			log.debug(String.format("System has computed the global audit evaluation %d for the project %s", auditEvaluation, project.getName()));
		}
		
		synchronized (lockDataUpdated) {
			project.setAuditEvaluation(auditEvaluation);
			this.dataUpdated = true;
		}
	}

	/**
	 * Share and save the weights between all audit topics for the given project.
	 * @param project
	 * @throws SkillerException
	 */
	private void shareWeights(Project project) throws SkillerException {
		int numberOfTopics = project.getAudit().size();
		AuditTopic[] topics = new AuditTopic[numberOfTopics];
		System.arraycopy(project.getAudit().values().toArray(), 0, topics, 0, project.getAudit().size());
		int rest = 100;
		for (int i=0; i<topics.length; i++) {
			if (i == numberOfTopics - 1) {
				topics[i].setWeight(rest);
			} else {
				topics[i].setWeight((int) (Math.floor(100/numberOfTopics)));
				rest -= topics[i].getWeight();
			}
		}
	}

	@Override
	public void updateAttachmentFile(int idProject, int idTopic, AttachmentFile attachmentFile) throws SkillerException {

		AuditTopic auditTopic = getTopic(idProject, idTopic);
		
		synchronized (lockDataUpdated) {
			if (attachmentFile.getFileIdentifier() == auditTopic.getAttachmentList().size()) {
				auditTopic.getAttachmentList().add(attachmentFile);
			} else {
				auditTopic.getAttachmentList().remove(attachmentFile.getFileIdentifier());
				auditTopic.getAttachmentList().add(attachmentFile.getFileIdentifier(),  attachmentFile);
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void removeAttachmentFile(int idProject, int idTopic, int idFileIdentifier) throws SkillerException {
		
		AuditTopic auditTopic = getTopic(idProject, idTopic);
		AttachmentFile af = auditTopic.getAttachmentList().get(idFileIdentifier);
		if (af == null) {
			throw new SkillerException (
					CODE_CANNOT_RETRIEVE_ATTACHMENTFILE,
					MessageFormat.format(LIB_CANNOT_RETRIEVE_ATTACHMENTFILE, idProject, idTopic, idFileIdentifier));
		}
		storageService.removeFile(buildAttachmentFileName(idProject, idTopic, af.getFileName()));
		
		synchronized (lockDataUpdated) {
			auditTopic.getAttachmentList().remove(idFileIdentifier);
			this.dataUpdated = true;
		}
	}

	@Override
	public String buildAttachmentFileName(int idProject, int idTopic, String filename) {
		return idProject + "-" + idTopic + "-" + filename;
	}

}
