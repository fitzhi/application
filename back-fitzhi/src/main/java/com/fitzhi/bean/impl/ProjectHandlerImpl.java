/**
 * 
 */
package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.CODE_SONAR_KEY_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SONAR_KEY_NOFOUND;
import static com.fitzhi.Global.NO_USER_PASSWORD_ACCESS;
import static com.fitzhi.Global.REMOTE_FILE_ACCESS;
import static com.fitzhi.Global.USER_PASSWORD_ACCESS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.SonarHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.FilesStats;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.ProjectSonarMetricValue;
import com.fitzhi.data.internal.SonarEvaluation;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.SkillerException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This class is the implementation of the ProjectHandler and is in charge of handling the projects in Fitzhì&copy;.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
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
	public DataHandler dataSaver;
			
	/**
	 * Bean in charge of handling connected Sonar server.
	 */
	@Autowired
	public SonarHandler sonarHandler;
	
	/**
	 * Bean in charge of handling the skills server.
	 */
	@Autowired 
	SkillHandler skillHandler;
	
	/**
	 * @return the <strong>Project</strong> collection.
	 * @throws SkillerException exception thrown most probably if an {@link IOException} occurs during the de-serialization process.
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
		
		// 
		// We encrypt the password 
		//
		encryptPasswordIfNecessary(project);
		
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

	/**
	 * If a password has been given to the GIT connection, we encrypt it.
	 * @param project the current project
	 * @throws SkillerException thrown certainly if the encryption failed
	 */
	private void encryptPasswordIfNecessary(Project project) throws SkillerException {
		if (project.getConnectionSettings() == 1) {
			String encryptedPassword = DataEncryption.encryptMessage(project.getPassword());
			project.setPassword(encryptedPassword);
		}
	}
	
	@Override
	public boolean containsProject(int idProject) throws SkillerException {
		return getProjects().containsKey(idProject);
	}

	@Override
	//TODO Need to verify that all fields are effectively update {
	public void saveProject(Project project) throws SkillerException {
		if (project.getId() == 0) {
			throw new SkillerException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, project.getId()));
		}
		synchronized (lockDataUpdated) {

			Project savedProject = get(project.getId());
			if (savedProject == null) {
				throw new SkillerRuntimeException(
						"SHOULD NOT PASS HERE : The project " + project.getId() + " is supposed to exist !");
			}
			savedProject.setName(project.getName());
			savedProject.setActive(project.isActive());
			savedProject.setUrlSonarServer(project.getUrlSonarServer());
			if ((project.getUrlSonarServer() == null) || ("".equals(project.getUrlSonarServer()))) {
				savedProject.setSonarProjects(new ArrayList<SonarProject>());
			}			
			savedProject.setConnectionSettings(project.getConnectionSettings());
			switch (project.getConnectionSettings()) {
				case USER_PASSWORD_ACCESS:
					savedProject.setConnectionSettings(USER_PASSWORD_ACCESS);
					savedProject.setUrlRepository(project.getUrlRepository());
					savedProject.setUsername(project.getUsername());
					if (project.getPassword() != null) {
						String encryptedPassword = DataEncryption.encryptMessage(project.getPassword());
						savedProject.setPassword(encryptedPassword);						
					}
					savedProject.setConnectionSettingsFile(null);
					break;
				case REMOTE_FILE_ACCESS:
					savedProject.setConnectionSettings(REMOTE_FILE_ACCESS);
					savedProject.setUrlRepository(project.getUrlRepository());
					savedProject.setUsername(null);
					savedProject.setPassword(null);
					savedProject.setConnectionSettingsFile(project.getConnectionSettingsFile());
					break;
				case NO_USER_PASSWORD_ACCESS:
					savedProject.setConnectionSettings(NO_USER_PASSWORD_ACCESS);
					savedProject.setUrlRepository(project.getUrlRepository());
					savedProject.setUsername(null);
					savedProject.setPassword(null);
					savedProject.setConnectionSettingsFile(project.getConnectionSettingsFile());
					break;
				default:
					savedProject.setConnectionSettings(0);
					savedProject.setUrlRepository(null);
					savedProject.setUsername(null);
					savedProject.setPassword(null);
					savedProject.setConnectionSettingsFile(null);
					break;
			}
			this.dataUpdated = true;
		}
	}
	
	
	@Override
	public void removeProject(int idProject) throws SkillerException {
		Map<Integer, Project> projects = getProjects();
		synchronized (lockDataUpdated) {
			projects.remove(idProject);
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
	public void initLocationRepository(int idProject) throws SkillerException {
		Project project = get(idProject);
		this.initLocationRepository(project);
	}

	@Override
	public void initLocationRepository(Project project) {
		synchronized (lockDataUpdated) {
			project.setLocationRepository(null);
			this.dataUpdated = true;
		}

	}
	@Override
	public void saveRisk(Project project, int staffEvaluation) {
		
		if (log.isInfoEnabled()) {
			log.info(String.format("The project %s has now, the level of risk %d", project.getName(), staffEvaluation));
		}
		synchronized (lockDataUpdated) {
			project.setStaffEvaluation(staffEvaluation);
			this.dataUpdated = true;
		}
	}
	
	@Override
	public void addSkill(Project project, ProjectSkill skill) {
		
		synchronized (lockDataUpdated) {
			if (!project.getSkills().containsKey(skill.getIdSkill())) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("The project '%s' has declared the skill with id '%d' in its scope", 
							project.getName(), skill.getIdSkill()));
				}
				project.getSkills().put(skill.getIdSkill(), skill);
			}
			this.dataUpdated = true;
		}
	}

	
	@Override
	public void removeSkill(Project project, int idSkill) {
		
		if (log.isInfoEnabled()) {
			log.info(String.format("The project %s will loose the skill with id '%d' from its scope", 
					project.getName(), idSkill));
		}

		synchronized (lockDataUpdated) {
			project.getSkills().remove(idSkill);
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
							if (log.isDebugEnabled()) {
								log.debug("removing mission " + mission.getIdProject() 
									+ " for " + staff.fullName());
							}
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
	public void detachStaffMemberFromGhostsOfAllProjects(int idStaff) throws SkillerException {
		
		List<Ghost> ghosts = this.getProjects().values().stream()
			.flatMap(p -> p.getGhosts().stream())
			.filter(ghost -> (ghost.getIdStaff() == idStaff))
			.collect(Collectors.toList());
			
		synchronized (lockDataUpdated) {
			ghosts.stream().forEach(ghost -> ghost.setIdStaff(-1));
			this.dataUpdated = true;			
		}
	}

	@Override
	public void integrateGhosts(int idProject, Set<String> unknownPseudos) throws SkillerException {

		Project project = get(idProject);
		
		List<Ghost> ghosts = project.getGhosts()
			.stream()
			.filter (ghost ->  (ghost.getIdStaff() > 0) || (ghost.isTechnical()) )
			.collect(Collectors.toList());
		
		for (String pseudo : unknownPseudos) {
			if (!ghosts.stream().anyMatch(ghost -> pseudo.equals(ghost.getPseudo()))) {
				ghosts.add(new Ghost(pseudo, false));
			}
		}
		
		synchronized (lockDataUpdated) {
			project.setGhosts(ghosts);
			this.dataUpdated = true;
		}
		
		// We list on INFO Level the ghosts contributing to the project
		if (log.isInfoEnabled() && (!ghosts.isEmpty())) {
			log.info(String.format("Ghost contributors for project %s", project.getName()));
			unknownPseudos.stream().forEach(log::info);
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
	public void addSonarEntry(Project project, SonarProject sonarEntry) throws SkillerException {

		Optional<SonarProject> oEntry = project.getSonarProjects()
				.stream()
				.filter(entry -> entry.getKey().equals(sonarEntry.getKey()))
				.findFirst();
		
		if (oEntry.isPresent()) {
			throw new SkillerRuntimeException(
					String.format(
							"The project %d:%s has already this Sonar key %s registered", 
							project.getId(), 
							project.getName(), 
							sonarEntry.getKey()));
		}

		/**
		 * We update the name if necessary.
		 */
		synchronized (lockDataUpdated) {
			if (log.isDebugEnabled()) {
				log.debug(String.format
					("Adding Sonar entry (%s, %s)", sonarEntry.getKey(), sonarEntry.getName()));
			}
			project.getSonarProjects().add(sonarEntry);
			
			/**
			 * We add the default metrics for this new Sonar project
			 */
			sonarEntry.setProjectSonarMetricValues(sonarHandler.getDefaultProjectSonarMetrics());
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

	/**
	 * Select the Sonar project corresponding to the couple (project, sonarKey)
	 * @param project the given project
	 * @param sonarKey the given 
	 * @return
	 * @throws SkillerException
	 */
	private SonarProject getSonarProject(Project project, String sonarKey) throws SkillerException {
		Optional<SonarProject> oSonarProject = project.getSonarProjects()
				.stream()
				.filter(sp -> sonarKey.equals(sp.getKey()))
				.findFirst();
		if (!oSonarProject.isPresent()) {
			throw new SkillerException(CODE_SONAR_KEY_NOFOUND, MESSAGE_SONAR_KEY_NOFOUND, sonarKey, project.getName());
		}
		return oSonarProject.get();
	}
	
	@Override
	public void saveFilesStats(Project project, String sonarProjectKey, List<FilesStats> filesStats) {
		
		try {

			SonarProject sonarProject = getSonarProject(project, sonarProjectKey);
				
			synchronized (lockDataUpdated) {
				sonarProject.setProjectFilesStats(filesStats);
				this.dataUpdated = true;
			}
		} catch (Exception e) {
			throw new SkillerRuntimeException(e);
		}
	}
	
	@Override
	public void saveSonarMetricValues(
			Project project, 
			String sonarProjectKey,
			List<ProjectSonarMetricValue> metricValues) throws SkillerException {
		
		SonarProject sonarProject = getSonarProject(project, sonarProjectKey);
		
		synchronized (lockDataUpdated) {
			sonarProject.setProjectSonarMetricValues(metricValues); 
			this.dataUpdated = true;
		}
	}

	@Override
	public void saveSonarEvaluation(Project project, String sonarProjectKey, SonarEvaluation sonarEvaluation)
			throws SkillerException {
		
		SonarProject sonarProject = getSonarProject(project, sonarProjectKey);
		synchronized (lockDataUpdated) {
			sonarProject.setSonarEvaluation(sonarEvaluation);
			this.dataUpdated = true;
		}
		
	}

	@Override
	public void saveUrlSonarServer(Project project, String newUrlSonarServer)  {
		synchronized (lockDataUpdated) {
			if ((newUrlSonarServer == null) || "".equals(newUrlSonarServer) || !newUrlSonarServer.equals(project.getUrlSonarServer())) {
				project.setSonarProjects(new ArrayList<SonarProject>());
			}
			project.setUrlSonarServer(newUrlSonarServer);
			this.dataUpdated = true;
		}
	}

	@Override
	public void saveEcosystems(Project project, List<Integer> ecosystems) {
		synchronized (lockDataUpdated) {
			project.setEcosystems(ecosystems);
			this.dataUpdated = true;
		}
	}

	@Override
	public void updateSkills(Project project, List<CommitHistory> entries) throws SkillerException {
		
		this.resetProjectSkillsMetrics(project);
		
		Map<Integer, ProjectSkill> detectedSkills = this.skillHandler.extractSkills(project.getLocationRepository(), entries);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("detected skills for project %s", project.getName()));
			for (ProjectSkill detectedSkill : detectedSkills.values()) {
				log.debug(skillHandler.getSkill(detectedSkill.getIdSkill()).getTitle());
			}
		}
		
		// We Update the skills for the given project.
		synchronized (lockDataUpdated) {
			for (ProjectSkill projectSkill : detectedSkills.values()) {
				ProjectSkill existingProjectSkill = project.getSkills().get(projectSkill.getIdSkill());
				if (existingProjectSkill == null) {
					project.getSkills().put(projectSkill.getIdSkill(), projectSkill);
				} else  {
					existingProjectSkill.setNumberOfFiles(projectSkill.getNumberOfFiles());
					existingProjectSkill.setTotalFilesSize(projectSkill.getTotalFilesSize());
				}
			}
			this.dataUpdated = true;
		}
	}

	@Override
	public void inactivateProject(Project project) {

		synchronized (lockDataUpdated) {
			project.setActive(false);
			this.dataUpdated = true;
		}
		
	}

	@Override
	public void reactivateProject(Project project) {

		synchronized (lockDataUpdated) {
			project.setActive(true);
			this.dataUpdated = true;
		}
		
	}

	@Override
	public void resetProjectSkillsMetrics(Project project) {
		project.getSkills().values().stream().forEach(skill -> skill.setNumberOfFiles(0));
	}

	@Override
	public boolean hasValidRepository(Project project) {
		
		if (project.getLocationRepository() == null) {
			return false;
		}
		
		Path repo = Paths.get(project.getLocationRepository(), ".git");
		if (log.isDebugEnabled()) {
			log.debug(String.format("Examining if %s exists", repo.toFile().getAbsolutePath()));
		}
		return (repo.toFile().exists());
	}
	
}
