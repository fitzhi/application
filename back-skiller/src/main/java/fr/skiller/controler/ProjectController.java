package fr.skiller.controler;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import fr.skiller.Constants;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.data.internal.Project;

@RestController
@RequestMapping("/project")
public class ProjectController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	@Autowired
	@Qualifier("mock.Project")
	ProjectHandler projectHandler;
	
	@RequestMapping(value = "/name/{projectName}", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
	ResponseEntity<Project> read(@PathVariable("projectName") String projectName) {
		
		final ResponseEntity<Project> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Optional<Project> result = projectHandler.lookup(projectName);
		if (result.isPresent()) {
			responseEntity = new ResponseEntity<Project>(result.get(), new HttpHeaders(), HttpStatus.OK);
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no project with the name " + projectName);
			responseEntity = new ResponseEntity<Project>(new Project(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a Project with the name " + projectName);
			}			
		}
		return responseEntity;
	}
	
	@RequestMapping(value = "/id/{idParam}", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
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

	@GetMapping("/all")
	@CrossOrigin(origins = "http://localhost:4200")
	String readAll() {
		final String resultContent = g.toJson(projectHandler.getProjects().values());
		if (logger.isDebugEnabled()) {
			logger.debug("'/Project/all' is returning " + resultContent);
		}
		return resultContent;
	}

	@PostMapping("/save")
	@CrossOrigin(origins = "http://localhost:4200")
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
}
