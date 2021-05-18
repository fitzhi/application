package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_SONAR_KEY_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SONAR_KEY_NOFOUND;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamProjectSonarEvaluation;
import com.fitzhi.data.internal.FilesStats;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSonarMetricValue;
import com.fitzhi.data.internal.SonarEvaluation;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;

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
import io.swagger.annotations.ApiOperation;
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
	@ApiOperation(
		value="Associate a Sonar project to a Fitzhi projet."
	)
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
	@ApiOperation(
		value="Load the Sonar metrics of a Sonar project for a given Fitzhi projet."
	)
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
	@ApiOperation(
		value="Remove a Sonar project from a Fitzhi projet."
	)
	@DeleteMapping(path="/{idProject}/sonar/{sonarKey}")
	public boolean removeEntry(
		@PathVariable("idProject") int idProject,
		@PathVariable("sonarKey") String sonarKey) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on /api/project/%d/sonar/%s", idProject, sonarKey));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.removeSonarEntry(project, sonarKey); 
		return true;
	}
	
	@ResponseBody
	@ApiOperation(
		value="Save the statistics related to all langage files detected in the Sonar server."
	)
	@PutMapping(path="{idProject}/sonar/{sonarKey}/filesStats")
	public boolean saveFilesStats(
		@PathVariable("idProject") int idProject,
		@PathVariable("sonarKey") String sonarKey,
		@RequestBody List<FilesStats> filesStats) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST verb on /api/project/%d/sonar/%s/filestats for project", idProject, sonarKey));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.saveFilesStats(project, sonarKey, filesStats); 
		return true;
			
	}

	/**
	 * Add or update the Sonar evaluation of a Sonar project
	 * @param param
	 * @return {@code TRUE} if the operation succeeded, {@code FALSE} otherwise.
	 */
	@ResponseBody
	@ApiOperation(
		value = "Add or update the Sonar evaluation of a Sonar project."
	)
	@PutMapping(path="{idProject}/sonar/{sonarKey}/evaluation")
	public boolean saveEvaluation(
		@PathVariable("idProject") int idProject,
		@PathVariable("sonarKey") String sonarKey,
		@RequestBody SonarEvaluation sonarEvaluation) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"PUT verb on /api/project/%d/sonar/%s/evaluation", idProject, sonarKey));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.saveSonarEvaluation(project, sonarKey, sonarEvaluation); 
		return true;
	}
	
	@ResponseBody
	@ApiOperation(
		value = "Save the metrics retrieved from a Sonar project."
	)
	@PutMapping(path="{idProject}/sonar/{sonarKey}/metricValues")
	public boolean updateMetricValues(
		@PathVariable("idProject") int idProject,
		@PathVariable("sonarKey") String sonarKey,
		@RequestBody List<ProjectSonarMetricValue> metricValues) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("PUT verb on /api/project/%d/sonar/%s/metricValues", idProject, sonarKey));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.saveSonarMetricValues(project, sonarKey, metricValues); 
		return true;
	}
	
	@ResponseBody
	@ApiOperation(
		value = "Save the Sonar URL where a project is registered."
	)
	@PutMapping(path="{idProject}/sonar/url")
	public boolean saveUrl(
		@PathVariable("idProject") int idProject,		
		@RequestBody String urlSonarServer) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"PUT verb on /api/project/%d/sonar/url with url %s", 
				idProject, urlSonarServer));
		}
		
		Project project = projectHandler.find(idProject);
		projectHandler.saveUrlSonarServer(project, urlSonarServer); 
		return true;
	}

}