package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.CODE_PROJECT_TOPIC_ALREADY_DECLARED;
import static fr.skiller.Error.CODE_PROJECT_TOPIC_UNKNOWN;

import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_TOPIC_ALREADY_DECLARED;
import static fr.skiller.Error.MESSAGE_PROJECT_TOPIC_UNKNOWN;

import java.text.MessageFormat;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.ProjectAuditHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.data.internal.AuditTopic;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * <p>
 * Main implementation for the interface {@link ProjectAuditHandler}.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
public class ProjectAuditHandlerImpl extends AbstractDataSaverLifeCycleImpl implements ProjectAuditHandler {

	@Autowired
	ProjectHandler projectHandler;
	
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
	
}
