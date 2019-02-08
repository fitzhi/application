/**
 * 
 */
package fr.skiller.bean.impl;

import java.text.MessageFormat;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;
import fr.skiller.data.external.Action;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Mission;
import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;
import static fr.skiller.Error.CODE_MULTIPLE_LOGIN;
import static fr.skiller.Error.MESSAGE_MULTIPLE_LOGIN;

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
				.filter( (Project project) -> project.name.equals(projectName))
				.findFirst();
	}

	@Override
	public void init() {
		this.projects = null;
	}

	@Override
	public List<Contributor> contributors(int idProject) {
		List<Contributor> contributors = new ArrayList<Contributor>();
		staffHandler.getStaff().values().forEach(staff -> {
			Optional<Mission> optMission  = 
						staff.missions.stream()
						.filter(mission -> mission.idProject == idProject)
						.findFirst();
			if (optMission.isPresent()) {
				Mission mission = optMission.get();
				contributors.add(
						new Contributor(
								staff.idStaff, 
								mission.firstCommit, 
								mission.lastCommit, 
								mission.numberOfCommits, 
								mission.numberOfFiles));
			}
		});
		return contributors;
	}

	@Override
	public Project addNewProject(Project project) throws SkillerException {
		synchronized (lockDataUpdated) {
			Map<Integer, Project> projects = getProjects();
			if (project.id < 1) {
				project.id = projects.size() + 1;
			}
			projects.put(project.id, project);
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
		if (project.id == 0) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, project.id));
		}
		synchronized (lockDataUpdated) {
			getProjects().put(project.id, project);
			this.dataUpdated = true;
		}
	}

	@Override
	public List<Pseudo> saveGhosts(int idProject, List<Pseudo> pseudos) throws SkillerException {
	
		Project project = get(idProject);
		if (project == null) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		List<Ghost> newGhosts = new ArrayList<Ghost>();
		List<Pseudo> newPseudos = new ArrayList<Pseudo>();
		
		for (Pseudo pseudo : pseudos) {
			
			// Nothing to do for this pseudo. We'll keep him present in the list. 
			if ( ((pseudo.login == null) || (pseudo.login.length() == 0)) && !pseudo.technical ) {
				// In fact, this pseudo was present in the project.ghosts list, we'll remove it
				if (getGhost(project, pseudo.pseudo) != null) {
					pseudo.action = Action.D;
					newPseudos.add(pseudo);
				}
				continue;
			}
			
			if ((pseudo.login != null) && (pseudo.login.length() > 0)) {
				List<Staff> result = staffHandler.getStaff().values()
					.stream()
					.filter(staff -> pseudo.login.equals(staff.login))
					.collect(Collectors.toList());
				
				// Unknown login
				if (result.isEmpty()) {
					pseudo.action = Action.N;
					newPseudos.add(pseudo);
					continue;
				}
				
				if (result.size() > 1) {
					throw new SkillerException(CODE_MULTIPLE_LOGIN, 
							MessageFormat.format(MESSAGE_PROJECT_NOFOUND, pseudo.login, result.size()));
				}
				
				newGhosts.add(new Ghost(pseudo.pseudo, result.get(0).idStaff, false));
				Ghost gh = getGhost(project, pseudo.pseudo);
				if (gh == null) {
					pseudo.action = Action.A;
				} else {
					pseudo.action =  (gh.idStaff == result.get(0).idStaff) ? Action.N : Action.U;
				}
				pseudo.idStaff = result.get(0).idStaff;
				pseudo.fullName = staffHandler.getFullname(pseudo.idStaff);
				newPseudos.add(pseudo);
			}
			
			// login technical and not a developer.
			if (pseudo.technical) {
				newGhosts.add(new Ghost(pseudo.pseudo, true));	
				Ghost gh = getGhost(project, pseudo.pseudo);
				pseudo.technical = true;
				pseudo.idStaff = Ghost.NULL;
				pseudo.fullName = "";
				if (gh == null) {
					pseudo.action = Action.A;
				} else {
					pseudo.action =  (gh.idStaff > 0) ? Action.U : Action.N;
				}
				newPseudos.add(pseudo);						
			}
		}

		synchronized (lockDataUpdated) {
			project.ghosts = newGhosts;
			this.dataUpdated = true;
		}
		
		return newPseudos;
	}
	
	@Override
	public Ghost getGhost(final Project project, final String pseudo) {
		List<Ghost> actualGhosts = project.ghosts.stream()
				.filter(g -> g.pseudo.equals(pseudo))
				.collect(Collectors.toList());
		return actualGhosts.isEmpty() ? null : actualGhosts.get(0);
	}
	
	@Async
	public CompletableFuture<String> tempTest() throws Exception {
		System.out.println("I " + System.currentTimeMillis());
		Thread.sleep(10000);
		System.out.println("O " + System.currentTimeMillis());
		return CompletableFuture.completedFuture("OK");
	}

	@Override
	@Async
	public void test() {
		System.out.println("Input " + System.currentTimeMillis());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Output " + System.currentTimeMillis());
		
	}
}
