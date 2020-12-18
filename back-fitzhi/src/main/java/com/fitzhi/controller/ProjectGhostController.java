package com.fitzhi.controller;

import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitzhi.Error;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyRemoveGhost;
import com.fitzhi.controller.in.BodyUpdateGhost;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Controller in charge of {@link Ghost ghosts}.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/project/ghost")
public class ProjectGhostController {


	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * <p>
	 * Manage a ghost in a project.
	 * </p>
	 */
	@PostMapping(path="/save")
	public ResponseEntity<Boolean> saveGhost(@RequestBody BodyUpdateGhost param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /api/project/ghost/save for project : %d", param.getIdProject()));
		}
		
		try {
			Project project = projectHandler.get(param.getIdProject());
			
			// We update the staff identifier associated to this ghost
			if (param.getIdStaff() > 0) {
				projectHandler.associateStaffToGhost(project, param.getPseudo(), param.getIdStaff());
				return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);	
			}

			// This ghost is technical
			if (param.isTechnical()) {
				projectHandler.setGhostTechnicalStatus(project, param.getPseudo(), true); 				
				return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);			
			}
			
			// Neither staff member, nor technical, we reset the ghost
			projectHandler.resetGhost(project, param.getPseudo()); 				
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);			
			
			
		} catch (ApplicationException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(
					Boolean.FALSE, headers,
					HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
	
	/**
	 * <p>
	 * Manage a ghost in a project.
	 * </p>
	 */
	@PostMapping(path="/remove")
	public ResponseEntity<Boolean> removeGhost(@RequestBody BodyRemoveGhost param) {
		
		HttpHeaders headers = new HttpHeaders();

		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /project/ghosts/remove for project : %d and pseudi %s", 
					param.getIdProject(), param.getPseudo()));
		}
		
		try {
			Project project = projectHandler.get(param.getIdProject());
			
			// Neither staff member, nor technical, we reset the ghost
			projectHandler.removeGhost(project, param.getPseudo()); 				
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);			
			
			
		} catch (ApplicationException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.getIdProject()));
			return new ResponseEntity<>(
					Boolean.FALSE, headers,
					HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
}
