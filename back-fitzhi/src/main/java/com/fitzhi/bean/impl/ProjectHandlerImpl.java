package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.CODE_SONAR_KEY_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SONAR_KEY_NOFOUND;
import static com.fitzhi.service.ConnectionSettingsType.DIRECT_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.NO_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.PUBLIC_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.REMOTE_FILE_LOGIN;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.SonarHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.AuthorExperienceTemplate;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.ExperienceAbacus;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.FilesStats;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.ProjectSonarMetricValue;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.internal.SonarEvaluation;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.StaffExperienceTemplate;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.javaparser.ExperienceParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
	 * <p>his setting activates or not the code parser execution.</p>
	 * <i>
	 * This setting has been setup to avoid memory problems on some platforms.
	 * The nominal behavior of the application is to run the code parser.
	 * </i> 
	 */
	@Value("${code.parser}")
	private int codeParser;

	/**
	 * The Project collection.
	 */
	private Map<Integer, Project> projects;

	/**
	 * Component in charge of handling the staff members.
	 */
	@Autowired
	public StaffHandler staffHandler;
	
	/**
	 * Service For retrieving data from the persistent repository.
	 */
	@Autowired
	public DataHandler dataHandler;
			
	/**
	 * Cache service regarding the sunburst data.
	 */
	@Autowired
	public CacheDataHandler cacheDataHandler;

	/**
	 * Component in charge of handling connected Sonar server.
	 */
	@Autowired
	public SonarHandler sonarHandler;
	
	/**
	 * Component in charge of handling the skills server.
	 */
	@Autowired 
	SkillHandler skillHandler;
	
	/**
	 * This service is in charge of the detection of ecosystems and experiences.
	 */
	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;

	@Override
	public Map<Integer, Project> getProjects() throws ApplicationException {
		if (this.projects != null) {
			return this.projects;
		}
		this.projects = dataHandler.loadProjects();
		return projects;
	}

	@Override
	public List<Project> activeProjects() throws ApplicationException {
		return getProjects().values().stream().filter(Project::isActive).collect(Collectors.toList());
	}

	@Override
	public Project lookup(final int idProject) throws ApplicationException {
		return getProjects().get(idProject);
	}

	@Override
	public Project getProject(final int idProject) throws ApplicationException {
		Project project = getProjects().get(idProject);
		if (project == null) {
			throw new NotFoundException(CODE_PROJECT_NOFOUND, 
				MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		return project;
	}

	@Override
	public Optional<Project> lookup(final String projectName) throws ApplicationException {
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
		return staffHandler.getStaff().values().stream()
			.flatMap(st -> st.getMissions().stream())
			.filter(mission -> mission.getIdProject() == idProject)
			.map(mission -> new Contributor(
				mission.getIdStaff(), 
				mission.getFirstCommit(), 
				mission.getLastCommit(), 
				mission.getNumberOfCommits(), 
				mission.getNumberOfFiles()))
			.collect(Collectors.toList());
	}

	@Override
	public Project addNewProject(Project project) throws ApplicationException {
		
		// 
		// We encrypt the GIT credential password if necessary.
		//
		encryptPasswordIfNecessary(project);
		
		synchronized (lockDataUpdated) {
			Map<Integer, Project> theProjects = getProjects();
			final int nextId = (project.getId() < 0) ? nextIdProject() : project.getId();
			project.setId(nextId);
			theProjects.put(nextId, project);
			this.dataUpdated = true;
		}
		return project;
	}

	@Override
	public int nextIdProject() throws ApplicationException {
		Map<Integer, Project> portfolio = getProjects();
		try {
			int max = portfolio
				.keySet()
				.stream()
				.mapToInt(v->v)
				.max()
				.orElseThrow(NoSuchElementException::new);
			return max + 1;
		} catch (final NoSuchElementException e) {
			return 1;
		}
	}

	/**
	 * If a password has been given to the GIT connection, we encrypt it.
	 * @param project the current project
	 * @throws ApplicationException thrown certainly if the encryption failed
	 */
	private void encryptPasswordIfNecessary(Project project) throws ApplicationException {
		if (project.getConnectionSettings() == DIRECT_LOGIN) {
			String encryptedPassword = DataEncryption.encryptMessage(project.getPassword());
			project.setPassword(encryptedPassword);
		}
	}
	
	@Override
	public boolean containsProject(int idProject) throws ApplicationException {
		return getProjects().containsKey(idProject);
	}

	@Override
	public void saveProject(Project project) throws ApplicationException {
		if (project.getId() == 0) {
			throw new ApplicationException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, project.getId()));
		}
		synchronized (lockDataUpdated) {

			Project savedProject = lookup(project.getId());
			if (savedProject == null) {
				throw new ApplicationRuntimeException(
						"SHOULD NOT PASS HERE : The project " + project.getId() + " is supposed to exist !");
			}
			savedProject.setName(project.getName());
			savedProject.setActive(project.isActive());
			savedProject.setUrlSonarServer(project.getUrlSonarServer());

			savedProject.setUrlCodeFactorIO((project.getUrlCodeFactorIO()));
			if ((project.getUrlSonarServer() == null) || ("".equals(project.getUrlSonarServer()))) {
				savedProject.setSonarProjects(new ArrayList<SonarProject>());
			}			
			
			// If we change the URL repository, we have to reset the previous clone (if any). 
			// We force this re-construction by reseting the clone location repository.
			if ( 	(savedProject.getUrlRepository() != null) && 
					(!savedProject.getUrlRepository().equals(project.getUrlRepository()))) {
				savedProject.setLocationRepository(null);
				dataHandler.removeCrawlerFiles(savedProject);
				cacheDataHandler.removeRepository(savedProject);
				staffHandler.removeProject(savedProject.getId());
			}
			
			// If we change the active branch, we have to reset the previous clone (if any). 
			// We force this reconstruction by reseting the clone location repository.
			if ( 	(savedProject.getBranch() != null) && 
					(!savedProject.getBranch().equals(project.getBranch()))) {
				savedProject.setLocationRepository(null);
				dataHandler.removeCrawlerFiles(savedProject);
				cacheDataHandler.removeRepository(savedProject);
				staffHandler.removeProject(savedProject.getId());
			}

			// Default branch name is "master" if none is given
			if ((project.getUrlRepository() != null) && (project.getBranch() == null)) {
				savedProject.setBranch("master");
			} else {
				savedProject.setBranch(project.getBranch());
			}
		
			savedProject.setConnectionSettings(project.getConnectionSettings());
			switch (project.getConnectionSettings()) {
				case DIRECT_LOGIN:
					savedProject.setConnectionSettings(DIRECT_LOGIN);
					savedProject.setUrlRepository(project.getUrlRepository());
					savedProject.setUsername(project.getUsername());
					if (project.getPassword() != null) {
						String encryptedPassword = DataEncryption.encryptMessage(project.getPassword());
						savedProject.setPassword(encryptedPassword);						
					}
					savedProject.setConnectionSettingsFile(null);
					break;
				case REMOTE_FILE_LOGIN:
					savedProject.setConnectionSettings(REMOTE_FILE_LOGIN);
					savedProject.setUrlRepository(project.getUrlRepository());
					savedProject.setUsername(null);
					savedProject.setPassword(null);
					savedProject.setConnectionSettingsFile(project.getConnectionSettingsFile());
					break;
				case PUBLIC_LOGIN:
					savedProject.setConnectionSettings(PUBLIC_LOGIN);
					savedProject.setUrlRepository(project.getUrlRepository());
					savedProject.setUsername(null);
					savedProject.setPassword(null);
					savedProject.setConnectionSettingsFile(project.getConnectionSettingsFile());
					break;
				default:
					savedProject.setConnectionSettings(NO_LOGIN);
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
	public void removeProject(int idProject) throws ApplicationException {
		synchronized (lockDataUpdated) {
			getProjects().remove(idProject);
			this.dataUpdated = true;
		}
	}

	@Override
	public List<Library> saveLibraries(int idProject, List<Library> libraries) throws ApplicationException {
		Project prj = this.lookup(idProject);
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
	public void saveLocationRepository(int idProject, String location) throws ApplicationException {
		Project project = lookup(idProject);
		synchronized (lockDataUpdated) {
			project.setLocationRepository(location);
			this.dataUpdated = true;
		}
	}
	
	@Override
	public void initLocationRepository(int idProject) throws ApplicationException {
		Project project = getProject(idProject);
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
		
		Staff staff = staffHandler.lookup(idAssociatedStaff);
		if (staff == null) {
			throw new ApplicationRuntimeException(
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
				this.dataUpdated = true;
				if (!projectAlreadyDeclared) {
					staffHandler.addMission(idAssociatedStaff, project.getId(), project.getName());
				}
				return;
			}
		}
		throw new ApplicationRuntimeException(
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
				
				Staff staff = staffHandler.lookup(oGhost.get().getIdStaff());
				if ((staff != null) && (technical)) {
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
		throw new ApplicationRuntimeException(
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
		throw new ApplicationRuntimeException(
				String.format("%s does not exist anymore in the project %s (id: %d)",
						pseudo, project.getName(), project.getId()));
	}

	
	@Override
	public void detachStaffMemberFromGhostsOfAllProjects(int idStaff) throws ApplicationException {
		
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
	public void integrateGhosts(int idProject, List<Ghost> detectedGhosts) throws ApplicationException {

		Project project = getProject(idProject);

		// We must keep the edit made on ghosts since the last analysis, such as
		// - the identifer assigned to a ghost by an end-user.
		// - the fact that a ghost is tag as technical
		// Therefore, we keep the identified ghosts.
		List<Ghost> ghosts = project.getGhosts()
			.stream()
			.filter (ghost ->  (ghost.getIdStaff() > 0) || (ghost.isTechnical()) )
			.collect(Collectors.toList());
		
		for (Ghost detectedGhost : detectedGhosts) {
			if (ghosts.stream().noneMatch(ghost -> detectedGhost.getPseudo().equals(ghost.getPseudo()))) {
				ghosts.add(detectedGhost);
			}
		}
		
		synchronized (lockDataUpdated) {
			project.setGhosts(ghosts);
			this.dataUpdated = true;
		}
		
		// We list on INFO Level the ghosts contributing to the project
		if (log.isInfoEnabled() && (!ghosts.isEmpty())) {
			log.info(String.format("[%d] Ghost contributors for project %s", project.getId(), project.getName()));
			StringBuilder sb = new StringBuilder(String.format("[%d]", project.getId()));
			detectedGhosts.stream().forEach(s -> sb.append(s).append(" "));
			log.info(sb.toString());
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
	public void addSonarEntry(Project project, SonarProject sonarProject) throws ApplicationException {

		Optional<SonarProject> oEntry = project.getSonarProjects()
				.stream()
				.filter(entry -> entry.getKey().equals(sonarProject.getKey()))
				.findFirst();
		
		if (oEntry.isPresent()) {
			log.warn(String.format(
				"The project %d %s has already registered the Sonar key %s", 
				project.getId(), 
				project.getName(), 
				sonarProject.getKey()));
			return;
		}

		/**
		 * We update the name if necessary.
		 */
		synchronized (lockDataUpdated) {
			if (log.isDebugEnabled()) {
				log.debug(String.format
					("Adding Sonar entry (%s, %s)", sonarProject.getKey(), sonarProject.getName()));
			}
			project.getSonarProjects().add(sonarProject);
			
			/**
			 * We add the default metrics for this new Sonar project
			 */
			sonarProject.setProjectSonarMetricValues(sonarHandler.getDefaultProjectSonarMetrics());
			this.dataUpdated = true;
		}
	}

	@Override
	public void removeSonarEntry(@NotNull Project project, @NotNull String sonarKey) {

		boolean isDeleted;
		
		synchronized (lockDataUpdated) {
			isDeleted = project
				.getSonarProjects()
				.removeIf(entry -> sonarKey.equals(entry.getKey()));
			this.dataUpdated = true;
		}
		
		if ((isDeleted) && (log.isDebugEnabled())) {
			log.debug( String.format("The Sonar project %s has been removed", sonarKey));
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
	 * @throws ApplicationException
	 */
	private SonarProject getSonarProject(Project project, String sonarKey) throws ApplicationException {
		Optional<SonarProject> oSonarProject = project.getSonarProjects()
				.stream()
				.filter(sp -> sonarKey.equals(sp.getKey()))
				.findFirst();
		if (!oSonarProject.isPresent()) {
			throw new ApplicationException(CODE_SONAR_KEY_NOFOUND, MESSAGE_SONAR_KEY_NOFOUND, sonarKey, project.getName());
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
			throw new ApplicationRuntimeException(e);
		}
	}
	
	@Override
	public void saveSonarMetricValues(
			Project project, 
			String sonarProjectKey,
			List<ProjectSonarMetricValue> metricValues) throws ApplicationException {
		
		SonarProject sonarProject = getSonarProject(project, sonarProjectKey);
		
		synchronized (lockDataUpdated) {
			sonarProject.setProjectSonarMetricValues(metricValues); 
			this.dataUpdated = true;
		}
	}

	@Override
	public void saveSonarEvaluation(Project project, String sonarProjectKey, SonarEvaluation sonarEvaluation)
			throws ApplicationException {
		
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
				project.setSonarProjects(new ArrayList<>());
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
	public void updateSkills(Project project, List<CommitHistory> entries) throws ApplicationException {
		
		this.resetProjectSkillsMetrics(project);
		
		Map<Integer, ProjectSkill> detectedSkills = this.skillHandler.extractSkills(project.getLocationRepository(), entries);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("Detected skills for project %s", project.getName()));
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

	@Override
	public void processProjectsExperiences() throws ApplicationException {
		clearProjectsExperiences();	
		fillProjectsExperiencesWithLinesNumbers();
		if (codeParser == 1) {
			fillProjectsExperiencesWithCodeParsers();
		}
	}

	/**
	 * Load or create a new {@link ProjectDetectedExperiences container of Project experiences}.
	 * @param project the given project
	 * @return {@link ProjectDetectedExperiences container of experiences}
	 * @throws ApplicationException thrown if any problem occurs
	 */
	private ProjectDetectedExperiences getProjectDetectedExperiences(final Project project) throws ApplicationException {
		final ProjectDetectedExperiences experiences = dataHandler.loadDetectedExperiences(project);
		if (experiences == null) {
			return ProjectDetectedExperiences.of();
		} else {
			return experiences;
		}
	}

	/**
	 * Clean all experiences intermediate collections.
	 * @throws ApplicationException thrown if any problem occurs
	 */
	private void clearProjectsExperiences() throws ApplicationException {
		for (Project project : this.activeProjects()) {
			dataHandler.saveDetectedExperiences(project, ProjectDetectedExperiences.of());
		}
	}

	/**
	 * Evaluate the experience based on the content of a source file.
	 * @param experiences the experiences detected on this project
	 * @throws ApplicationException thrown if any problem occurs
	 */
	private void fillProjectsExperiencesWithCodeParsers() throws ApplicationException {
		if (log.isInfoEnabled()) {
			log.info("Evaluation of the skills levels based on content of the source files.");
		}
		for (Project project : this.activeProjects()) {
			// We skip all projects without local repository. There is nothing to parse.
			if (!project.hasLocalRepository()) {
				continue;
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("Processing %s.", project.getName()));
			}
			final ExperienceParser[] parsers = this.ecosystemAnalyzer.loadExperienceParsers(project, ".java$");
			if (log.isDebugEnabled()) {
				for (ExperienceParser parser : parsers) {
					log.debug(String.format("Parsers used %s.", parser.getClass().getName()));
				}
			}
			ProjectDetectedExperiences experiences = getProjectDetectedExperiences(project);
			ecosystemAnalyzer.loadDetectedExperiences(project, experiences, parsers);
			dataHandler.saveDetectedExperiences(project, experiences);
			if (log.isInfoEnabled()) {
				log.info(String.format("Content detection process terminated for %s.", project.getName()));
			}
		}
	}
	/**
	 * Evaluate the experience based on the number of lines implemented by developers.
	 * @throws ApplicationException thrown if any problem occurs
	 */
	private void fillProjectsExperiencesWithLinesNumbers() throws ApplicationException {

		if (log.isInfoEnabled()) {
			log.info("Evaluation of the skills levels based on the number of lines per type.");
		}
		for (Project project : this.activeProjects()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Processing %s.", project.getName()));
			}
			
			final List<Skill> skills = project.getSkills().values()
				.stream()
				.map(ProjectSkill::getIdSkill)
				.map(id -> skillHandler.lookup(id))
				.filter(Objects::nonNull)
				.filter(skill -> skill.getDetectionTemplate().getDetectionType() == SkillDetectorType.FILENAME_DETECTOR_TYPE)
				.collect(Collectors.toList());
			// No skill elligible for the experiences detection.
			if (skills.isEmpty()) {
				continue;
			}

			final SourceControlChanges changes = this.dataHandler.loadChanges(project);
			// If the changes file does not exist, we skiip this project
			if (changes == null) {
				continue;

			}
			ProjectDetectedExperiences experiences = getProjectDetectedExperiences(project);
			ecosystemAnalyzer.calculateExperiences(project, skills, changes, experiences);
			if (log.isDebugEnabled()) {
				experiences.content()
					.stream()
					.forEach(exp -> log.debug(String.format("%s %d %d.", exp.getAuthor().getName(), exp.getIdExperienceDetectionTemplate(), exp.getCount())));
			}
			dataHandler.saveDetectedExperiences(project, experiences);

			if (log.isInfoEnabled()) {
				log.info(String.format("Line number detection process terminated for %s.", project.getName()));
			}
		}
	}

	@Override
	public Map<StaffExperienceTemplate, Integer> processGlobalExperiences() throws ApplicationException {

		List<DetectedExperience> globalExperiences = new ArrayList<>();

		for (Project project : this.activeProjects()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Taking in account %s", project.getName()));
			}

			ProjectDetectedExperiences detectedExperiences = dataHandler.loadDetectedExperiences(project);
			// No intermediate file saved for goof or bad reason : we skip this project
			if (detectedExperiences == null) {
				continue;
			}

			globalExperiences.addAll(detectedExperiences.content());

		}

		Map<AuthorExperienceTemplate, Integer> authorAggregations = globalExperiences.stream().collect(
			Collectors.groupingBy(
				DetectedExperience::getKeyAggregateExperience,
				Collectors.summingInt(DetectedExperience::getCount)));
		
		Map<StaffExperienceTemplate, Integer> staffAggregations = new HashMap<>();
		for (AuthorExperienceTemplate authorExperienceTemplate : authorAggregations.keySet()) {
			Staff staff = staffHandler.lookup(authorExperienceTemplate.getAuthor());
			if (staff != null) {
				StaffExperienceTemplate key = StaffExperienceTemplate.of(authorExperienceTemplate.getIdExperienceDetectionTemplate(), staff.getIdStaff());
				Integer count = staffAggregations.get(key);
				if (count == null) {
					// We create a new record 
					staffAggregations.put(key, authorAggregations.get(authorExperienceTemplate));
				} else { 
					// We update an existing one
					staffAggregations.put(key, authorAggregations.get(authorExperienceTemplate) + count);
				}
			}
		}
		return staffAggregations;
	}

	@Override
	public void updateStaffSkillLevel(Map<StaffExperienceTemplate, Integer> experiences) throws ApplicationException {

		// Nothing to do.
		if (experiences.isEmpty()) {
			return;
		}

		Map<Integer, ExperienceDetectionTemplate> templates = ecosystemAnalyzer.loadExperienceDetectionTemplates();

		List<ExperienceAbacus> abacus = ecosystemAnalyzer.loadExperienceAbacus();
		for (Entry<StaffExperienceTemplate, Integer> entry : experiences.entrySet()) {
			final int idStaff = entry.getKey().getIdStaff();
			final int idEDT = entry.getKey().getIdExperienceDetectionTemplate();
			final int value = experiences.get(entry.getKey());

			// We do not take in account developers with a negative value
			if (value >= 0) {

				// If the staff member does not exist anymore, we skip him
				Staff staff = staffHandler.lookup(idStaff);
				if (staff == null) {
					if (log.isWarnEnabled()) {
						log.warn(String.format("Staff id %d does not exist anymore", idStaff));
					}
				} else {
					
					ExperienceDetectionTemplate edt = templates.get(idEDT);
					if (edt == null) {
						throw new ApplicationRuntimeException("WTF : edt should not be null at this stage!");
					}
		
					final int idSkill = edt.getIdSkill();
					if (log.isDebugEnabled()) {
						log.debug(String.format("Setting the level of skill/id %d for staff/id %d", idSkill, idStaff));
					}
		
					Optional<ExperienceAbacus> oExperienceAbacus = abacus.stream()
						.filter (ea -> (ea.getIdExperienceDetectionTemplate() == idEDT))
						.filter (ea -> (ea.getValue() <= value))
						.sorted((ea1, ea2) -> (ea2.getValue() - ea1.getValue()))
						.findFirst();
					if (!oExperienceAbacus.isPresent()) {
						if (log.isWarnEnabled()) {
							log.warn(String.format(
								"Cannot retrieve an entry in the abacus for the value %d of %d", value, idEDT));
						}
					} else {
						ExperienceAbacus ea = oExperienceAbacus.get();
						staffHandler.updateSkillSystemLevel(idStaff, idSkill, ea.getLevel());
						if (log.isDebugEnabled()) {
							log.debug(String.format("updateSkillSystemLevel(%d, %d, %d)", idStaff, idSkill, ea.getLevel()));
						}
					}
		
				}
			}
		}
	}
}
