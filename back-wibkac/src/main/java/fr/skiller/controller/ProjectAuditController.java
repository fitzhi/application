package fr.skiller.controller;

import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

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

import fr.skiller.bean.ProjectAuditHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyParamAuditEntry;
import fr.skiller.data.internal.AuditTopic;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Controller in charge the collection of {@link AuditTopic} declared in the project.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/project/audit")
public class ProjectAuditController {

	@Autowired
	ProjectAuditHandler projectAuditHandler;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@PostMapping(path="/saveTopic")
	public ResponseEntity<Boolean> saveTopic(@RequestBody BodyParamAuditEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/addTopic for project.id %d and topic.id %d", 
						param.getIdProject(), param.getAuditTopic().getId()));
		}
		
		try {
			projectAuditHandler.addTopic(param.getIdProject(), param.getAuditTopic().getId());
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
	/**
	 * load and return an {@link AuditTopic}
	 * @param idProject the project identifier
	 * @param sonarKey the key of the Sonar project
	 * @return an HTTP response with the found Sonar project, or {@code null} if none is found
	 */
	@GetMapping(path="/loadTopic/{idProject}/{idTopic}")
	public ResponseEntity<AuditTopic> getTopicAudit(
			@PathVariable("idProject") int idProject,
			@PathVariable("idTopic") int idTopic) {

		HttpHeaders headers = new HttpHeaders();

		try {
			AuditTopic auditProject = projectAuditHandler.getTopic(idProject, idTopic);
			return  new ResponseEntity<>(auditProject, headers, HttpStatus.OK);
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@PostMapping(path="/removeTopic")
	public ResponseEntity<Boolean> removeTopic(@RequestBody BodyParamAuditEntry param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"POST command on /project/audit/removeTopic for project.id %d and topic.id %d", 
						param.getIdProject(), param.getAuditTopic().getId()));
		}
		
		try {
			projectAuditHandler.removeTopic(param.getIdProject(), param.getAuditTopic().getId(), false);
			
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(se.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, se.errorMessage);
			return new ResponseEntity<>(Boolean.FALSE, headers, HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
		
}