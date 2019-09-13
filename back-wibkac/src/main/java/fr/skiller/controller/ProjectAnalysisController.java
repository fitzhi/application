package fr.skiller.controller;

import static fr.skiller.Error.getStackTrace;
import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.bean.ProjectDashboardCustomizer;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.util.ProjectLoader;
import fr.skiller.controller.util.ProjectLoader.MyReference;
import fr.skiller.data.internal.Library;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * <p>
 * Controller in charge of the interaction between the front-end and the
 * analysis processed by the back-end.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RestController
@RequestMapping("/project/analysis")
public class ProjectAnalysisController {

	private final Logger log = LoggerFactory.getLogger(ProjectAnalysisController.class.getCanonicalName());

	@Autowired
	ProjectDashboardCustomizer dashboardCustomizer;

	@Autowired
	ProjectHandler projectHandler;

	/**
	 * Utility class in charge of loading the project.
	 */
	ProjectLoader projectLoader;

	/**
	 * Initialization of the controller post-construction.
	 */
	@PostConstruct
	public void init() {
		projectLoader = new ProjectLoader(projectHandler);
	}

	/**
	 * Lookup all directories from the repository starting with a given criteria.
	 * @param idProject the project identifier
	 * @param criteria the given searched criteria
	 */
	@GetMapping(value = "/lib-dir/lookup")
	public ResponseEntity<List<String>> libDir(
			final @RequestParam("idProject") int idProject,
			final @RequestParam("criteria") String criteria) throws SkillerException {

		MyReference<ResponseEntity<List<String>>> refResponse = projectLoader.new MyReference<>();

		final Project project = projectLoader.getProject(idProject, new ArrayList<String>(), refResponse);
		if (refResponse.response != null) {
			if (log.isDebugEnabled()) {
				log.debug (String.format("Project not found for id %d" , idProject));
			} 
			return refResponse.response;
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("scanning the directories from %s", project.getLocationRepository()));
		}
		
		try {

			List<String> paths = this.dashboardCustomizer.lookupPathRepository(project, criteria);
			if (log.isDebugEnabled()) {
				log.debug(String.format("Resulting paths starting with %s", criteria));
				paths.stream().forEach(log::debug);
			}

			return new ResponseEntity<>(paths, new HttpHeaders(), HttpStatus.OK);

		} catch (final SkillerException e) {

			log.error(getStackTrace(e));

			final HttpHeaders headers = new HttpHeaders();
			headers.set(BACKEND_RETURN_CODE, "O");
			headers.set(BACKEND_RETURN_MESSAGE, e.getMessage());
			return new ResponseEntity<>(new ArrayList<String>(), headers, HttpStatus.BAD_REQUEST);

		}
	}

	/**
	 * Add or change the name of skill required for a project.
	 * @param param the body of the post containing an instance of ParamProjectSkill in JSON format
	 * @see ProjectController.ParamProjectSkill
	 * @return
	 */
	@PostMapping("/lib-dir/save/{idProject}")
	public ResponseEntity<Boolean> saveLibDir(@PathVariable int idProject, @RequestBody Library[] tabLib) {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /project/analysis/save/libDir/save/%d ", 
					idProject));
		}
		
		List<Library> libraries = Arrays.asList(tabLib);
		
		MyReference<ResponseEntity<Boolean>> refResponse = projectLoader.new MyReference<>();
		Project project = projectLoader.getProject(idProject, Boolean.FALSE, refResponse);
		if (refResponse.response != null) {
			return refResponse.response;
		}
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving the librairies of project %s", project.getName()));
			libraries.stream().map(Library::getExclusionDirectory).forEach(log::debug);
		}
		
		try {
		 this.projectHandler.saveLibraries(idProject, libraries);
		} catch (Exception e) {
			log.error(getStackTrace(e));
			return new ResponseEntity<> (Boolean.FALSE, 
					new HttpHeaders(), 
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(Boolean.TRUE, new HttpHeaders(), HttpStatus.OK);
	}
}
