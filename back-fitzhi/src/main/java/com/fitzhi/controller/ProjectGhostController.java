package com.fitzhi.controller;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.GhostAssociation;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * Controller in charge of {@link Ghost ghosts}.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
@Api(
	tags="Project Ghosts controller API",
	description = "API endpoints to manage the ghosts discovered in a project."
)
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
	@ResponseBody
	@ApiOperation(
		value = "Save a ghost in the project."
	)
	@PostMapping(path="{idProject}/ghost")
	public boolean saveGhost(
		@PathVariable("idProject") int idProject,
		@RequestBody GhostAssociation association) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("POST verb on /api/project/%d/ghost for project", idProject));
		}
		
		Project project = projectHandler.get(idProject);
		
		// We update the staff identifier associated to this ghost.
		if (association.getIdStaff() > 0) {
			projectHandler.associateStaffToGhost(project, association.getPseudo(), association.getIdStaff());
			return true;	
		}

		// This ghost is technical.
		if (association.isTechnical()) {
			projectHandler.setGhostTechnicalStatus(project, association.getPseudo(), true); 				
			return true;			
		}
		
		// Neither staff member, nor technical, we reset the ghost.
		projectHandler.resetGhost(project, association.getPseudo());
				
		return true;			
	}
	
	/**
	 * <p>
	 * Remove a pseudo from the ghosts list.
	 * </p>
	 * @param idProject the Project identifier
	 * @param pseudo the pseudo to remove
	 * 
	 * @throws ApplicationException if any problem occurs during the treatment
	 */
	@ResponseBody
	@ApiOperation(
		value = "Remove a ghost from the project."
	)
	@DeleteMapping(path="{idProject}/ghost/{pseudo}")
	public boolean removeGhost(
			@PathVariable("idProject") int idProject,
			@PathVariable("pseudo") String pseudo
			) throws ApplicationException {
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("DELETE verb on /api/project/%d/ghosts/%s", idProject, pseudo));
		}
		
		Project project = projectHandler.get(idProject);
		
		// Neither staff member, nor technical, we reset the ghost
		projectHandler.removeGhost(project, pseudo); 

		return true;			
	}
}
