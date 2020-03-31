package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_MULTIPLE_TASK;
import static com.fitzhi.Error.CODE_UNDEFINED;
import static com.fitzhi.Error.UNKNOWN_PROJECT;
import static com.fitzhi.Error.getStackTrace;
import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;
import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;
import static com.fitzhi.Global.deepClone;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.in.BodyParamProjectSkill;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.controller.util.ProjectLoader;
import com.fitzhi.controller.util.ProjectLoader.MyReference;
import com.fitzhi.data.external.BooleanDTO;
import com.fitzhi.data.external.ProjectContributorDTO;
import com.fitzhi.data.external.ProjectDTO;
import com.fitzhi.data.external.SunburstDTO;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController {

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	SkillHandler skillHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	@Autowired
	CacheDataHandler cacheDataHandler;

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
	
	@GetMapping(path = "/name/{projectName}")
	public ResponseEntity<ProjectDTO> read(@PathVariable("projectName") String projectName) {
		
		final ResponseEntity<ProjectDTO> responseEntity;
		try {
			Optional<Project> result = projectHandler.lookup(projectName);
			if (result.isPresent()) {
				Project project = (Project) deepClone(result.get());
				project.setPassword(null);
				responseEntity = new ResponseEntity<>(
						new ProjectDTO(buildProjectWithoutPassword(result.get())), headers(), HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<>(
						new ProjectDTO(new Project(), 404, "There is no project with the name " + projectName), 
						headers(), 
						HttpStatus.NOT_FOUND);
				if (log.isDebugEnabled()) {
					log.debug(String.format("Cannot find a Project with the name %s", projectName));
				}			
			}
			return responseEntity;
		} catch (final SkillerException e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<>(
					new ProjectDTO(new Project(), e.errorCode, e.getMessage()), 
					headers(), 
					HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Read and return a project corresponding to the passed identifier
	 * @param idProject the searched project identifier
	 * @return the HTTP Response with the retrieved project, or an empty one if the query failed.
	 */
	@GetMapping(value = "/id/{idProject}")
	public ResponseEntity<Project> read(@PathVariable("idProject") int idProject) {

		MyReference<ResponseEntity<Project>> refResponse = projectLoader.new MyReference<>();
		final Project searchProject = projectLoader.getProject(idProject, new Project(), refResponse);
		if (refResponse.getResponse() != null) {
			return refResponse.getResponse();
		}

		ResponseEntity<Project> response = new ResponseEntity<>(
				buildProjectWithoutPassword(searchProject), headers(), HttpStatus.OK);
		if (log.isDebugEnabled()) {
			log.debug(
					String.format("Project corresponding to the id %d has returned %s", 
							idProject, response.getBody()));
		}
		return response;
	}

	/**
	 * @param idProject the project identifier
	 * @return the experience of a developer as list of skills.
	 */
	@GetMapping(value="/skills/{idProject}")
	public ResponseEntity<Set<Skill>> get(final @PathVariable("idProject") int idProject) {
		
		MyReference<ResponseEntity<Set<Skill>>> refResponse = projectLoader.new MyReference<>();

		final Project project = projectLoader.getProject(idProject, new HashSet<Skill>(), refResponse);
		return  (refResponse.getResponse() != null) ? refResponse.getResponse() : 
			new ResponseEntity<>(project.getSkills(), headers(), HttpStatus.OK);
	}	
	
	@GetMapping("/all")
	public Collection<Project> readAll() {
		try {
			Collection<Project> projects = projectHandler.getProjects().values();
			
			// Returning project
			final Collection<Project> responseProjects;
			
			if (shuffleService.isShuffleMode()) {
				responseProjects = new ArrayList<>();
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
				responseProjects = new ArrayList<>();
				for (Project project : projects) {
					responseProjects.add(buildProjectWithoutPassword(project));			
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("'/Project/all' is returning %d projects", responseProjects.size()));
			}
			return responseProjects;
			
		} catch (final SkillerException e) {
			log.error(getStackTrace(e));
			return new ArrayList<Project>();
		}

	}
	
	/**
	 * Add or Update the given project.
	 * @param project the passed project
	 * @return the updated or the just new project created
	 */
	@PostMapping("/save")
	public ResponseEntity<Project> save(@RequestBody Project project) {

		final ResponseEntity<Project> responseEntity;
		final HttpHeaders headers = headers();
		try {
			if (project.getId() <= 0) {
				project = projectHandler.addNewProject(project);
				headers.add(BACKEND_RETURN_CODE, "1");
				responseEntity = new ResponseEntity<>(project, headers, HttpStatus.OK);
			} else {
				if (!projectHandler.containsProject(project.getId())) {
					responseEntity = new ResponseEntity<>(project, headers, HttpStatus.NOT_FOUND);
					headers.add(BACKEND_RETURN_CODE, "O");
					headers.set(BACKEND_RETURN_MESSAGE,
							"There is no Project associated to the id " + project.getId());
				} else {
					projectHandler.saveProject(project);
					responseEntity = new ResponseEntity<>(project, headers, HttpStatus.OK);
					headers.add(BACKEND_RETURN_CODE, "1");
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("POST command on /project/save returns the body %s", responseEntity.getBody()));
			}
			return responseEntity;
		} catch (final SkillerException e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<>(
					new Project(), 
					headers(), 
					HttpStatus.BAD_REQUEST);
		}
	}
		
	/**
	 * Test the connection settings for a given project.
	 * @param idProject the project identifier
	 * @return {@code true} if the 
	 */
	@GetMapping(value="/test/{idProject}")
	public ResponseEntity<Boolean> test(@PathVariable("idProject") int idProject) {
		final HttpHeaders headers = headers();
		try {
			final Project project = projectHandler.get(idProject);
			boolean connected = this.scanner.testConnection(project);
			return new ResponseEntity<>(connected, headers, HttpStatus.OK);			
		} catch (SkillerException e) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(e.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, e.getMessage());
			return new ResponseEntity<>(false, headers, HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * <p>Add a new skill required for a project.</p>
	 * @param projectSkill the body of the post containing an instance of ParamProjectSkill in JSON format
	 * @see ProjectController.BodyParamProjectSkill
	 * @return
	 */
	@PostMapping("/skill/add")
	public ResponseEntity<BooleanDTO> saveSkill(@RequestBody BodyParamProjectSkill projectSkill) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /project/skill/add with params idProject:%d, idSkill:%d", 
					projectSkill.getIdProject(), projectSkill.getIdSkill()));
		}
		
		MyReference<ResponseEntity<BooleanDTO>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(projectSkill.getIdProject(), new BooleanDTO(), refResponse);
		if (refResponse.getResponse() != null) {
			return refResponse.getResponse();
		}
		
		try {
			Skill skill = this.skillHandler.getSkill(projectSkill.getIdSkill());
			this.projectHandler.addSkill(project, skill);	
			return new ResponseEntity<BooleanDTO>(new BooleanDTO(), headers(), HttpStatus.OK);
		} catch (final SkillerException ske) {
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Cannot save the skill %d inside the project %s", projectSkill.getIdSkill(), project.getName()));
				log.debug (ske.errorMessage);
			}
			return new ResponseEntity<BooleanDTO>(
					new BooleanDTO(-1, String.format("There is no skill with id " + projectSkill.getIdSkill())), 
					headers(), HttpStatus.BAD_REQUEST);
		}
	}	
	
	/**
	* <p>Unregister a skill within a project.</p>
	* @param param an instance of {@link BodyParamProjectSkill} containing the project identifier and the skill identifier
	*/
	@PostMapping("/skill/del")
	public ResponseEntity<BooleanDTO> revokeSkill(@RequestBody BodyParamProjectSkill projectSkill) {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /staff/skills/del with params (idProject: %d, idSkill: %d)",
				projectSkill.getIdProject(), projectSkill.getIdSkill()));
		}

		MyReference<ResponseEntity<BooleanDTO>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(projectSkill.getIdProject(), new BooleanDTO(), refResponse);
		if (refResponse.getResponse() != null) {
			return refResponse.getResponse();
		}
		
		projectHandler.removeSkill(project, projectSkill.getIdSkill());
		
		return new ResponseEntity<>(new BooleanDTO(), headers(), HttpStatus.OK);
	}
	
	/**
	 * <p>
	 * Retrieve the activities for a project in an object ready made to be injected into the Sunburst chart.
	 * </p>
	 * @param settings settings for the chart generation <i>(such as a filter on date, or a staff member)
	 * @return the Sunburst chart.
	 */
	@PostMapping("/sunburst")
	public ResponseEntity<SunburstDTO> generateChartSunburst(@RequestBody SettingsGeneration settings) {

		if (log.isDebugEnabled()) {
			log.debug( MessageFormat.format(
				"POST command on /sunburst with params idProject : {0}, starting from {1}, for the staff member {2}",
				settings.getIdProject(),
				(settings.getStartingDate() == 0) ? "EPOC" : new Date(settings.getStartingDate()),
				settings.getIdStaffSelected()));
		}

		MyReference<ResponseEntity<SunburstDTO>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(settings.getIdProject(), new SunburstDTO(), refResponse);
		if (refResponse.getResponse() != null) {
			return refResponse.getResponse();
		}
		
		try {
			if (scanner.hasAvailableGeneration(project)) {
				return generate(project, settings);
			} else {
				if (log.isDebugEnabled()) {
					log.debug ("Tasks present in the tasks collection");
					log.debug (tasks.trace());
				}
				if (tasks.hasActiveTask(DASHBOARD_GENERATION, PROJECT, project.getId())) {
					if (log.isDebugEnabled()) {
						log.debug("The generation has already been called for the project " 
								+ project.getName() + ". Please wait !");
					}
					return new ResponseEntity<> (
							new SunburstDTO(project.getId(), project.getStaffEvaluation(), CODE_MULTIPLE_TASK,
							"A dashboard generation has already been launched for " + project.getName()), 
							headers(), 
							HttpStatus.OK);
				}
				if (log.isDebugEnabled()) {
					log.debug("The generation will be processed asynchronously !");
				}
				scanner.generateAsync(project, settings);
				return new ResponseEntity<> (new SunburstDTO(project.getId(), project.getStaffEvaluation(), null, 
						HttpStatus.CREATED.value(), 
						"The dashboard generation has been launched. Operation might last a while. Please try later !"), 
						headers(), 
						HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<> (new SunburstDTO(project.getId(), project.getStaffEvaluation(), null, -1, e.getMessage()), 
					headers(), 
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Generate the dashboard.
	 * @param project the passed project
	 * @param settings parameters sent to the dashboard generation such as the starting date, or the filtered staff member.
	 * @return the generated risks dashboard.
	 */
	private ResponseEntity<SunburstDTO> generate (final Project project, final SettingsGeneration settings) {
		try {
			tasks.addTask(DASHBOARD_GENERATION, PROJECT, project.getId());
		} catch (final Exception e) {
			return new ResponseEntity<>(
				new SunburstDTO(project.getId(), project.getStaffEvaluation(), CODE_MULTIPLE_TASK,
				"A dashboard generation has already been launched for " + project.getName()), 
				headers(), 
				HttpStatus.OK);
		}
		try {
			RiskDashboard data = scanner.generate(project, settings);
			if (shuffleService.isShuffleMode()) {
				if (log.isInfoEnabled()) {
					log.info("Shuffling the sunburst data");
				}
			}
			return new ResponseEntity<>(
					new SunburstDTO(project.getId(), project.getStaffEvaluation(), data), new HttpHeaders(), HttpStatus.OK);
		} catch (final Exception e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<>(new SunburstDTO( UNKNOWN_PROJECT, -1, null, CODE_UNDEFINED, e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);			
		} finally {
			try {
				tasks.completeTask(DASHBOARD_GENERATION, PROJECT, project.getId());
			} catch (SkillerException e) {
				log.error(e.errorMessage);
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * @param idProject the project identifier
	 * @return the contributors who have been involved in the project
	 */
	@GetMapping(value="/contributors/{idProject}")
	public ResponseEntity<ProjectContributorDTO> projectContributors(final @PathVariable("idProject") int idProject) {

		final List<Contributor> contributors = projectHandler.contributors(idProject);
		if (log.isDebugEnabled()) {
			log.debug(
					contributors.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
					.toString());
		}
		
		ProjectContributorDTO projectContributorDTO = new ProjectContributorDTO(idProject);
		
		contributors.stream().forEach(contributor -> {
			final Staff staff = staffHandler.getStaff().get(contributor.getIdStaff());
			if (staff == null) {
				throw new SkillerRuntimeException(String.format("No staff member retrieved for the id %d", contributor.getIdStaff()));
			}
			projectContributorDTO.addContributor(
							contributor.getIdStaff(), 
							shuffleService.isShuffleMode() ? shuffleService.shuffle(staff.getFirstName() + " " + staff.getLastName()) : (staff.getFirstName() + " " + staff.getLastName()), 
							staff.isActive(),
							staff.isExternal(),
							contributor.getFirstCommit(), 
							contributor.getLastCommit(), 
							contributor.getNumberOfCommitsSubmitted(),
							contributor.getNumberOfFiles());
			
		});
		
		return new ResponseEntity<>(projectContributorDTO, new HttpHeaders(), HttpStatus.OK);
	}
	
	/**
	 * Remove the password of the project.<br/>
	 * <i>The project has to be clone to avoid the deletion</i> 
	 * @param project the given project
	 * @return a cloned project without password
	 */
	private Project buildProjectWithoutPassword (Project project) {
		Project clone = (Project) deepClone(project);
		clone.setPassword(null);
		return clone;
	}
	
	/**
	 * @param idProject the project identifier
	 * @return the contributors who have been involved in the project
	 */
	@GetMapping(value="/resetDashboard/{idProject}")
	public ResponseEntity<String> resetDashboard(final @PathVariable("idProject") int idProject) {
		if (log.isDebugEnabled()) {
			log.debug (String.format("Removing project with %d", idProject));
		}
		MyReference<ResponseEntity<String>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(idProject, "", refResponse);
		if (refResponse.getResponse() != null) {
			return refResponse.getResponse();
		}

		try {
			projectHandler.saveLocationRepository(idProject, null);
			String response = cacheDataHandler.removeRepository(project) ? "1" : "0";
			scanner.generateAsync(project, new SettingsGeneration(project.getId()));
			return new ResponseEntity<>( 
					response,
					new HttpHeaders(), 
					HttpStatus.OK);
		} catch (Exception e) {
			log.error(getStackTrace(e)); 
			return new ResponseEntity<> (e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
	}
		
	/**
	 * @param code the error code
	 * @param message the error message
	 * @param project the project, if any, concerned by the error, or an empty project if none exist
	 * @return a response entity
	 */
	public ResponseEntity<ProjectDTO> postErrorReturnBodyMessage (int code, String message, Project project) {
		return new ResponseEntity<>( 
				new ProjectDTO(project, code, message),
				headers(), 
				HttpStatus.BAD_REQUEST);
	}

	
	/**
	 * @return a generated HTTP Headers for the response
	 */
	private HttpHeaders headers() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return headers;
	}
}
