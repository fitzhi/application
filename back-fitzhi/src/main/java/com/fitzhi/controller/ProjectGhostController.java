package com.fitzhi.controller;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyRemoveGhost;
import com.fitzhi.controller.in.BodyUpdateGhost;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
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
@RequestMapping("/api/project")
@Api(
	tags="Projects Ghosts API",
	description = "API endpoints to manage the ghosts discovered in a project."
)
public class ProjectGhostController extends BaseRestController {


	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * <p>
	 * Manage a ghost in a project.
	 * </p>
	 * @param param the proposal of association for this ghost sent to the controller
	 * @throws ApplicationException if any problem occurs during the treatment
	 */
	@PostMapping(path="/ghost/save")
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
	@DeleteMapping(path="{idProject}/ghost/{pseudo}")
	public ResponseEntity<Boolean> removeGhost(
			@PathVariable("idProject") int idProject,
			@PathVariable("pseudo") String pseudo
			) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"POST command on /project/ghosts/remove for project : %d and pseudo %s", 
					idProject, pseudo));
		}
		
		Project project = projectHandler.get(idProject);
		
		// Neither staff member, nor technical, we reset the ghost
		projectHandler.removeGhost(project, pseudo); 

		return OK();			
	}
	
	private ResponseEntity<Boolean> OK() {
		return new ResponseEntity<>(Boolean.TRUE, headers(), HttpStatus.OK);
	}
}
