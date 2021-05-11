package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;
import static com.fitzhi.Error.getStackTrace;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.ProjectLoader;
import com.fitzhi.controller.util.ProjectLoader.MyReference;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
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
	tags="Projects Analysis API",
	description = "API endpoints in charge of the interaction between the front-end and the analysis processed by the back-end."
)
public class ProjectAnalysisController extends BaseRestController  {

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
	 */
	@GetMapping(value = "/{idProject}/analysis/lib-dir/{criteria}")
	public ResponseEntity<List<String>> lookup(
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

		return new ResponseEntity<>(paths, new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Add or change the name of skill required for a project.
	 * @param param the body of the post containing an instance of ParamProjectSkill in JSON format
	 * @see ProjectController.BodyParamProjectSkill
	 * @return
	 */
	@PostMapping("/analysis/lib-dir/save/{idProject}")
	public ResponseEntity<Boolean> saveLibDir(@PathVariable int idProject, @RequestBody Library[] tabLib) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /project/analysis/save/libDir/save/%d ", 
					idProject));
		}
		
		List<Library> libraries = new ArrayList<>(Arrays.asList(tabLib));
		
		MyReference<ResponseEntity<Boolean>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(idProject, Boolean.FALSE, refResponse);
		if (refResponse.getResponse() != null) {
			return refResponse.getResponse();
		}
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving the librairies of project %s", project.getName()));
			libraries.stream().map(Library::getExclusionDirectory).forEach(log::debug);
		}
		
		try {
		 this.projectHandler.saveLibraries(idProject, libraries);
		} catch (Exception e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<> (
					Boolean.FALSE, 
					new HttpHeaders(), 
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(Boolean.TRUE, new HttpHeaders(), HttpStatus.OK);
	}

	
	@GetMapping("/analysis/onboard/{idProject}/{idStaff}")
	public ResponseEntity<Boolean> onBoardStaff(@PathVariable int idProject, @PathVariable int idStaff) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("GET command on /onboard/%d/%d ", idProject, idStaff));
		}
		
		Staff staff = staffHandler.getStaff(idStaff);		
		if (staff == null) {
			throw new ApplicationException(CODE_STAFF_NOFOUND, MessageFormat.format(MESSAGE_STAFF_NOFOUND, idStaff));
		}

		Project project = projectHandler.get(idProject);
		if (project == null) {
			throw new ApplicationException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, idProject));
		}
		
		this.projectDashboardCustomizer.takeInAccountNewStaff(project, staff);
			 
		return new ResponseEntity<>(Boolean.TRUE, new HttpHeaders(), HttpStatus.OK);
	}

}
