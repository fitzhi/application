package fr.skiller.controller;

import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.Error;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyParamSonarEntry;
import fr.skiller.controller.in.BodyParamSonarFilesStats;
import fr.skiller.controller.in.BodyParamProjectSonarEvaluation;
import fr.skiller.controller.in.BodyParamProjectSonarMetricValues;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.SonarProject;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Controller in charge the collection of {@link SonarProject} declared in the project.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/project/sonar")
public class ProjectSonarController {

	@Autowired
	ProjectHandler projectHandler;
	
	@PostMapping(path="/saveEntry")
	public ResponseEntity<Boolean> saveEntry(@RequestBody BodyParamSonarEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /project/sonar/addEntry for project : %d", param.getIdProject()));
		}
		
		try {
			Project project = projectHandler.get(param.getIdProject());
			
			projectHandler.addSonarEntry(project, param.getSonarProject()); 
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
	/**
	 * load and return a SonarProject
	 * @param idProject the project identifier
	 * @param sonarKey the key of the Sonar project
	 * @return an HTTP response with the found Sonar project, or {@code null} if none is found
	 */
	@GetMapping(path="/load/{idProject}/{sonarKey}")
	public ResponseEntity<SonarProject> getSonarProject(
			@PathVariable("idProject") int idProject,
			@PathVariable("sonarKey") String sonarKey) {

		HttpHeaders headers = new HttpHeaders();

		try {
			Project project = projectHandler.get(idProject);
			
			Optional<SonarProject> oSonarProject = project.getSonarProjects()
				.stream()
				.filter(sp -> sp.getKey().equals(sonarKey))
				.findFirst();
			
			if (oSonarProject.isPresent()) {
				return  new ResponseEntity<>(oSonarProject.get(), headers, HttpStatus.OK);
			} else {
				headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_SONAR_KEY_NOFOUND));
				headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_SONAR_KEY_NOFOUND, sonarKey, idProject));
				return  new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);				
			}
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, idProject));
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/removeEntry")
	public ResponseEntity<Boolean> removeEntry(@RequestBody BodyParamSonarEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/sonar/removeEntry for project : %s %s", 
				param.getIdProject(), param.getSonarProject().getName()));
		}
		
		try {
			Project project = projectHandler.get(param.getIdProject());
			
			projectHandler.removeSonarEntry(project, param.getSonarProject()); 
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/files-stats")
	public ResponseEntity<Boolean> saveFilesStats(@RequestBody BodyParamSonarFilesStats param) {

		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/sonar/file-stats for project : %s %s", 
				param.getIdProject(), param.getSonarProjectKey()));
		}
		
		try {
			Project project = projectHandler.get(param.getIdProject());
			
			projectHandler.saveFilesStats(project, param.getSonarProjectKey(), param.getFilesStats()); 
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}

	/**
	 * Add or update the Sonar evaluation of a Sonar project
	 * @param param
	 * @return {@code TRUE} if the operation succeeded, {@code FALSE} otherwise.
	 */
	@PostMapping(path="/saveEvaluation")
	public ResponseEntity<Boolean> saveEvaluation(@RequestBody BodyParamProjectSonarEvaluation param) {
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/sonar/saveEvaluation for project : %s %s", 
				param.getIdProject(), param.getSonarKey()));
		}
		
		Project project = null;
		try {
			project = projectHandler.get(param.getIdProject());
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
		try {
			projectHandler.saveSonarEvaluation(project, param.getSonarKey(), param.getSonarEvaluation()); 
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/saveMetricValues")
	public ResponseEntity<Boolean> updateMetricValues(@RequestBody BodyParamProjectSonarMetricValues param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/sonar/saveMetricValues for project : %s %s", 
				param.getIdProject(), param.getSonarKey()));
		}
		
		Project project = null;
		try {
			project = projectHandler.get(param.getIdProject());
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
		try {
			projectHandler.saveSonarMetricValues(project, param.getSonarKey(), param.getMetricValues()); 
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
			
		
	}
	
	
}