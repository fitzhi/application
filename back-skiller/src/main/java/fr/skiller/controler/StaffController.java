package fr.skiller.controler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.StaffDTO;
import fr.skiller.data.internal.Collaborator;
import fr.skiller.data.internal.Project;

@RestController
@RequestMapping("/staff")
public class StaffController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	@Qualifier("mock.Project")
	ProjectHandler projectHandler;

	@Autowired
	@Qualifier("mock.Staff")
	StaffHandler staffHandler;
	
	@GetMapping("/all")
	String readAll() {
		return gson.toJson(staffHandler.getStaff().values());	
	}

	/**
	 * @param idStaff staff member's identifier
	 * @return the staff member identified by its id
	 */
	@RequestMapping(value="/{idStaff}", method = RequestMethod.GET)
	ResponseEntity<Collaborator> read(@PathVariable("idStaff") int idStaff) {

		final ResponseEntity<Collaborator> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Collaborator searchCollab = staffHandler.getStaff().get(idStaff);
		if (searchCollab != null) {
			responseEntity = new ResponseEntity<Collaborator>(searchCollab, headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("read for id " + String.valueOf(idStaff) + " returns " + responseEntity.getBody());
			}
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no collaborator associated to the id " + idStaff);
			responseEntity = new ResponseEntity<Collaborator>(new Collaborator(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a staff member for id " + String.valueOf(idStaff));
			}
		}
		return responseEntity;
	}
	
	/**
	 * @param idStaff staff member's identifier
	 * @return the list of projects where the staff member is involved
	 */
	@RequestMapping(value="/projects/{idStaff}", method = RequestMethod.GET)
	ResponseEntity<List<Project>> readProjects(@PathVariable("idStaff") int idStaff) {
		
		System.out.println("here");
		
		ResponseEntity<Collaborator> responseEntityStaffMember = read(idStaff);
		
		ResponseEntity<List<Project>> response = 
				new ResponseEntity<List<Project>>(responseEntityStaffMember.getBody().projects, 
						responseEntityStaffMember.getHeaders(), 
						responseEntityStaffMember.getStatusCode());
		return response;
	}
	
	@PostMapping("/save")
	ResponseEntity<Collaborator> add(@RequestBody Collaborator input) {
		
		final ResponseEntity<Collaborator> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Collection<Collaborator> staff = staffHandler.getStaff().values();
		if (input.id == 0) {
			input.id = staff.size()+1;
			staffHandler.getStaff().put(input.id, input);
			headers.add("backend.return_code", "1");
			responseEntity = new ResponseEntity<Collaborator>(input, headers, HttpStatus.OK);
		} else {
			Collaborator updatedStaff = staffHandler.getStaff().get(input.id);
			if (updatedStaff == null) {
				responseEntity = new ResponseEntity<Collaborator>(input, headers, HttpStatus.NOT_FOUND);
				headers.add("backend.return_code", "O");
				headers.add("backend.return_message", "There is no collaborator associated to the id " + input.id);
				responseEntity.getHeaders().set("backend.return_message", "There is no collaborator associated to the id " + input.id);
				responseEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			} else {
				updatedStaff.firstName = input.firstName;
				updatedStaff.lastName = input.lastName;
				updatedStaff.nickName = input.nickName;
				updatedStaff.email = input.email;
				updatedStaff.level = input.level;
				responseEntity = new ResponseEntity<Collaborator>(input, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/save returns the body " + responseEntity.getBody());
		}
		return responseEntity;
	}
	
	/**
	 * Internal Parameters class containing all possible parameters necessaries for add/remove a project from a staff member.
	 * @author Fr&eacute;d&eacute;ric VIDAL 
	 */
	class ParamStaffProject {
		public int idStaff;
		public int idProject;		
		public String formerProjectName;
		public String newProjectName;
		@Override
		public String toString() {
			return "Param [staffId=" + idStaff + ", projectName=" + newProjectName + "]";
		}			
	}
	
	@PostMapping("/project/save")
	ResponseEntity<StaffDTO> save(@RequestBody String param) {
		
		ParamStaffProject p = gson.fromJson(param, ParamStaffProject.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/project/save with params id:" + String.valueOf(p.idStaff) + ",projectName:" + p.newProjectName);
		}
		final ResponseEntity<StaffDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Collaborator staff = staffHandler.getStaff().get(p.idStaff);
		assert (staff != null);

		Optional<Project> result = projectHandler.lookup(p.newProjectName);
		if (result.isPresent()) {
			
			/*
			 * If the passed project is already present in the staff member's project list,
			 * we send back a BAD_REQUEST to avoid duplicate entries
			 */
			Predicate<Project> predicate = pr -> (pr.id == result.get().id);
			if (staff.projects.stream().anyMatch(predicate)) {
				responseEntity = new ResponseEntity<StaffDTO>( 
				new StaffDTO(staff, 0, "The collaborator " + staff.fullName() + " is already involved in "+  p.newProjectName),
				headers, 
				HttpStatus.BAD_REQUEST);
				return responseEntity;
			}
			
			
			/**
			 *  If the user change the name of the project, 
			 *  1) we create a new entry into the projects list of the staff member
			 *  2) we remove the former entry of the previous name
			 */
			if ( (p.formerProjectName != null) && (p.formerProjectName.length() > 0) ) {
				Optional<Project> formerProject = projectHandler.lookup(p.formerProjectName);
				if (result.isPresent()) {
					staff.projects.remove(formerProject.get());
				}				
			}

			staff.projects.add(result.get());
			responseEntity = new ResponseEntity<StaffDTO>(new StaffDTO(staff), headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("returning  staff " + gson.toJson(staff));
			}
			
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a Project with the name " + p.newProjectName);
			}
			return postErrorReturnBodyMessage(404, "There is no project with the name " + p.newProjectName, staff);
		}
		return responseEntity;
	}	


	/**
	* Revoke the participation of staff member in a project. 
	*/
	@PostMapping("/project/del")
	ResponseEntity<StaffDTO> del(@RequestBody String param) {

		ParamStaffProject p = gson.fromJson(param, ParamStaffProject.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/project/del with params idStaff:" + String.valueOf(p.idStaff) + ",idProject:" + String.valueOf(p.idProject));
		}

		final Collaborator staff = staffHandler.getStaff().get(p.idStaff);
		assert (staff != null);
		if (staff == null) {
			return postErrorReturnBodyMessage(404, "There is no staff member for id" + p.idStaff);
		}
		
		Optional<Project> oProject = staff.projects.stream().filter(pr -> (pr.id == p.idProject) ).findFirst();
		if (oProject.isPresent()) {
			staff.projects.remove(oProject.get());
		}
		
		return new ResponseEntity<StaffDTO>(new StaffDTO(staff), new HttpHeaders(), HttpStatus.OK);
	}

	ResponseEntity<StaffDTO> postErrorReturnBodyMessage (int code, String message) {
		return postErrorReturnBodyMessage (code, message, new Collaborator());
	}
	
	ResponseEntity<StaffDTO> postErrorReturnBodyMessage (int code, String message, Collaborator staffMember) {
		return new ResponseEntity<StaffDTO>( 
				new StaffDTO(staffMember, code, message),
				new HttpHeaders(), 
				HttpStatus.BAD_REQUEST);
	}
}	
