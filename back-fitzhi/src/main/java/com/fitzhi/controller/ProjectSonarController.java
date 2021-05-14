package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_SONAR_KEY_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SONAR_KEY_NOFOUND;

import java.text.MessageFormat;
import java.util.Optional;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamProjectSonarEvaluation;
import com.fitzhi.controller.in.BodyParamProjectSonarMetricValues;
import com.fitzhi.controller.in.BodyParamProjectSonarServer;
import com.fitzhi.controller.in.BodyParamSonarFilesStats;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
@RequestMapping("/api/project")
@Api(
	tags="Project Sonar controller API",
	description = "API endpoints to retrieve the Sonar metrics linked to their Fitzhi projects counterparts."
)
public class ProjectSonarController {

	@Autowired
	ProjectHandler projectHandler;
	
	@ResponseBody
	@PutMapping(path="/{idProject}/sonar/{sonarKey}")
	public boolean saveEntry(
		@PathVariable("idProject") int idProject,
		@PathVariable("sonarKey") String sonarKey,
		@RequestBody SonarProject sonarProject) throws ApplicationException  {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("PUT verb on /api/project/%d/sonar/%s", idProject, sonarKey));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.addSonarEntry(project, sonarProject); 
		return true;
	}
	
	/**
	 * <p>
	 * load and return a SonarProject.
	 * </p>
	 * @param idProject the project identifier
	 * @param sonarKey the key of the Sonar project
	 * @return a Sonar project corresponding to tge given key
	 */
	@ResponseBody
	@GetMapping(path="/sonar/load/{idProject}/{sonarKey}")
	public SonarProject getSonarProject(
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
		return oSonarProject.get();
	}
	
	@ResponseBody
	@DeleteMapping(path="/{idProject}/sonar/{sonarKey}")
	public boolean removeEntry(
		@PathVariable("idProject") int idProject,
		@PathVariable("sonarKey") String sonarKey) throws ApplicationException, NotFoundException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on /api/project/%d/sonar/%s", idProject, sonarKey));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.removeSonarEntry(project, sonarKey); 
		return true;
	}
	
	@ResponseBody
	@PostMapping(path="/sonar/files-stats")
	public boolean saveFilesStats(@RequestBody BodyParamSonarFilesStats param) 
		throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/file-stats for project : %s %s", 
				param.getIdProject(), param.getSonarProjectKey()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		projectHandler.saveFilesStats(project, param.getSonarProjectKey(), param.getFilesStats()); 
		return true;
			
	}

	/**
	 * Add or update the Sonar evaluation of a Sonar project
	 * @param param
	 * @return {@code TRUE} if the operation succeeded, {@code FALSE} otherwise.
	 */
	@ResponseBody
	@PostMapping(path="/sonar/saveEvaluation")
	public boolean saveEvaluation(@RequestBody BodyParamProjectSonarEvaluation param) 
		throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/saveEvaluation for project : %s %s", 
				param.getIdProject(), param.getSonarKey()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		projectHandler.saveSonarEvaluation(project, param.getSonarKey(), param.getSonarEvaluation()); 
		return true;
	}
	
	@ResponseBody
	@PostMapping(path="/sonar/saveMetricValues")
	public boolean updateMetricValues(@RequestBody BodyParamProjectSonarMetricValues param) 
		throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/saveMetricValues for project : %s %s", 
				param.getIdProject(), param.getSonarKey()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		projectHandler.saveSonarMetricValues(project, param.getSonarKey(), param.getMetricValues()); 
		return true;
	}
	
	@ResponseBody
	@PostMapping(path="/sonar/saveUrl")
	public boolean saveUrlSonarServer(@RequestBody BodyParamProjectSonarServer param) 
		throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /api/project/sonar/saveUrl for ID Project %d & url %s", 
				param.getIdProject(), param.getUrlSonarServer()));
		}
		
		Project project = projectHandler.find(param.getIdProject());
		projectHandler.saveUrlSonarServer(project, param.getUrlSonarServer()); 
		return true;
	}

}