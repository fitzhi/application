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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.bean.AsyncTask;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.ShuffleService;
import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.ProjectContributorDTO;
import fr.skiller.data.external.ProjectDTO;
import fr.skiller.data.external.PseudoListDTO;
import fr.skiller.data.external.SunburstDTO;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.scanner.RepoScanner;

@RestController
@RequestMapping("/project")
public class ProjectController {

	private static final String PROJECT = "project";

	private final Logger logger = LoggerFactory.getLogger(ProjectController.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	private Gson g = new Gson();

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
	 * Class used as a passed reference to a method in order to change it. The class will be used to setup a response entity.
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 * @param <T> the type of cariable top be passed
	 */
	class MyReference<T> {
		T response;
	}

	@GetMapping(path = "/name/{projectName}")
	public ResponseEntity<ProjectDTO> read(@PathVariable("projectName") String projectName) {
		
		final ResponseEntity<ProjectDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		try {
			Optional<Project> result = projectHandler.lookup(projectName);
			if (result.isPresent()) {
				responseEntity = new ResponseEntity<>(new ProjectDTO(result.get()), new HttpHeaders(), HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<>(
						new ProjectDTO(new Project(), 404, "There is no project with the name " + projectName), 
						headers, 
						HttpStatus.NOT_FOUND);
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Cannot find a Project with the name %s", projectName));
				}			
			}
			return responseEntity;
		} catch (final SkillerException e) {
			logger.error(getStackTrace(e));
			return new ResponseEntity<>(
					new ProjectDTO(new Project(), e.errorCode, e.getMessage()), 
					headers, 
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

		MyReference<ResponseEntity<Project>> refResponse = new MyReference<>();
		final Project searchProject = getProject(idProject, new Project(), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		ResponseEntity<Project> response = new ResponseEntity<>(searchProject, new HttpHeaders(), HttpStatus.OK);
		if (logger.isDebugEnabled()) {
			logger.debug(
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
		
		MyReference<ResponseEntity<List<Skill>>> refResponse = new MyReference<>();

		final Project project = getProject(idProject, new ArrayList<Skill>(), refResponse);
		return  (refResponse.response != null) ? refResponse.response : 
			new ResponseEntity<>(project.getSkills(), new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Read the project
	 * @param <T> The class of instance which will be sent within the envelop of the ResponseEntity
	 * @param idProject project identifier
	 * @param t the object to be sent back inside the ResponseEntit.
	 * @param response the response to be returned to the front if the search is unsuccessful.<br/>
	 * 			<b>This parameter is not final. This method might change its value.</b>
	 * @return the retrieved project, or {@code null} if none's found.
	 */
	<T> Project getProject(final int idProject, final T t, MyReference<ResponseEntity<T>> refResponse) {

		Project project = null;
		
		final HttpHeaders headers = new HttpHeaders();
		headers.set(BACKEND_RETURN_CODE, "O");
		headers.set(BACKEND_RETURN_MESSAGE, "No project found for the identifier " + idProject);

		try {
			project = projectHandler.get(idProject);
			if (project == null) {
				refResponse.response = new ResponseEntity<T>(t, headers, HttpStatus.NOT_FOUND);			
			} 
		} catch (final SkillerException e) {
			logger.error(getStackTrace(e));
			refResponse.response = new ResponseEntity<T>(t, headers, HttpStatus.BAD_REQUEST);
		}
		
		return project;
	}
	
	
	@GetMapping("/all")
	public String readAll() {
		try {
			
			
			Collection<Project> projects = projectHandler.getProjects().values();
			
			// Returning project
			final Collection<Project> responseProjects;
			
			if (shuffleService.isShuffleMode()) {
				responseProjects = new ArrayList<>();
				if (logger.isInfoEnabled()) {
					logger.info("The projects collection is beeing shuffled for confidentiality purpose");
				}
				projects.stream().forEach(project -> {
					final Project clone = project.clone();
					clone.setName(shuffleService.shuffle(clone.getName()));
					clone.setUsername(shuffleService.shuffle(clone.getUsername()));
					clone.setUrlRepository(shuffleService.shuffle(clone.getName()));
					responseProjects.add(clone);
				});
			} else {
				responseProjects = projects;
			}
			
			final String resultContent = g.toJson(responseProjects);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("'/Project/all' is returning %s", resultContent));
			}
			return resultContent;
		} catch (final SkillerException e) {
			logger.error(getStackTrace(e));
			return "";
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
		final HttpHeaders headers = new HttpHeaders();
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
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("POST command on /project/save returns the body %s", responseEntity.getBody()));
			}
			return responseEntity;
		} catch (final SkillerException e) {
			logger.error(getStackTrace(e));
			return new ResponseEntity<>(
					new Project(), 
					new HttpHeaders(), 
					HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Internal Parameters class containing all possible parameters necessaries for add/remove a skill from a project.
	 * @author Fr&eacute;d&eacute;ric VIDAL 
	 */
	class ParamProjectSkill {
		int idProject;
		int idSkill;
		String formerSkillTitle;
		String newSkillTitle;
		@Override
		public String toString() {
			return "ParamProjectSkill [idProject=" + idProject + ", idSkill=" + idSkill + ", formerSkillTitle="
					+ formerSkillTitle + ", newSkillTitle=" + newSkillTitle + "]";
		}
	}
	
	/**
	 * Add or change the name of skill required for a project.
	 * @param param the body of the post containing an instance of ParamProjectSkill in JSON format
	 * @see ProjectController.ParamProjectSkill
	 * @return
	 */
	@PostMapping("/skills/save")
	public ResponseEntity<ProjectDTO> saveSkill(@RequestBody String param) {
		
		ParamProjectSkill p = g.fromJson(param, ParamProjectSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"POST command on /project/skill/save with params id:%d, new skillTitle:%s, former skillTitle:%s", 
					p.idProject, p.newSkillTitle, p.formerSkillTitle));
		}
		
		MyReference<ResponseEntity<ProjectDTO>> refResponse = new MyReference<>();
		Project project = getProject(p.idProject, new ProjectDTO(new Project()), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		final HttpHeaders headers = new HttpHeaders();
		
		if ( 		(p.formerSkillTitle != null) 
				&& 	(p.newSkillTitle != null)
				&& 	(p.formerSkillTitle.equals(p.newSkillTitle))) {
			// Nothing to DO.
			return new ResponseEntity<> (new ProjectDTO(project), headers, HttpStatus.OK);
		}
		
		Optional<Skill> result = skillHandler.lookup(p.newSkillTitle);
		if (result.isPresent()) {
			
			/**
			 *  If the user change the title of the skill, 
			 *  1) we create a new entry into the skills list of the project
			 *  2) we remove the former entry assigned to the previous title.
			 *  
			 *  Below, is the code in charge of REMOVING the former skill.
			 */
			if ( 		(p.formerSkillTitle != null) 
					&& 	(p.formerSkillTitle.length() > 0) 
					&& 	(!p.formerSkillTitle.equals(p.newSkillTitle))) {
				Optional<Skill> formerSkill = skillHandler.lookup(p.formerSkillTitle);
				if (formerSkill.isPresent()) {
					Optional<Skill> oSkill = project.getSkills().stream().
							filter(skill -> (skill.getId() == formerSkill.get().getId()) ).
							findFirst();
					if (oSkill.isPresent()) {
						project.getSkills().remove(oSkill.get());
					}
				}				
			}
			
			/*
			 * If the passed skill is already present in the skills list for the project, nothing to do.
			 * otherwise we add this new skill.
			 */
			if (project.getSkills().stream().anyMatch(skill -> (skill.getId() == result.get().getId()))) {
				return postErrorReturnBodyMessage(HttpStatus.BAD_REQUEST.value(), 
						"The project " + project.getName() + " has already the skill " + p.newSkillTitle + " declared within.", project);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Adding the skill %s to the project %s", 
							result.get().getTitle(), project.getName()));
				}
				project.getSkills().add(result.get());
				return new ResponseEntity<> (new ProjectDTO(project), headers, HttpStatus.OK);
			}
						
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cannot find a skill with the name %s", p.newSkillTitle));
			}
			return postErrorReturnBodyMessage(HttpStatus.NOT_FOUND.value(), "There is no skill with the name " + p.newSkillTitle, project);
		}
	}	
	
	/**
	* Unregister a skill within a project.
	*/
	@PostMapping("/skills/del")
	public ResponseEntity<ProjectDTO> revokeSkill(@RequestBody String param) {

		ParamProjectSkill p = g.fromJson(param, ParamProjectSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
				"POST command on /staff/skills/del with params (idProject: %d, idSkill: %d)",
				p.idProject, p.idSkill));
		}

		MyReference<ResponseEntity<ProjectDTO>> refResponse = new MyReference<>();
		Project project = getProject(p.idProject, new ProjectDTO(new Project()), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		Optional<Skill> oSkill = project.getSkills().stream().filter(exp -> (exp.getId() == p.idSkill) ).findFirst();
		if (oSkill.isPresent()) {
			project.getSkills().remove(oSkill.get());
		}
		
		return new ResponseEntity<>(new ProjectDTO(project), new HttpHeaders(), HttpStatus.OK);
	}
	
	/**
	 * Parameter sent to the controller in order to obtain the sunburst data. 
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	public class SettingsGeneration {
		/**
		 * Project identifier.
		 */
		int idProject;

		/**
		 * Starting date of investigation.
		 */
		private long startingDate;
		
		/**
		 * selected staff identifier.
		 */
		private int idStaffSelected;
		
		/**
		 * ParamSunburst.
		 */
		public SettingsGeneration() {
			super();
		}

		/**
		 * @param idProject project identifier
		 */
		public SettingsGeneration(final int idProject) {
			this.idProject = idProject;
		}
		
		/**
		 * @param idProject project identifier.
		 * @param idStaffSelected staff identifier selected.
		 */
		public SettingsGeneration(final int idProject, final int idStaffSelected) {
			this.idProject = idProject;
			this.setIdStaffSelected(idStaffSelected);
		}
		
		/**
		 * @return {@code true} if the repository requires personalization, {@code false} otherwise.
		 */
		public boolean requiresPersonalization() {
			return (getIdStaffSelected() > 0 || getStartingDate() > 0);
		}


		/**
		 * @return the startingDate of investigation.
		 */
		public long getStartingDate() {
			return startingDate;
		}

		/**
		 * @return the idStaffSelected
		 */
		public int getIdStaffSelected() {
			return idStaffSelected;
		}

		/**
		 * @param idStaffSelected the idStaffSelected to set
		 */
		private void setIdStaffSelected(int idStaffSelected) {
			this.idStaffSelected = idStaffSelected;
		}

	}
	
	/**
	* Retrieve the activities for a project in an object ready made to be injected into the sunburst chart.
	*/
	@PostMapping("/sunburst")
	public ResponseEntity<SunburstDTO> retrieveRiskDashboard(@RequestBody String param) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Reception of the JSON body %s", param));
		}
		SettingsGeneration gp = g.fromJson(param, SettingsGeneration.class);
		if (logger.isDebugEnabled()) {
			logger.debug( MessageFormat.format(
				"POST command on /sunburst with params idProject : {0}, starting from {1}, for the staff member {2}",
				gp.idProject,
				(gp.getStartingDate() == 0) ? "EPOC" : new Date(gp.getStartingDate()),
				gp.getIdStaffSelected()));
		}

		MyReference<ResponseEntity<SunburstDTO>> refResponse = new MyReference<>();
		Project project = getProject(gp.idProject, new SunburstDTO(), refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		try {
			if (scanner.hasAvailableGeneration(project)) {
				return generate(project, gp);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug ("Tasks present in the tasks collection");
					logger.debug (tasks.trace());
				}
				if (tasks.containsTask(DASHBOARD_GENERATION, PROJECT, project.getId())) {
					if (logger.isDebugEnabled()) {
						logger.debug("The generation has already been called for the project " 
								+ project.getName() + ". Please wait !");
					}
					return new ResponseEntity<> (
							new SunburstDTO(project.getId(), CODE_MULTIPLE_TASK,
							"A dashboard generation has already been launched for " + project.getName()), 
							new HttpHeaders(), 
							HttpStatus.BAD_REQUEST);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("The generation will be processed asynchronously !");
				}
				scanner.generateAsync(project, gp);
				return new ResponseEntity<> (new SunburstDTO(project.getId(), null, HttpStatus.CREATED.value(), 
						"The dashboard generation has been launched. Operation might last a while. Please try later !"), 
						new HttpHeaders(), 
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			return new ResponseEntity<> (new SunburstDTO(project.getId(), null, -1, e.getMessage()), 
					new HttpHeaders(), 
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
				if (logger.isInfoEnabled()) {
					logger.info("Shuffling the sunburst data");
				}
				data.undefinedContributors.stream()
					.forEach(pseudo -> 
					pseudo.setFullName(shuffleService.shuffle(pseudo.getFullName()))
				);
			}
			return new ResponseEntity<>(
					new SunburstDTO(project.getId(), data), new HttpHeaders(), HttpStatus.OK);
		} catch (final Exception e) {
			logger.error (e.getMessage());
			return new ResponseEntity<>(new SunburstDTO( UNKNOWN_PROJECT,null, CODE_UNDEFINED, e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);			
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
		
		ProjectContributorDTO projectContributorDTO = new ProjectContributorDTO(idProject);
		
		contributors.stream().forEach(contributor -> {
			final Staff staff = staffHandler.getStaff().get(contributor.getIdStaff());
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
		if (logger.isDebugEnabled()) {
			logger.debug (String.format("Removing project with %d", idProject));
		}

		MyReference<ResponseEntity<String>> refResponse = new MyReference<>();
		Project project = getProject(idProject, "", refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}

		try {
			String response = cacheDataHandler.removeRepository(project) ? "1" : "0";
			if ("1".equals(response)) {
				scanner.generateAsync(project, new SettingsGeneration(project.getId()));
			}
			return new ResponseEntity<>( 
					response,
					new HttpHeaders(), 
					HttpStatus.OK);
		} catch (Exception e) {
			logger.error(getStackTrace(e)); 
			return new ResponseEntity<> (e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Revoke the participation of staff member in a project.
	 */
	@PostMapping("/api-ghosts")
	public ResponseEntity<PseudoListDTO> saveGhosts(@RequestBody String param) {
		
		PseudoListDTO pseudosDTO = g.fromJson(param, PseudoListDTO.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("POST command on /project/ghosts for project : %s", pseudosDTO.idProject));
			logger.debug(String.format("%d pseudos received", pseudosDTO.unknowns.size()));
		}
		try {
			List<Pseudo> pseudos = projectHandler.saveGhosts(pseudosDTO.idProject, pseudosDTO.unknowns);
			return new ResponseEntity<>( 
					new PseudoListDTO(pseudosDTO.idProject, pseudos),
					new HttpHeaders(), 
					HttpStatus.OK);
		} catch (SkillerException e) {
			logger.error(getStackTrace(e)); 
			return new ResponseEntity<> (new PseudoListDTO(pseudosDTO.idProject, e), 
					new HttpHeaders(), HttpStatus.BAD_REQUEST);
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
				new HttpHeaders(), 
				HttpStatus.BAD_REQUEST);
	}

}
