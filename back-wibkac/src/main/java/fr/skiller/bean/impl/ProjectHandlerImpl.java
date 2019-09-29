/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.FilesStats;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Library;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.SonarProject;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
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
		if (log.isDebugEnabled()) {
			log.debug (String.format("Retrieve the contributors list for the project id %d", idProject));
		}
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
	//TODO We have to be more precise on the saving process.
	// The implementation DELETE and REPLACE seams dangerous.
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
	public List<Library> saveLibraries(int idProject, List<Library> libraries) throws SkillerException {
		Project prj = this.get(idProject);
		List<Library> previousLibraries = prj.getLibraries();
		synchronized (lockDataUpdated) {
			prj.setLibraries(libraries);
			this.dataUpdated = true;
		}
		return previousLibraries;
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

	@Override
	public void saveRisk(Project project, int risk) {
		
		if (log.isInfoEnabled()) {
			log.info(String.format("The project %s has now, the level of risk %d", project.getName(), risk));
		}
		synchronized (lockDataUpdated) {
			project.setRisk(risk);
			this.dataUpdated = true;
		}
	}
	
	@Override
	public void addSkill(Project project, Skill skill) {
		
		if (log.isInfoEnabled()) {
			log.info(String.format("The project %s has declared the skill %s in its scope", 
					project.getName(), skill.getTitle()));
		}
		synchronized (lockDataUpdated) {
			if (!project.getSkills().contains(skill)) {
				project.getSkills().add(skill);
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void removeSkill(Project project, int idSkill) {
		
		if (log.isInfoEnabled()) {
			log.info(String.format("The project %s will loose the skill with id %d from its scope", 
					project.getName(), idSkill));
		}

		synchronized (lockDataUpdated) {
			Optional<Skill> oSkill = project.getSkills()
					.stream()
					.filter(exp -> (exp.getId() == idSkill) )
					.findFirst();
			if (oSkill.isPresent()) {
				project.getSkills().remove(oSkill.get());
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void associateStaffToGhost(Project project, String pseudo, int idAssociatedStaff) {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"the project %s is associating the pseudo ghost %s to the staff identifier %d",
					project.getName(),
					pseudo,
					idAssociatedStaff));
		}
		
		Staff staff = staffHandler.getStaff(idAssociatedStaff);
		if (staff == null) {
			throw new SkillerRuntimeException(
					String.format("SHOULD NOT PASS HERE : id %d does not exist anymore!", idAssociatedStaff));
		}
		boolean projectAlreadyDeclared = staff.getMissions().stream().anyMatch(mission -> mission.getIdProject() == project.getId());
		synchronized (lockDataUpdated) {
			Optional<Ghost> oGhost = project.getGhosts()
					.stream()
					.filter(g -> g.getPseudo().equals(pseudo))
					.findFirst();
			
			if (oGhost.isPresent()) {
				oGhost.get().setIdStaff(idAssociatedStaff);

				if (!projectAlreadyDeclared) {
					staff.addMission(new Mission(idAssociatedStaff, project.getId(), project.getName()));
				}
				return;
			}
			this.dataUpdated = true;
		}
		throw new SkillerRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
	}

	@Override
	public void setGhostTechnicalStatus(Project project, String pseudo, boolean technical) {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"the project %s has setup %s the ghost pseudo %s",
				project.getName(),
				technical ? "technical" : "non technical",
				pseudo));
		}
		
		synchronized (lockDataUpdated) {
			Optional<Ghost> oGhost = project.getGhosts()
					.stream()
					.filter(g -> g.getPseudo().equals(pseudo))
					.findFirst();
			if (oGhost.isPresent()) {
				
				oGhost.get().setTechnical(technical);
				
				Staff staff = staffHandler.getStaff(oGhost.get().getIdStaff());
				if ((staff != null) && (technical)) {
					System.out.println("removing mission " + staff.getMissions().size());
					staff.getMissions().stream()
						.filter(mission -> mission.getIdProject() == project.getId())
						.filter(mission -> mission.getFirstCommit() == null)
						.findFirst()
						.ifPresent(mission -> {
							System.out.println("removing mission " + mission.getIdProject() + " " + mission.getIdStaff());
							staff.getMissions().remove(mission);
						});
				}
				
				if (technical) {
					oGhost.get().setIdStaff(Ghost.NULL);			
				}
				this.dataUpdated = true;
				return;
			}		
		}
		throw new SkillerRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
		
	}

	@Override
	public void resetGhost(Project project, String pseudo) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"Resetting the Ghost pseudo %s of the project %s",
				project.getName(),
				pseudo));
		}
		
		synchronized (lockDataUpdated) {
			Optional<Ghost> oGhost = project.getGhosts()
					.stream()
					.filter(g -> g.getPseudo().equals(pseudo))
					.findFirst();
			if (oGhost.isPresent()) {
				oGhost.get().setTechnical(false);
				oGhost.get().setIdStaff(Ghost.NULL);
				this.dataUpdated = true;			
				return;
			}	
		}
		throw new SkillerRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
	}

	@Override
	public void integrateGhosts(int idProject, Set<String> pseudos) throws SkillerException {

		Project project = get(idProject);
		
		List<Ghost> ghosts = project.getGhosts().stream()
			.filter (ghost ->  (ghost.getIdStaff() > 0) || (ghost.isTechnical()) )
			.collect(Collectors.toList());
		
		for (String pseudo : pseudos) {
			if (!ghosts.stream().anyMatch(ghost -> pseudo.equals(ghost.getPseudo()))) {
				ghosts.add(new Ghost(pseudo, false));
			}
		}
		
		synchronized (lockDataUpdated) {
			project.setGhosts(ghosts);
			this.dataUpdated = true;
		}
	}

	@Override
	public void removeGhost(Project project, String pseudo) {
		synchronized (lockDataUpdated) {
			for (Iterator<Ghost> iter = project.getGhosts().listIterator(); iter.hasNext(); ) {
			    Ghost ghost = iter.next();
			    if (pseudo.equals(ghost.getPseudo())) {
			        iter.remove();
			    }
			}			
			this.dataUpdated = true;
		}
	}

	@Override
	public void saveSonarEntry(Project project, SonarProject sonarEntry) {

		Optional<SonarProject> oEntry = project.getSonarProjects()
				.stream()
				.filter(entry -> entry.getKey().equals(sonarEntry.getKey()))
				.findFirst();

		/**
		 * We update the name if necessary.
		 */
		synchronized (lockDataUpdated) {
			if (oEntry.isPresent()) {
				if (log.isDebugEnabled()) {
					log.debug(String.format
						("Updating Sonar entry %s name to %s", sonarEntry.getKey(), sonarEntry.getName()));
				}
				oEntry.get().setName(sonarEntry.getName());
			} else {
				if (log.isDebugEnabled()) {
					log.debug(String.format
						("Adding Sonar entry (%s, %s)", sonarEntry.getKey(), sonarEntry.getName()));
				}
				project.getSonarProjects().add(sonarEntry);
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void removeSonarEntry(Project project, SonarProject sonarEntry) {

		boolean isDeleted;
		
		synchronized (lockDataUpdated) {
			isDeleted = project
				.getSonarProjects()
				.removeIf(entry -> sonarEntry.getKey().equals(entry.getKey()));
			this.dataUpdated = true;
		}
		
		if ((isDeleted) && (log.isDebugEnabled())) {
			log.debug(
				String.format("The Sonar project %s has been deleted for id %s",
				sonarEntry.getName(), sonarEntry.getKey()));
		}
	}

	@Override
	public boolean containsSonarEntry(Project project, String key) {
		return project.getSonarProjects()
				.stream()
				.anyMatch(entry -> key.equals(entry.getKey()));
	}

	@Override
	public void saveFilesStats(Project project, String sonarProjectKey, List<FilesStats> filesStats) {
		
		try {
			SonarProject sonarPrj = project.getSonarProjects()
				.stream()
				.filter(sonarP -> sonarProjectKey.equals(sonarP.getKey()))
				.findFirst().get();
				
			synchronized (lockDataUpdated) {
				sonarPrj.setProjectFilesStats(filesStats);
				this.dataUpdated = true;
			}
		} catch (NoSuchElementException nsee) {
			throw new SkillerRuntimeException(nsee);
		}
		
	}
	
}
