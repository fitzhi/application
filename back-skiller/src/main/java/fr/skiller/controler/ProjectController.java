package fr.skiller.controler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.ProjectDTO;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;

@RestController
@RequestMapping("/project")
public class ProjectController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");

	/**
	 * Initialization of the Google JSON parser.
	 */
	private Gson g = new Gson();

	@Autowired
	@Qualifier("mock.Project")
	ProjectHandler projectHandler;

	@Autowired
	@Qualifier("mock.Skill")
	SkillHandler skillHandler;
		
	@RequestMapping(value = "/name/{projectName}", method = RequestMethod.GET)
	ResponseEntity<ProjectDTO> read(@PathVariable("projectName") String projectName) {
		
		final ResponseEntity<ProjectDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Optional<Project> result = projectHandler.lookup(projectName);
		if (result.isPresent()) {
			responseEntity = new ResponseEntity<ProjectDTO>(new ProjectDTO(result.get()), new HttpHeaders(), HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<ProjectDTO>(
					new ProjectDTO(new Project(), 404, "There is no project with the name " + projectName), 
					headers, 
					HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a Project with the name " + projectName);
			}			
		}
		return responseEntity;
	}
	
	@RequestMapping(value = "/id/{idParam}", method = RequestMethod.GET)
	ResponseEntity<Project> read(@PathVariable("idParam") int idParam) {

		final ResponseEntity<Project> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Project searchProject = projectHandler.getProjects().get(idParam);
		if (searchProject != null) {
			responseEntity = new ResponseEntity<Project>(searchProject, headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("Project read for id " + String.valueOf(idParam) + " returns " + responseEntity.getBody());
			}
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no project associated to the id " + idParam);
			responseEntity = new ResponseEntity<Project>(new Project(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a Project for id " + String.valueOf(idParam));
			}
		}
		return responseEntity;
	}

	/**
	 * @param idProject the project identifier
	 * @return the experience of a developer as list of skills.
	 */
	@RequestMapping(value="/skills/{idProject}", method = RequestMethod.GET)
	ResponseEntity<List<Skill>> get(final @PathVariable("idProject") int idProject) {
		final Project project = projectHandler.get(idProject);
		if (project == null) {
			return new ResponseEntity<List<Skill>>(new ArrayList<Skill>(), new HttpHeaders(), HttpStatus.OK);			
		} else {
			return new ResponseEntity<List<Skill>>(project.skills, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@GetMapping("/all")
	String readAll() {
		final String resultContent = g.toJson(projectHandler.getProjects().values());
		if (logger.isDebugEnabled()) {
			logger.debug("'/Project/all' is returning " + resultContent);
		}
		return resultContent;
	}

	@PostMapping("/save")
	ResponseEntity<Project> add(@RequestBody Project input) {

		final ResponseEntity<Project> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		Map<Integer, Project> Projects = projectHandler.getProjects();

		if (input.id == 0) {
			input.id = Projects.size() + 1;
			Projects.put(input.id, input);
			headers.add("backend.return_code", "1");
			responseEntity = new ResponseEntity<Project>(input, headers, HttpStatus.OK);
		} else {
			final Project searchProject = Projects.get(input.id);
			if (searchProject == null) {
				responseEntity = new ResponseEntity<Project>(input, headers, HttpStatus.NOT_FOUND);
				headers.add("backend.return_code", "O");
				responseEntity.getHeaders().set("backend.return_message",
						"There is no Project associated to the id " + input.id);
				responseEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			} else {
				searchProject.name = input.name;
				responseEntity = new ResponseEntity<Project>(input, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /project/save returns the body " + responseEntity.getBody());
		}
		return responseEntity;
	}
	
	/**
	 * Internal Parameters class containing all possible parameters necessaries for add/remove a skill from a project.
	 * @author Fr&eacute;d&eacute;ric VIDAL 
	 */
	class ParamProjectSkill {
		public int idProject;
		public int idSkill;
		public String formerSkillTitle;
		public String newSkillTitle;
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
	ResponseEntity<ProjectDTO> saveSkill(@RequestBody String param) {
		
		ParamProjectSkill p = g.fromJson(param, ParamProjectSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /project/skill/save with params id:" 
					+ String.valueOf(p.idProject) 
					+ ", new skillTitle:" + p.newSkillTitle
					+ ", former skillTitle:" + p.formerSkillTitle);
		}
		
		final Project project = projectHandler.get(p.idProject);
		assert (project != null);		
		
		final HttpHeaders headers = new HttpHeaders();
		
		if ( 		(p.formerSkillTitle != null) 
				&& 	(p.newSkillTitle != null)
				&& 	(p.formerSkillTitle.equals(p.newSkillTitle))) {
			// Nothing to DO.
			return new ResponseEntity<ProjectDTO> (new ProjectDTO(project), headers, HttpStatus.OK);
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
					Optional<Skill> oSkill = project.skills.stream().
							filter(skill -> (skill.id == formerSkill.get().id) ).
							findFirst();
					if (oSkill.isPresent()) {
						project.skills.remove(oSkill.get());
					}
				}				
			}
			
			/*
			 * If the passed skill is already present in the skills list for the project, nothing to do.
			 * otherwise we add this new skill.
			 */
			if (project.skills.stream().anyMatch(skill -> (skill.id == result.get().id))) {
				return postErrorReturnBodyMessage(HttpStatus.BAD_REQUEST.value(), 
						"The project " + project.name + " has already the skill " + p.newSkillTitle + " declared within.", project);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Adding the skill " + result.get().title + " to the project " + project.name);
				}
				project.skills.add(result.get());
				return new ResponseEntity<ProjectDTO> (new ProjectDTO(project), headers, HttpStatus.OK);
			}
						
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a skill with the name " + p.newSkillTitle);
			}
			return postErrorReturnBodyMessage(404, "There is no skill with the name " + p.newSkillTitle, project);
		}
	}	
	
	/**
	* Unregister a skill within a project.
	*/
	@PostMapping("/skills/del")
	ResponseEntity<ProjectDTO> revokeSkill(@RequestBody String param) {

		ParamProjectSkill p = g.fromJson(param, ParamProjectSkill.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/skills/del with params idProject:" + String.valueOf(p.idProject) + ",idSkill:" + String.valueOf(p.idSkill));
		}

		final Project project = projectHandler.get(p.idProject);
		assert (project != null);
		if (project == null) {
			return postErrorReturnBodyMessage(404, "There is no project registered for id" + p.idProject, new Project());
		}
		
		Optional<Skill> oSkill = project.skills.stream().filter(exp -> (exp.id == p.idSkill) ).findFirst();
		if (oSkill.isPresent()) {
			project.skills.remove(oSkill.get());
		}
		
		return new ResponseEntity<ProjectDTO>(new ProjectDTO(project), new HttpHeaders(), HttpStatus.OK);
	}
	
	
	ResponseEntity<ProjectDTO> postErrorReturnBodyMessage (int code, String message, Project project) {
		return new ResponseEntity<ProjectDTO>( 
				new ProjectDTO(project, code, message),
				new HttpHeaders(), 
				HttpStatus.BAD_REQUEST);
	}

}
