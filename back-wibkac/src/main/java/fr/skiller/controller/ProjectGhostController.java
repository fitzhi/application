package fr.skiller.controller;

import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.Error;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyRemoveGhost;
import fr.skiller.controller.in.BodyUpdateGhost;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;
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
@RequestMapping("/project/ghost")
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
			log.debug(String.format("POST command on /project/ghosts/save for project : %d", param.idProject));
		}
		
		try {
			Project project = projectHandler.get(param.idProject);
			
			// We update the staff identifier associated to this ghost
			if (param.idStaff > 0) {
				projectHandler.associateStaffToGhost(project, param.pseudo, param.idStaff);
				return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);	
			}

			// This ghost is technical
			if (param.technical) {
				projectHandler.setGhostTechnicalStatus(project, param.pseudo, true); 				
				return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);			
			}
			
			// Neither staff member, nor technical, we reset the ghost
			projectHandler.resetGhost(project, param.pseudo); 				
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);			
			
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.idProject));
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
					param.idProject, param.pseudo));
		}
		
		try {
			Project project = projectHandler.get(param.idProject);
			
			// Neither staff member, nor technical, we reset the ghost
			projectHandler.removeGhost(project, param.pseudo); 				
			return new ResponseEntity<>(Boolean.TRUE, headers, HttpStatus.OK);			
			
			
		} catch (SkillerException se) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(Error.CODE_PROJECT_NOFOUND));
			headers.set(BACKEND_RETURN_MESSAGE, MessageFormat.format(Error.MESSAGE_PROJECT_NOFOUND, param.idProject));
			return new ResponseEntity<>(
					Boolean.FALSE, headers,
					HttpStatus.INTERNAL_SERVER_ERROR);			
		}
		
	}
	
}
