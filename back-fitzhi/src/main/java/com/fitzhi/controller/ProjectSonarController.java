package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_SONAR_KEY_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SONAR_KEY_NOFOUND;

import java.text.MessageFormat;
import java.util.Optional;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamProjectSonarEvaluation;
import com.fitzhi.controller.in.BodyParamProjectSonarMetricValues;
import com.fitzhi.controller.in.BodyParamProjectSonarServer;
import com.fitzhi.controller.in.BodyParamSonarEntry;
import com.fitzhi.controller.in.BodyParamSonarFilesStats;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
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
@RequestMapping("/api/project/sonar")
@Api(
	tags="Projects Sonnar API.",
	description = "API endpoints to retrieve the Sonar metrics linked to their Fitzhi projects counterparts."
)
public class ProjectSonarController extends BaseRestController {

	@Autowired
	ProjectHandler projectHandler;
	
	@PostMapping(path="/saveEntry")
	public ResponseEntity<Boolean> saveEntry(@RequestBody BodyParamSonarEntry param) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /api/project/sonar/addEntry for project : %d", param.getIdProject()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		
		projectHandler.addSonarEntry(project, param.getSonarProject()); 
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
		
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
			@PathVariable("sonarKey") String sonarKey) throws ApplicationException {

		Project project = projectHandler.find(idProject);
		
		Optional<SonarProject> oSonarProject = project.getSonarProjects()
			.stream()
			.filter(sp -> sp.getKey().equals(sonarKey))
			.findFirst();
		
		if (!oSonarProject.isPresent()) {
			throw new ApplicationException(
				CODE_SONAR_KEY_NOFOUND, 
				MessageFormat.format(MESSAGE_SONAR_KEY_NOFOUND, sonarKey, idProject));
		}
		return new ResponseEntity<>(oSonarProject.get(), headers(), HttpStatus.OK);
	}
	
	@PostMapping(path="/removeEntry")
	public ResponseEntity<Boolean> removeEntry(@RequestBody BodyParamSonarEntry param) 
		throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/removeEntry for project : %s %s", 
				param.getIdProject(), 
				param.getSonarProject().getName()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		
		projectHandler.removeSonarEntry(project, param.getSonarProject()); 
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
	}
	
	@PostMapping(path="/files-stats")
	public ResponseEntity<Boolean> saveFilesStats(@RequestBody BodyParamSonarFilesStats param) 
		throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/file-stats for project : %s %s", 
				param.getIdProject(), param.getSonarProjectKey()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		
		projectHandler.saveFilesStats(project, param.getSonarProjectKey(), param.getFilesStats()); 
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
			
	}

	/**
	 * Add or update the Sonar evaluation of a Sonar project
	 * @param param
	 * @return {@code TRUE} if the operation succeeded, {@code FALSE} otherwise.
	 */
	@PostMapping(path="/saveEvaluation")
	public ResponseEntity<Boolean> saveEvaluation(@RequestBody BodyParamProjectSonarEvaluation param) 
		throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/saveEvaluation for project : %s %s", 
				param.getIdProject(), param.getSonarKey()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		
		projectHandler.saveSonarEvaluation(project, param.getSonarKey(), param.getSonarEvaluation()); 
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
	}
	
	@PostMapping(path="/saveMetricValues")
	public ResponseEntity<Boolean> updateMetricValues(@RequestBody BodyParamProjectSonarMetricValues param) 
		throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/saveMetricValues for project : %s %s", 
				param.getIdProject(), param.getSonarKey()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		projectHandler.saveSonarMetricValues(project, param.getSonarKey(), param.getMetricValues()); 
		
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);

	}
	
	@PostMapping(path="/saveUrl")
	public ResponseEntity<Boolean> saveUrlSonarServer(@RequestBody BodyParamProjectSonarServer param) 
		throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/saveUrl for ID Project %d & url %s", 
				param.getIdProject(), param.getUrlSonarServer()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		projectHandler.saveUrlSonarServer(project, param.getUrlSonarServer()); 
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
	}

}