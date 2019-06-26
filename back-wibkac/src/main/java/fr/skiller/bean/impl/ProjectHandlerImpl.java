/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_MULTIPLE_LOGIN;
import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.Action;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Committer;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Component
public class ProjectHandlerImpl extends AbstractDataSaverLifeCycleImpl implements ProjectHandler {
	
	/**
	 * The Project collection.
	 */
	private Map<Integer, Project> projects;

	/**
	 * The staff Handler.
	 */
	@Autowired
	public StaffHandler staffHandler;
	
	/**
	 * For retrieving data from the persistent repository.
	 */
	@Autowired
	public DataSaver dataSaver;
		
	/**
	 * @return the Project collection.
	 * @throws SkillerException 
	 */
	@Override
	public Map<Integer, Project> getProjects() throws SkillerException {
		if (this.projects != null) {
			return this.projects;
		}
		this.projects = dataSaver.loadProjects();
		return projects;
	}

	@Override
	public Project get(final int idProject) throws SkillerException {
		return getProjects().get(idProject);
	}

	@Override
	public Optional<Project> lookup(final String projectName) throws SkillerException {
		return getProjects().values().stream()
				.filter( (Project project) -> project.getName().equals(projectName))
				.findFirst();
	}

	@Override
	public void init() {
		this.projects = null;
	}

	@Override
	public List<Contributor> contributors(int idProject) {
		List<Contributor> contributors = new ArrayList<>();
		staffHandler.getStaff().values().forEach(staff -> {
			Optional<Mission> optMission  = 
						staff.getMissions().stream()
						.filter(mission -> mission.getIdProject() == idProject)
						.findFirst();
			if (optMission.isPresent()) {
				Mission mission = optMission.get();
				contributors.add(
						new Contributor(
								staff.getIdStaff(), 
								mission.getFirstCommit(), 
								mission.getLastCommit(), 
								mission.getNumberOfCommits(), 
								mission.getNumberOfFiles()));
			}
		});
		return contributors;
	}

	@Override
	public Project addNewProject(Project project) throws SkillerException {
		synchronized (lockDataUpdated) {
			Map<Integer, Project> theProjects = getProjects();
			if (project.getId() < 1) {
				project.setId(theProjects.size() + 1);
			}
			theProjects.put(project.getId(), project);
			this.dataUpdated = true;
		}
		return project;
	}

	@Override
	public boolean containsProject(int idProject) throws SkillerException {
		return getProjects().containsKey(idProject);
	}

	@Override
	public void saveProject(Project project) throws SkillerException {
		if (project.getId() == 0) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, project.getId()));
		}
		synchronized (lockDataUpdated) {
			getProjects().put(project.getId(), project);
			this.dataUpdated = true;
		}
	}

	@Override
	public List<Committer> saveGhosts(int idProject, List<Committer> pseudos) throws SkillerException {
	
		Project project = get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		List<Ghost> newGhosts = new ArrayList<>();
		List<Committer> newPseudos = new ArrayList<>();
		
		for (Committer pseudo : pseudos) {
			
			// Nothing to do for this pseudo. We'll keep him present in the list. 
			if ( ((pseudo.getLogin() == null) || (pseudo.getLogin().length() == 0)) && !pseudo.isTechnical() ) {
				// In fact, this pseudo was present in the project.ghosts list, we'll remove it
				if (getGhost(project, pseudo.getPseudo()) != null) {
					pseudo.setAction(Action.D);
					newPseudos.add(pseudo);
				}
				continue;
			}
			
			if ((pseudo.getLogin() != null) && (pseudo.getLogin().length() > 0)) {
				List<Staff> result = staffHandler.getStaff().values()
					.stream()
					.filter(staff -> pseudo.getLogin().equals(staff.getLogin()))
					.collect(Collectors.toList());
				
				// Unknown login
				if (result.isEmpty()) {
					pseudo.setAction(Action.N);
					newPseudos.add(pseudo);
					continue;
				}
				
				if (result.size() > 1) {
					throw new SkillerException(CODE_MULTIPLE_LOGIN, 
							MessageFormat.format(MESSAGE_PROJECT_NOFOUND, pseudo.getLogin(), result.size()));
				}
				
				newGhosts.add(new Ghost(pseudo.getPseudo(), result.get(0).getIdStaff(), false));
				Ghost gh = getGhost(project, pseudo.getPseudo());
				if (gh == null) {
					pseudo.setAction(Action.A);
				} else {
					pseudo.setAction((gh.getIdStaff() == result.get(0).getIdStaff()) ? Action.N : Action.U);
				}
				pseudo.setIdStaff(result.get(0).getIdStaff());
				pseudo.setFullName(staffHandler.getFullname(pseudo.getIdStaff()));
				newPseudos.add(pseudo);
			}
			
			// login technical and not a developer.
			if (pseudo.isTechnical()) {
				newGhosts.add(new Ghost(pseudo.getPseudo(), true));	
				Ghost gh = getGhost(project, pseudo.getPseudo());
				pseudo.setTechnical(true);
				pseudo.setIdStaff(Ghost.NULL);
				pseudo.setFullName("");
				if (gh == null) {
					pseudo.setAction(Action.A);
				} else {
					pseudo.setAction((gh.getIdStaff() > 0) ? Action.U : Action.N);
				}
				newPseudos.add(pseudo);						
			}
		}

		synchronized (lockDataUpdated) {
			project.setGhosts(newGhosts);
			this.dataUpdated = true;
		}
		
		return newPseudos;
	}
	
	@Override
	public Ghost getGhost(final Project project, final String pseudo) {
		List<Ghost> actualGhosts = project.getGhosts().stream()
				.filter(g -> pseudo.equals(g.getPseudo()))
				.collect(Collectors.toList());
		return actualGhosts.isEmpty() ? null : actualGhosts.get(0);
	}

	@Override
	public void saveLocationRepository(int idProject, String location) throws SkillerException {
		Project project = get(idProject);
		synchronized (lockDataUpdated) {
			project.setLocationRepository(location);
			this.dataUpdated = true;
		}
		
	}
	
}
