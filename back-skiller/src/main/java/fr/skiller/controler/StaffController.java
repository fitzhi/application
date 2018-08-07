package fr.skiller.controler;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import fr.skiller.data.Collaborator;

@RestController
@RequestMapping("/staff")
public class StaffController {

	Logger logger = LoggerFactory.getLogger("backend-skiller");
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson g = new Gson();

	/**
	 * The staff collection.
	 */
	private ArrayList<Collaborator> staff;
	
	/**
	 * @return the staff collection.
	 */
	private List<Collaborator> getStaff() {
		if (this.staff != null) {
			return this.staff;
		}
		this.staff = new ArrayList<Collaborator>();
		staff.add(new Collaborator(1,
		    "Frederic",
		    "VIDAL",
		    "altF4",
		    "frvidal@sqli.com",
		    "ET2"));
		staff.add(new Collaborator(2,
		    "Olivier",
		    "MANFE",
		    "la Mouf",
		    "omanfe@sqli.com",
		    "ICD 3"));
		staff.add(new Collaborator(3,
		    "Alexandre",
		    "JOURDES",
		    "Jose",
		    "ajourdes@sqli.com",
		    "ICD 2"));
		staff.add(new Collaborator(4,
			    "Thomas",
			    "LEVAVASSEUR",
			    "Grg",
			    "tlavavasseur@sqli.com",
			    "ICD 4"));
		staff.add(new Collaborator(5,
		    "Christophe",
		    "OPOIX",
		    "Copo",
		    "ocopoix@sqli.com",
		    "ET 2"));
		return staff;
	}
	
	@RequestMapping(value="/{idParam}", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
	ResponseEntity<Collaborator> read(@PathVariable("idParam") int idParam) {

		final ResponseEntity<Collaborator> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		Optional<Collaborator> searchCollab = getStaff().stream().filter (c -> (c.id == idParam)).findFirst();
		if (searchCollab.isPresent()) {
			responseEntity = new ResponseEntity<Collaborator>(searchCollab.get(), headers, HttpStatus.OK);
			if (logger.isDebugEnabled()) {
				logger.debug("read for id " + String.valueOf(idParam) + " returns " + responseEntity.getBody());
			}
		} else {
			headers.set("backend.return_code", "O");
			headers.set("backend.return_message", "There is no collaborator associated to the id " + idParam);
			responseEntity = new ResponseEntity<Collaborator>(new Collaborator(), headers, HttpStatus.NOT_FOUND);
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find a staff member for id " + String.valueOf(idParam));
			}
		}
		return responseEntity;
	}
	
	@GetMapping("/all")
	@CrossOrigin(origins = "http://localhost:4200")
	String readAll() {
		return g.toJson(getStaff());	
	}

	@PostMapping("/save")
	@CrossOrigin(origins = "http://localhost:4200")
	ResponseEntity<Collaborator> add(@RequestBody Collaborator input) {
		
		final ResponseEntity<Collaborator> responseEntity;
		final HttpHeaders headers = new HttpHeaders();
		
		List<Collaborator> staff = getStaff();
		if (input.id == 0) {
			input.id = staff.size()+1;
			getStaff().add(input);
			headers.add("backend.return_code", "1");
			responseEntity = new ResponseEntity<Collaborator>(input, headers, HttpStatus.OK);
		} else {
			List<Collaborator> updatedStaff = staff.stream().filter(collab -> (collab.id == input.id)).collect(Collectors.toList());
			if (updatedStaff.size() != 1) {
				responseEntity = new ResponseEntity<Collaborator>(input, headers, HttpStatus.NOT_FOUND);
				headers.add("backend.return_code", "O");
				headers.add("backend.return_message", "There is no collaborator associated to the id " + input.id);
				responseEntity.getHeaders().set("backend.return_message", "There is no collaborator associated to the id " + input.id);
				responseEntity.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
			} else {
				updatedStaff.get(0).firstName = input.firstName;
				updatedStaff.get(0).lastName = input.lastName;
				updatedStaff.get(0).nickName = input.nickName;
				updatedStaff.get(0).email = input.email;
				updatedStaff.get(0).level = input.level;
				responseEntity = new ResponseEntity<Collaborator>(input, headers, HttpStatus.OK);
				headers.add("backend.return_code", "1");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("POST command on /staff/save returns the body " + responseEntity.getBody());
		}
		return responseEntity;
	}
	
	
	
}	
