package fr.skiller.controler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
	 * Internal Parameters class
	 * @author Fr&eacute;d&eacute;ric VIDAL 
	 */
	class Param {
		public int staffId;
		public String projectName;
		@Override
		public String toString() {
			return "Param [staffId=" + staffId + ", projectName=" + projectName + "]";
		}			
	}
	
	@PostMapping("/project/save")
	ResponseEntity<StaffDTO> add(@RequestBody String param) {
		
		Param p = gson.fromJson(param, Param.class);
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /project/staff/save with params id:" + String.valueOf(p.staffId) + ",projectName:" + p.projectName);
		}
		final ResponseEntity<StaffDTO> responseEntity;
		final HttpHeaders headers = new HttpHeaders();

		final Collaborator staff = staffHandler.getStaff().get(p.staffId);
		assert (staff != null);

		Optional<Project> result = projectHandler.lookup(p.projectName);
		if (result.isPresent()) {
			staff.projects.add(result.get());
			responseEntity = new ResponseEntity<StaffDTO>(new StaffDTO(staff), headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("returning  staff " + gson.toJson(staff));
			}
		} else {
			responseEntity = new ResponseEntity<StaffDTO>( 
					new StaffDTO(staff, 0, "There is no project with the name " + p.projectName),
					headers, 
					HttpStatus.BAD_REQUEST);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a Project with the name " + p.projectName);
			}			
		}
		return responseEntity;
		 
	}	
	
}	
