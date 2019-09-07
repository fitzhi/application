/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.MESSAGE_PROJECT_NOFOUND;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Library;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
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
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(ProjectHandlerImpl.class.getCanonicalName());

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
		if (logger.isDebugEnabled()) {
			logger.debug (String.format("Retrieve the contributors list for the project id %d", idProject));
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
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("The project %s has now, the level of risk %d", project.getName(), risk));
		}
		synchronized (lockDataUpdated) {
			project.setRisk(risk);
			this.dataUpdated = true;
		}
	}
	
	@Override
	public void addSkill(Project project, Skill skill) {
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("The project %s has declared the skill %s in its scope", 
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
		
		if (logger.isInfoEnabled()) {
			logger.info(String.format("The project %s will loose the skill with id %d from its scope", 
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

		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"the project %s has associated the pseudo ghost % to the staff identifier %d",
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
			
		}
		throw new SkillerRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
	}

	@Override
	public void setGhostTechnicalStatus(Project project, String pseudo, boolean technical) {

		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
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
					System.out.println("removing mission " + staff.getMissions().size());
				}
				
				if (technical) {
					oGhost.get().setIdStaff(Ghost.NULL);			
				}
				return;
			}		
		}
		throw new SkillerRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
		
	}

	@Override
	public void resetGhost(Project project, String pseudo) {
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
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
				return;
			}		
		}
		throw new SkillerRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
	}
	
}
