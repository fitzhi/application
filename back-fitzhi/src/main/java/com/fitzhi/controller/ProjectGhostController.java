package com.fitzhi.controller;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyRemoveGhost;
import com.fitzhi.controller.in.BodyUpdateGhost;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	 * @param param the proposal of association for this ghost sent to the controller
	 * @throws ApplicationException if any problem occurs during the treatment
	 */
	@PostMapping(path="/save")
	public ResponseEntity<Boolean> saveGhost(@RequestBody BodyUpdateGhost param) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /api/project/ghost/save for project : %d", param.getIdProject()));
		}
		
		Project project = projectHandler.get(param.getIdProject());
		
		// We update the staff identifier associated to this ghost
		if (param.getIdStaff() > 0) {
			projectHandler.associateStaffToGhost(project, param.getPseudo(), param.getIdStaff());
			return OK();	
		}

		// This ghost is technical
		if (param.isTechnical()) {
			projectHandler.setGhostTechnicalStatus(project, param.getPseudo(), true); 				
			return OK();			
		}
		
		// Neither staff member, nor technical, we reset the ghost
		projectHandler.resetGhost(project, param.getPseudo()); 				
		return OK();			
						
	}
	
	
	/**
	 * <p>
	 * Remove a pseudo from the ghosts list.
	 * </p>
	 * @param param the ghost to be remove from the project
	 * @throws ApplicationException if any problem occurs during the treatment
	 */
	@PostMapping(path="/remove")
	public ResponseEntity<Boolean> removeGhost(@RequestBody BodyRemoveGhost param) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST command on /project/ghosts/remove for project : %d and pseudo %s", 
					param.getIdProject(), param.getPseudo()));
		}
		
		Project project = projectHandler.get(param.getIdProject());
		
		// Neither staff member, nor technical, we reset the ghost
		projectHandler.removeGhost(project, param.getPseudo()); 				
		return OK();			
	}
	
	private ResponseEntity<Boolean> OK() {
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);
	}
}
