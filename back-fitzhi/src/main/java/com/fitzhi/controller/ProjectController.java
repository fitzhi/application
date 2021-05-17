package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_DASHBOARD_START;
import static com.fitzhi.Error.CODE_GIT_ERROR;
import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static com.fitzhi.Error.CODE_MULTIPLE_TASK;
import static com.fitzhi.Error.CODE_PROJECT_IS_NOT_EMPTY;
import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_DASHBOARD_START;
import static com.fitzhi.Error.MESSAGE_GIT_ERROR;
import static com.fitzhi.Error.MESSAGE_MULTIPLE_TASK_WITH_PARAM;
import static com.fitzhi.Error.MESSAGE_PROJECT_IS_NOT_EMPTY;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Error.UNKNOWN_PROJECT;
import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;
import static com.fitzhi.Global.deepClone;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.controller.util.ProjectLoader;
import com.fitzhi.data.external.ProjectContributorDTO;
import com.fitzhi.data.external.Sunburst;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.InformationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/project")
@Api(
	tags="Project controller API",
	description = "API endpoints to manage the projects declared in the application."
)
public class ProjectController extends BaseRestController {

	/**
	 * The Project management service
	 */
	@Autowired
	ProjectHandler projectHandler;

	/**
	 * The Skill management service
	 */
	@Autowired
	SkillHandler skillHandler;

	/**
	 * The Staff management service
	 */
	@Autowired
	StaffHandler staffHandler;

	/**
	 * This service stores, and provides the last result.
	 */
	@Autowired
	CacheDataHandler cacheDataHandler;

	/**
	 * This service saves, and removes the data associated to projects.
	 */
	@Autowired
	DataHandler dataHandler;

	/**
	 * This service is in charge of shuffle the data for anonymous purpose.
	 */
	@Autowired
	ShuffleService shuffleService;

	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	/**
	 * Asynchronous tasks list.
	 */
	@Autowired
	AsyncTask tasks;

	/**
	 * Utility class in charge of loading the project.
	 */
	ProjectLoader projectLoader;

	/**
	 * Initialization of the controller post-construction.
	 */
	@PostConstruct
	public void init() {
		projectLoader = new ProjectLoader(projectHandler);
	}

	/**
	 * <p>
	 * This method creates a new project.
	 * </p>
	 * @param builder the {@code Spring} URI builder
	 * @param project the project to be created
	 * @return a ResponseEntity with just the location containing the URI of the newly
	 *         created project
	 */
	@ApiOperation("Create a new project.")
	@PostMapping("")
	public ResponseEntity<Void> create(UriComponentsBuilder builder, @RequestBody Project project)
			throws ApplicationException {

		if (projectHandler.containsProject(project.getId())) {
			return new ResponseEntity<Void>(null, headers(), HttpStatus.CONFLICT);
		}
		
		project.setId(UNKNOWN_PROJECT);
		project = projectHandler.addNewProject(project);

		UriComponents uriComponents = builder.path("/api/project/{id}").buildAndExpand(project.getId());

		return ResponseEntity.created(uriComponents.toUri()).build();
	}

	/**
	 * <p>
	 * Update the project identified by the given {@link Project#getId() idProject}
	 * </p>
	 * 
	 * @param idProject the project identifier. The projet identifier is hosted in the URL in accordance with the Rest naming conventions
	 * @param project the project to update. This project is hosted inside the body of the {@code PUT} Medhod.
	 * @return an empty content for an update request
	 */
	@ApiOperation(value = "Update the given project.")
	@PutMapping("/{idProject}")
	public ResponseEntity<Void> updateProject(@PathVariable("idProject") int idProject, @RequestBody Project project)
			throws NotFoundException, ApplicationException {

		if (idProject != project.getId()) {
			throw new ApplicationRuntimeException("WTF : SHOULD NOT PASS HERE!");
		}

		if (!projectHandler.containsProject(idProject)) {
			throw new NotFoundException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}

		// You cannot anymore update an INACTIVE project
		if (!project.isActive()) {
			return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
		}

		projectHandler.saveProject(project);

		return ResponseEntity.noContent().build();

	}

	/**
	 * <p>
	 * This entry-point search a project on its name.
	 * </p>
	 * @param projectName the given name
	 * @return the retrieved project
	 * @throws ApplicationException thrown if an error occurs during the treatment
	 * @throws NotFoundException thrown if the search failed to find a project
	 */
	@ApiOperation(value = "Update the given project.", notes = "Nota bene : The returned project does not transport the password.")
	@GetMapping(path = "/name/{projectName}")
	public Project read(@PathVariable("projectName") String projectName) throws ApplicationException, NotFoundException {

		Optional<Project> result = projectHandler.lookup(projectName);
		if (!result.isPresent()) {
				throw new NotFoundException(
					CODE_PROJECT_NOFOUND, 
					String.format("Cannot find a Project with the name %s", projectName));
		}

		// We deep clone the project because we will change the password and we do not want to save this modification.
		return new Project(buildProjectWithoutPassword(result.get()));

	}

	/**
	 * Delete the project corresponding to the identifier id
	 * 
	 * @param idProject the searched project identifier
	 * @return the HTTP Response with the retrieved project, or an empty one if the
	 *         query failed.
	 * 
	 * @throws ApplicationException thrown if an error occurs during the treatment, (most probably due to an {@link IOException})
	 * @throws NotFoundException thrown if the project to delete does not exist (any more)
	 */
	@ResponseBody
	@ApiOperation("Remove the given project.")
	@DeleteMapping(value = "/{idProject}")
	public void removeProject(@PathVariable("idProject") int idProject)
			throws NotFoundException, ApplicationException {

		Project project = projectHandler.find(idProject);

		if (!project.isEmpty()) {
			throw new ApplicationException(CODE_PROJECT_IS_NOT_EMPTY,
					MessageFormat.format(MESSAGE_PROJECT_IS_NOT_EMPTY, project.getName()));
		}

		if (staffHandler.isProjectReferenced(idProject)) {
			throw new ApplicationException(CODE_PROJECT_IS_NOT_EMPTY,
					MessageFormat.format(MESSAGE_PROJECT_IS_NOT_EMPTY, project.getName()));
		}

		projectHandler.removeProject(project.getId());
	}

	/**
	 * <strong>Inactivate</strong> the project corresponding to the identifier id
	 * 
	 * @param idProject the given project identifier
	 * @return an <strong>empty</strong> {@code HTTP} response.
	 * 
	 * @throws ApplicationException thrown if an error occurs during the treatment, (most probably due to an {@link IOException})
	 * @throws NotFoundException thrown if the project to inactivate does not exist (any more?)
	 */
	@ResponseBody
	@ApiOperation(value = "Inactivate the project corresponding to the given identifier.")
	@PostMapping(value = "/{idProject}/rpc/inactivation")
	public void inactivateProject(@PathVariable("idProject") int idProject)
			throws NotFoundException, ApplicationException {

		Project project = projectHandler.find(idProject);
		
		projectHandler.inactivateProject(project);
	}

	/**
	 * <strong>Reactivation</strong> the project corresponding to the identifier id
	 * 
	 * @param idProject the given project identifier
	 * @return an <strong>empty</strong> {@code HTTP} response.
	 * 
	 * @throws ApplicationException thrown if an error occurs during the treatment, (most probably due to an {@link IOException})
	 * @throws NotFoundException thrown if the project to reactivate does not exist (any more?)
	 */
	@ApiOperation(value = "Reactivate the project corresponding to the given identifier.")
	@PostMapping(value = "/{idProject}/rpc/reactivation")
	public void reactivateProject(@PathVariable("idProject") int idProject)
			throws NotFoundException, ApplicationException {
		
		Project project = projectHandler.find(idProject);

		projectHandler.reactivateProject(project);
	}

	/**
	 * We do not allow to remove all projects
	 * 
	 * @return the HTTP Response with the retrieved project, or an empty one if the
	 *         query failed.
	 * 
	 */
	@ApiOperation("This method is not allowed.")
	@DeleteMapping()
	public ResponseEntity<Object> removeAllProjects() {
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	/**
	 * Read and return a project corresponding to the passed identifier.
	 * 
	 * @param idProject the searched project identifier
	 * @return the HTTP Response with the retrieved project, or an empty one if the
	 *         query failed.
	 * 
	 * @throws ApplicationException thrown if an error occurs during the treatment, (most probably due to an {@link IOException})
	 * @throws NotFoundException thrown if the project does not exist (any more?)
	 */
	@ResponseBody
	@ApiOperation(value = "Load and return the project corresponding to the passed identifier.")
	@GetMapping(value = "/{idProject}")
	public Project read(@PathVariable("idProject") int idProject) throws ApplicationException, NotFoundException {

		Project project = projectHandler.find(idProject);

		// We hide the password because we do not want to transport the GIT password on the network.
		return buildProjectWithoutPassword(project);
	}

	/**
	 * @param idProject the project identifier
	 * @return the experience of a developer as list of skills.
	 * @throws ApplicationException exception thrown if any problem occurs, most
	 *                          probably if the project does not exist for the given
	 *                          identifier.
	 */
	@ResponseBody
	@ApiOperation("Load and return the skills registered for the given project.")
	@GetMapping(value = "/{idProject}/skills")
	public Collection<ProjectSkill> loadSkills(final @PathVariable("idProject") int idProject)
			throws ApplicationException {

		Project project = projectHandler.find(idProject);

		return project.getSkills().values();
	}

	/**
	 * Retrieve and return the branches detected on the GIT repository 
	 * for the given project (identified by its id)
	 * 
	 * @param idProject the project identifier
	 * @return the HTTP Response with an array of branches, or an empty one if the query failed.
	 */
	@ResponseBody
	@ApiOperation(value = "Retrieve and return the branches detected on the GIT repository of the given project (identified by its ID).")
	@GetMapping(value = "/{idProject}/branches")
	public String[] branches(@PathVariable("idProject") int idProject) throws ApplicationException {

		Project project = projectHandler.find(idProject);

		final String REF_HEADS = "refs/heads/";

		Function<String, String> removeHeader  = (String s) -> {
			if (s.indexOf(REF_HEADS) != 0) {
				log.error("Unexpected ref name %s", s);
				return s;
			}
			return s.substring(REF_HEADS.length());
		};

		Collection<Ref> unfiltered_branches = this.scanner.loadBranches(project);
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d branches retrieved", unfiltered_branches.size()));
			unfiltered_branches.stream().forEach(ref -> log.debug(ref.getName()));
		}			
		
		String[] branches = unfiltered_branches
								.stream()
								.map(Ref::getLeaf)
								.map(Ref::getName)
								.filter(s -> s.contains(REF_HEADS))
								.map(removeHeader)
								.distinct()
								.collect(Collectors.toList())
								.toArray(new String[0]);
								
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d branches returned", branches.length));
			Arrays.stream(branches).forEach(branch -> log.debug(branch));
		}			
						
		return branches;
	}

	/**
	 * <p>
	 * This procedure reads and returns all projects declared in Fitzhi.
	 * </p>
	 * @return a collection of projects
	 * @throws ApplicationException throw if any problem occurs
	 */
	@ResponseBody
	@ApiOperation(value = "Loads and returns all projects declared in the application")
	@GetMapping("")
	public Collection<Project> readAll() throws ApplicationException {

		Collection<Project> projects = projectHandler.getProjects().values();

		// Returning project
		final Collection<Project> responseProjects = new ArrayList<>();
		
		if (shuffleService.isShuffleMode()) {
			if (log.isInfoEnabled()) {
				log.info("The projects collection is beeing shuffled for confidentiality purpose");
			}
			projects.stream().forEach(project -> {
				final Project clone = buildProjectWithoutPassword(project);
				clone.setName(shuffleService.shuffle(clone.getName()));
				clone.setUsername(shuffleService.shuffle(clone.getUsername()));
				clone.setUrlRepository(shuffleService.shuffle(clone.getName()));
				responseProjects.add(clone);
			});
		} else {
			for (Project project : projects) {
				responseProjects.add(buildProjectWithoutPassword(project));
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("'/Project' is returning %d projects", responseProjects.size()));
		}
		return responseProjects;

	}


	/**
	 * Test the connection settings for a given project.
	 * 
	 * @param idProject the project identifier
	 * @return {@code true} if the connection did success, {@code false} otherwise
	 */
	@ResponseBody
	@ApiOperation(value = "Test the connection settings for a given project.")
	@GetMapping(value = "/{idProject}/test")
	public boolean scmConnect(@PathVariable("idProject") int idProject) throws NotFoundException, ApplicationException {
		Project project = projectHandler.find(idProject);
		boolean connected = this.scanner.testConnection(project);
		return connected;
	}

	/**
	 * <p>
	 * Add a new skill detected in a project.
	 * </p>
	 * 
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 * 
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	@ResponseBody
	@ApiOperation(value = "Add a new skill to a project.")
	@PostMapping("{idProject}/skill/{idSkill}")
	public boolean saveSkill(
		@PathVariable("idProject") int idProject,
		@PathVariable("idSkill") int idSkill) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on %d/project/skill/%d", idProject, idSkill));
		}

		Project project = projectHandler.get(idProject);

		// Just to test if this skill exists.
		Skill skill = this.skillHandler.getSkill(idSkill);
		
		this.projectHandler.addSkill(project, new ProjectSkill(skill.getId()));

		return true;
	}

	/**
	 * <p>
	 * Unregister a skill within a project.
	 * </p>
	 * 
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 * 
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	@ResponseBody
	@ApiOperation (value = "Remove a skill from a project.")
	@DeleteMapping("{idProject}/skill/{idSkill}")
	public boolean revokeSkill(
		@PathVariable("idProject") int idProject,
		@PathVariable("idSkill") int idSkill) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on %d/staff/%d", idProject, idSkill));
		}

		Project project = projectHandler.get(idProject);
		projectHandler.removeSkill(project, idSkill);
		return true;
	}

	/**
	 * <p>
	 * Retrieve the activities for a project in an object ready made to be injected
	 * into the Sunburst chart.
	 * </p>
	 * 
	 * @param settings settings for the chart generation <i>(such as a filter on
	 *                 date, or a staff member)
	 * @return the Sunburst chart.
	 */
	@ResponseBody
	@ApiOperation(
		value = "Collect the activities of a project into a data container ready made to be injected in the Sunburst chart component.",
		notes = "This API is very coupled with an Angular component."
	)
	@PutMapping("/{idProject}/sunburst")
	public Sunburst generateChartSunburst(
		@PathVariable("idProject") int idProject,
		@RequestBody SettingsGeneration settings) throws ApplicationException, InformationException  {

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format(
				"PUT verb on /api/project/{0}/sunburst,  starting from {1}, for the staff member {2}",
				idProject,
				(settings.getStartingDate() == 0) ? "EPOC" : new Date(settings.getStartingDate()),
				settings.getIdStaffSelected()));
		}

		Project project = projectHandler.find(idProject);

		if (scanner.hasAvailableGeneration(project)) {
			return generate(project, settings);
		}

		if (log.isDebugEnabled()) {
			log.debug("Tasks already present in the tasks collection");
			log.debug(tasks.trace());
		}

		if (tasks.hasActiveTask(DASHBOARD_GENERATION, PROJECT, idProject)) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format(MESSAGE_MULTIPLE_TASK_WITH_PARAM, project.getName()));
			}
			throw new InformationException (CODE_MULTIPLE_TASK, MESSAGE_MULTIPLE_TASK_WITH_PARAM, project.getName());
		}

		if (log.isDebugEnabled()) {
			log.debug("The generation will be processed asynchronously !");
		}
		scanner.generateAsync(project, settings);

		throw new InformationException (CODE_DASHBOARD_START, MESSAGE_DASHBOARD_START);
	}

	/**
	 * Generate the dashboard.
	 * 
	 * @param project  the passed project
	 * @param settings parameters sent to the dashboard generation such as the
	 *                 starting date, or the filtered staff member.
	 * @return the generated risks dashboard.
	 */
	private Sunburst generate(final Project project, final SettingsGeneration settings) 
		throws ApplicationException, InformationException {
		
		try {
			tasks.addTask(DASHBOARD_GENERATION, PROJECT, project.getId());
		} catch (final ApplicationException e) {
			throw new InformationException(CODE_MULTIPLE_TASK,
				String.format(MESSAGE_MULTIPLE_TASK_WITH_PARAM, project.getName()), e);
		}

		try {
			RiskDashboard data = scanner.generate(project, settings);
			return new Sunburst(project.getId(), project.getStaffEvaluation(), data);
		} catch (GitAPIException gae) {
			throw new ApplicationException(CODE_GIT_ERROR, MESSAGE_GIT_ERROR, project.getId(), project.getName());
		} catch (IOException ioe) {
			throw new ApplicationException(CODE_IO_EXCEPTION, ioe.getMessage());
		} finally {
			try {
				tasks.completeTask(DASHBOARD_GENERATION, PROJECT, project.getId());
			} catch (ApplicationException e) {
				// We choke this exception. There is no alternative to a failure in completion.
				log.error("Internal error", e);
			}
		}
	}

	/**
	 * @param idProject the project identifier
	 * @return the contributors who have been involved in the project
	 */
	@GetMapping(value = "/{idProject}/contributors")
	public ResponseEntity<ProjectContributorDTO> projectContributors(final @PathVariable("idProject") int idProject) {

		final List<Contributor> contributors = projectHandler.contributors(idProject);
		if (log.isDebugEnabled()) {
			log.debug(contributors.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
					.toString());
		}

		ProjectContributorDTO projectContributorDTO = new ProjectContributorDTO(idProject);

		contributors.stream().forEach(contributor -> {
			final Staff staff = staffHandler.getStaff().get(contributor.getIdStaff());
			if (staff == null) {
				throw new ApplicationRuntimeException(
						String.format("No staff member retrieved for the id %d", contributor.getIdStaff()));
			}
			projectContributorDTO.addContributor(contributor.getIdStaff(),
					shuffleService.isShuffleMode()
							? shuffleService.shuffle(staff.getFirstName() + " " + staff.getLastName())
							: (staff.getFirstName() + " " + staff.getLastName()),
					staff.isActive(), staff.isExternal(), contributor.getFirstCommit(), contributor.getLastCommit(),
					contributor.getNumberOfCommitsSubmitted(), contributor.getNumberOfFiles());

		});

		return new ResponseEntity<>(projectContributorDTO, new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * <p>
	 * Initialize the password of the project.
	 * </p>
	 * 
	 * <i>The project has to be clone to avoid to be saved with a {@code null} value</i>
	 * z
	 * @param project the given project
	 * @return a cloned project without password
	 */
	private Project buildProjectWithoutPassword(Project project) {
		Project clone = (Project) deepClone(project);
		clone.setPassword(null);
		return clone;
	}

	/**
	 * <p>
	 * Delete the current Sunburst dashboard <em>and start the generation of a new Sunburst chart in an asynchronous mode</em>.
	 * </p>
	 * <p>
	 * This method requests for the deletion of the sunburst data, stored on the file system. 
	 * This deletion triggers the generation of a new one.
	 * </p>
	 * <p>
	 * <b>Therefore the reponse is EMPTY and has an {@link HttpStatus#ACCEPTED ACCEPTED 202} status.</b>
	 * </p>
	 * @param idProject the project identifier
	 * @return an empty reponse. 
	 * @throws NotFoundException if the project does not exist. 
	 * @throws ApplicationException if the any problem occurs, most probably an {@link IOException}
	 */
	@DeleteMapping(value = "/{idProject}/sunburst")
	public ResponseEntity<Void> resetSunburstChart(
		final @PathVariable("idProject") int idProject) throws NotFoundException, ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("Removing project with %d", idProject));
		}

		Project project = projectHandler.find(idProject);

		// We renitialize the local repository if the user asks for a RESET, comparing to a REFRESH 
		projectHandler.saveLocationRepository(idProject, null);
		// We reinitialize the project reference in the staff collection
		staffHandler.removeProject(idProject); 
		// Remove the GIT repository
		cacheDataHandler.removeRepository(project);
		// Remove all crawling intermediate files
		dataHandler.removeCrawlerFiles(project);

		// Launching the asynchronous generation
		scanner.generateAsync(project, new SettingsGeneration(project.getId()));
		
		return ResponseEntity.accepted().build();
	}

	/**
	 * <p>
	 * Generate the sunburst chart. 
	 * </p>
	 * <p>
	 * <ul>
	 * <li>This method can be invoked many times with the same result. This method is <b>idempotent</b></i></li>
	 * <li>This method updates the level of risk for the given project and the missions of the developers involved in its.</li>
	 * </ul>
	 * Therefore the REST verb is a <b>POST</b> with an empty BODY. Only the project ID is necessary.
	 * </p>
	 * <p>
	 * This API entry returns immediatly with an empty response with a {@link HttpStatus#ACCEPTED ACCEPTED 202} status. 
	 * </p>
	 * <p>
	 * The {@link  RepoScanner#generateAsync(Project, SettingsGeneration)  generation} is triggered.
	 * </p>
	 * @param idProject the project identifier
	 * @throws NotFoundException if the project does not exist. 
	 * @throws ApplicationException if the any problem occurs, most probably an {@link IOException}
	 */
	@PatchMapping(value = "/{idProject}/sunburst")
	public ResponseEntity<String> reloadSunburstChart(final @PathVariable("idProject") int idProject) throws NotFoundException, ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("Request for the generation of the Sunburst chart for project %d", idProject));
		}

		Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new NotFoundException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}

		cacheDataHandler.removeRepository(project);

		// Launching the asynchronous generation
		scanner.generateAsync(project, new SettingsGeneration(project.getId()));
		
		return ResponseEntity.accepted().build();
	}

}
