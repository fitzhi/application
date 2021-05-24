package com.fitzhi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.ProjectLoader;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Controller in charge of the interaction between the front-end and the
 * analysis processed by the back-end.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
@Api(
	tags="Project Analysis controller API",
	description = "API endpoints in charge of the interaction between the front-end and the analysis processed by the back-end."
)
public class ProjectAnalysisController  {

	@Autowired
	ProjectDashboardCustomizer dashboardCustomizer;

	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;

	@Autowired
	ProjectHandler projectHandler;

	/**
	 * Utility class in charge of loading the project.
	 */
	ProjectLoader projectLoader;

	/**
	 * Utility class in charge of handling the staff.
	 */
	@Autowired
	StaffHandler staffHandler;
	
	/**
	 * Service in charge of the generation of the rising skyline data.
	 */
	@Autowired
	SkylineProcessor skylineProcessor;

	/**
	 * Initialization of the controller post-construction.
	 */
	@PostConstruct
	public void init() {
		projectLoader = new ProjectLoader(projectHandler);
	}

	/**
	 * <p>
	 * Lookup all directories from the repository starting with a given criteria.
	 * </p>
	 * 
	 * @param idProject the project identifier
	 * @param criteria the given searched criteria
	 * 
	 * @return {@code true} if the operation success, {@code false} otherwise
	 */
	@ResponseBody
	@ApiOperation(
		value = "Lookup all directories from the repository starting with a given criteria."
	)
	@GetMapping(value = "/{idProject}/analysis/lib-dir/{criteria}")
	public List<String> lookup(
			final @PathVariable("idProject") int idProject,
			final @PathVariable("criteria") String criteria) throws ApplicationException {

		final Project project = projectHandler.get(idProject);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Scanning the directories from %s", project.getLocationRepository()));
		}
		
		List<String> paths = this.dashboardCustomizer.lookupPathRepository(project, criteria);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Resulting paths starting with %s", criteria));
			paths.stream().forEach(log::debug);
		}

		return paths;
	}

	/**
	 * <p>
	 * Save a library dependency for a project project.
	 * </p>
	 * @param idProject the project identifier
	 * @see Library
	 * @return
	 */
	@ResponseBody
	@ApiOperation(
		value = "Save the dependency libraries of the project."
	)
	@PostMapping("{idProject}/analysis/lib-dir")
	public ResponseEntity<Boolean> saveLibDir(@PathVariable int idProject, @RequestBody Library[] tabLib) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /project/analysis/save/libDir/save/%d ", idProject));
		}
		
		List<Library> libraries = new ArrayList<>(Arrays.asList(tabLib));
		Project project = projectHandler.get(idProject);
		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving the librairies of project %s", project.getName()));
			libraries.stream().map(Library::getExclusionDirectory).forEach(log::debug);
		}
		
		 this.projectHandler.saveLibraries(idProject, libraries);
		 return new ResponseEntity<>(Boolean.TRUE, new HttpHeaders(), HttpStatus.OK);
	}

	
	@ResponseBody
	@ApiOperation(
		value = "Onboard a staff member in the project."
	)
	@PostMapping("{idProject}/analysis/onboard/{idStaff}")
	public boolean onBoardStaff(
		@PathVariable int idProject, 
		@PathVariable int idStaff) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST verb on /api/project/%d/analysis/onboard/%d", idProject, idStaff));
		}
		
		Staff staff = staffHandler.getStaff(idStaff);	

		Project project = projectHandler.find(idProject);		
		this.projectDashboardCustomizer.takeInAccountNewStaff(project, staff);
			 
		return true;
	}

}
