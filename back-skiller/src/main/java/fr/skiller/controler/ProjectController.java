package fr.skiller.controler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import fr.skiller.data.Project;

@RestController
@RequestMapping("/project")
public class ProjectController {

	Logger logger = LoggerFactory.getLogger("backend-Projecter");

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	/**
	 * The staff collection.
	 */
	private HashMap<Integer, Project> projects;

	/**
	 * @return the Project collection.
	 */
	private Map<Integer, Project> getProjects() {
		if (this.projects != null) {
			return this.projects;
		}
		this.projects = new HashMap<Integer, Project>();
		this.projects.put(1, new Project(1, "VEGEO"));
		this.projects.put(2, new Project(2, "INFOTER"));
		return projects;
	}

	@RequestMapping(value = "/{idParam}", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
	ResponseEntity<Project> read(@PathVariable("idParam") int idParam) {

		final ResponseEntity<Project> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Project searchProject = getProjects().get(idParam);
		if (searchProject != null) {
			responseEntity = new ResponseEntity<Project>(searchProject, headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("Project read for id " + String.valueOf(idParam) + " returns " + responseEntity.getBody());
			}
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no collaborator associated to the id " + idParam);
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
		final String resultContent = g.toJson(getProjects().values());
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
		Map<Integer, Project> Projects = getProjects();

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
