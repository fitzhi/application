package fr.skiller.controller;

import static fr.skiller.Global.BACKEND_RETURN_CODE;
import static fr.skiller.Global.BACKEND_RETURN_MESSAGE;
import static fr.skiller.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static fr.skiller.Error.MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.BooleanDTO;
import fr.skiller.data.external.StaffDTO;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

@RestController
@RequestMapping("/admin")
/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */

public class AdminController {

	@Autowired
	private Administration administration;

	@Autowired
	private StaffHandler staffHandler;

	@GetMapping("/isVeryFirstConnection")
	public ResponseEntity<Boolean> isVeryFirstConnection()  {
		
		boolean isVeryFirstConnection = administration.isVeryFirstConnection();
		
		return new ResponseEntity<>(
				isVeryFirstConnection, 
				new HttpHeaders(), 
				HttpStatus.OK);
	}
	
	@GetMapping("/saveVeryFirstConnection")
	public ResponseEntity<BooleanDTO> keepVeryFirstConnecion()  {
		HttpHeaders headers = new HttpHeaders();
		try {
			administration.saveVeryFirstConnection();
			return new ResponseEntity<>(
					new BooleanDTO(), 
					headers, 
					HttpStatus.OK);
		} catch (final SkillerException ske) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(ske.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, ske.errorMessage);
			return new ResponseEntity<>(new BooleanDTO(ske.errorCode, ske.errorMessage), headers, HttpStatus.OK);
			
		}
	}
	
	@GetMapping("/veryFirstUser")
	public ResponseEntity<StaffDTO> veryFirstUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  {
		
		if (this.staffHandler.getStaff().isEmpty()) {
			return this.createNewUser(login, password);			
		} else {
			HttpHeaders headers = new HttpHeaders();
			headers.set(BACKEND_RETURN_CODE, String.valueOf(CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED));
			headers.set(BACKEND_RETURN_MESSAGE, MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED);
			return new ResponseEntity<>(new 
						StaffDTO(new Staff(), 
						CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED, 
						MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED), 
					headers, HttpStatus.OK);
		}
		
	}	
	
	@GetMapping("/newUser")
	public ResponseEntity<StaffDTO> createNewUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  {
		
		HttpHeaders headers = new HttpHeaders();
		try {
			Staff staff = administration.createNewUser(login, password);
			headers.add("backend.return_code", "1");
			return new ResponseEntity<>(new StaffDTO(staff), headers, HttpStatus.OK);
		} catch (final SkillerException ske) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(ske.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, ske.errorMessage);
			return new ResponseEntity<>(new StaffDTO(new Staff(), ske.errorCode, ske.errorMessage), headers, HttpStatus.OK);
		}
		
	}

	@GetMapping("/connect")
	public ResponseEntity<StaffDTO> connect(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  {
		
		HttpHeaders headers = new HttpHeaders();
		try {
			Staff staff = administration.connect(login, password);
			headers.add("backend.return_code", "1");
			return new ResponseEntity<>(new StaffDTO(staff), headers, HttpStatus.OK);
		} catch (final SkillerException ske) {
			headers.set(BACKEND_RETURN_CODE, String.valueOf(ske.errorCode));
			headers.set(BACKEND_RETURN_MESSAGE, ske.errorMessage);
			return new ResponseEntity<>(new StaffDTO(new Staff(), ske.errorCode, ske.errorMessage), headers, HttpStatus.OK);
		}
		
	}
	
}
