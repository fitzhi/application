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
import fr.skiller.data.internal.AuditProject;
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

		if (project.getAuditProjects() == null) {
			project.setAuditProjects(new HashMap<Integer, AuditProject>());
		}
		
		// We do not add this topic if it already exists.
		if (!project.getAuditProjects().containsKey(idTopic)) {		
			synchronized (lockDataUpdated) {
				project.getAuditProjects().put(idTopic, new AuditProject(idTopic));
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
			project.getAuditProjects().remove(idTopic);
			this.dataUpdated = true;
		}
	}

	@Override
	public AuditProject getTopic(int idProject, int idTopic) throws SkillerException {

		final Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		if (project.getAuditProjects() == null) {
			throw new SkillerException(CODE_PROJECT_TOPIC_UNKNOWN, MessageFormat.format(MESSAGE_PROJECT_TOPIC_UNKNOWN, idTopic, project.getName()));
		}
		
		final AuditProject auditProject = project.getAuditProjects().get(idTopic);
		if (auditProject == null) {
			throw new SkillerException(CODE_PROJECT_TOPIC_UNKNOWN, MessageFormat.format(MESSAGE_PROJECT_TOPIC_UNKNOWN, idTopic, project.getName()));
		}
		
		return auditProject;
	}
	
}
