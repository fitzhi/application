package fr.skiller.controller;

import static fr.skiller.Error.CODE_MULTIPLE_TASK;
import static fr.skiller.Error.CODE_UNDEFINED;
import static fr.skiller.Error.UNKNOWN_PROJECT;
import static fr.skiller.Error.getStackTrace;
import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.AsyncTask;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.ShuffleService;
import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.controller.in.ParamProjectSkill;
import fr.skiller.controller.in.SettingsGeneration;
import fr.skiller.controller.util.ProjectLoader;
import fr.skiller.controller.util.ProjectLoader.MyReference;
import fr.skiller.data.external.BooleanDTO;
import fr.skiller.data.external.ProjectContributorDTO;
import fr.skiller.data.external.ProjectDTO;
import fr.skiller.data.external.SunburstDTO;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

	private static final String PROJECT = "project";

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
	 * Operation.
	 */
	public static final String DASHBOARD_GENERATION = "Dashboard generation";
	
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
				responseEntity = new ResponseEntity<>(new ProjectDTO(result.get()), headers(), HttpStatus.OK);
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
	@GetMapping(value = "/id/{idParam}")
	public ResponseEntity<Project> read(@PathVariable("idParam") int idProject) {

		MyReference<ResponseEntity<Project>> refResponse = projectLoader.new MyReference<>();
		final Project searchProject = projectLoader.getProject(idProject, new Project(), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		ResponseEntity<Project> response = new ResponseEntity<>(searchProject, headers(), HttpStatus.OK);
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
	public ResponseEntity<List<Skill>> get(final @PathVariable("idProject") int idProject) {
		
		MyReference<ResponseEntity<List<Skill>>> refResponse = projectLoader.new MyReference<>();

		final Project project = projectLoader.getProject(idProject, new ArrayList<Skill>(), refResponse);
		return  (refResponse.response != null) ? refResponse.response : 
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
					final Project clone = new Project(project);
					clone.setName(shuffleService.shuffle(clone.getName()));
					clone.setUsername(shuffleService.shuffle(clone.getUsername()));
					clone.setUrlRepository(shuffleService.shuffle(clone.getName()));
					responseProjects.add(clone);
				});
			} else {
				responseProjects = projects;
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
			if (project.getId() == 0) {
				project = projectHandler.addNewProject(project);
				headers.add(BACKEND_RETURN_CODE, "1");
				responseEntity = new ResponseEntity<>(project, headers, HttpStatus.OK);
			} else {
				if (!projectHandler.containsProject(project.getId())) {
					responseEntity = new ResponseEntity<>(project, headers, HttpStatus.NOT_FOUND);
					headers.add(BACKEND_RETURN_CODE, "O");
					responseEntity.getHeaders().set(BACKEND_RETURN_MESSAGE,
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
	 * <p>Add a new skill required for a project.</p>
	 * @param projectSkill the body of the post containing an instance of ParamProjectSkill in JSON format
	 * @see ProjectController.ParamProjectSkill
	 * @return
	 */
	@PostMapping("/skill/add")
	public ResponseEntity<BooleanDTO> saveSkill(@RequestBody ParamProjectSkill projectSkill) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /project/skill/add with params idProject:%d, idSkill:%d", 
					projectSkill.idProject, projectSkill.idSkill));
		}
		
		MyReference<ResponseEntity<BooleanDTO>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(projectSkill.idProject, new BooleanDTO(), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		try {
			Skill skill = this.skillHandler.getSkill(projectSkill.idSkill);
			this.projectHandler.addSkill(project, skill);	
			return new ResponseEntity<BooleanDTO>(new BooleanDTO(), headers(), HttpStatus.OK);
		} catch (final SkillerException ske) {
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Cannot save the skill %d inside the project %s", projectSkill.idSkill, project.getName()));
				log.debug (ske.errorMessage);
			}
			return new ResponseEntity<BooleanDTO>(
					new BooleanDTO(-1, String.format("There is no skill with id " + projectSkill.idSkill)), 
					headers(), HttpStatus.BAD_REQUEST);
		}
	}	
	
	/**
	* <p>Unregister a skill within a project.</p>
	* @param param an instance of {@link ParamProjectSkill} containing the project identifier and the skill identifier
	*/
	@PostMapping("/skill/del")
	public ResponseEntity<BooleanDTO> revokeSkill(@RequestBody ParamProjectSkill projectSkill) {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /staff/skills/del with params (idProject: %d, idSkill: %d)",
				projectSkill.idProject, projectSkill.idSkill));
		}

		MyReference<ResponseEntity<BooleanDTO>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(projectSkill.idProject, new BooleanDTO(), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		projectHandler.removeSkill(project, projectSkill.idSkill);
		
		return new ResponseEntity<>(new BooleanDTO(), headers(), HttpStatus.OK);
	}
	
	
	/**
	* Retrieve the activities for a project in an object ready made to be injected into the sunburst chart.
	*/
	@PostMapping("/sunburst")
	public ResponseEntity<SunburstDTO> retrieveRiskDashboard(@RequestBody SettingsGeneration settings) {

		if (log.isDebugEnabled()) {
			log.debug( MessageFormat.format(
				"POST command on /sunburst with params idProject : {0}, starting from {1}, for the staff member {2}",
				settings.getIdProject(),
				(settings.getStartingDate() == 0) ? "EPOC" : new Date(settings.getStartingDate()),
				settings.getIdStaffSelected()));
		}

		MyReference<ResponseEntity<SunburstDTO>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(settings.getIdProject(), new SunburstDTO(), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		try {
			if (scanner.hasAvailableGeneration(project)) {
				return generate(project, settings);
			} else {
				if (log.isDebugEnabled()) {
					log.debug ("Tasks present in the tasks collection");
					log.debug (tasks.trace());
				}
				if (tasks.containsTask(DASHBOARD_GENERATION, PROJECT, project.getId())) {
					if (log.isDebugEnabled()) {
						log.debug("The generation has already been called for the project " 
								+ project.getName() + ". Please wait !");
					}
					return new ResponseEntity<> (
							new SunburstDTO(project.getId(), project.getRisk(), CODE_MULTIPLE_TASK,
							"A dashboard generation has already been launched for " + project.getName()), 
							headers(), 
							HttpStatus.BAD_REQUEST);
				}
				if (log.isDebugEnabled()) {
					log.debug("The generation will be processed asynchronously !");
				}
				scanner.generateAsync(project, settings);
				return new ResponseEntity<> (new SunburstDTO(project.getId(), project.getRisk(), null, HttpStatus.CREATED.value(), 
						"The dashboard generation has been launched. Operation might last a while. Please try later !"), 
						headers(), 
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<> (new SunburstDTO(project.getId(), project.getRisk(), null, -1, e.getMessage()), 
					headers(), 
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Generate the dashboard.
	 * @param project the passed project
	 * @param settings parameters sent to the dashboard generation as staring date, filtered staff member.
	 * @return the generated risks dashboard.
	 */
	private ResponseEntity<SunburstDTO> generate (final Project project, final SettingsGeneration settings) {
		try {
			tasks.addTask( DASHBOARD_GENERATION, PROJECT, project.getId());
			RiskDashboard data = scanner.generate(project, settings);
			if (shuffleService.isShuffleMode()) {
				if (log.isInfoEnabled()) {
					log.info("Shuffling the sunburst data");
				}
			}
			return new ResponseEntity<>(
					new SunburstDTO(project.getId(), project.getRisk(), data), new HttpHeaders(), HttpStatus.OK);
		} catch (final Exception e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<>(new SunburstDTO( UNKNOWN_PROJECT, -1, null, CODE_UNDEFINED, e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);			
		} finally {
			tasks.removeTask(DASHBOARD_GENERATION, PROJECT, project.getId());
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
		if (refResponse.response != null) {
			return refResponse.response;
		}

		try {
			projectHandler.saveLocationRepository(idProject, null);
			String response = cacheDataHandler.removeRepository(project) ? "1" : "0";
			if ("1".equals(response)) {
				scanner.generateAsync(project, new SettingsGeneration(project.getId()));
			}
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
