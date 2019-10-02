package fr.skiller.controller;

import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.Error;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyParamSonarEntry;
import fr.skiller.controller.in.BodyParamSonarFilesStats;
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
			System.out.println(param.getSonarProject());
		}
		
		try {
			Project project = projectHandler.get(param.getIdProject());
			
			projectHandler.saveSonarEntry(project, param.getSonarProject()); 
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
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
}