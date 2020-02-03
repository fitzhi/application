package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Global.BACKEND_RETURN_CODE;
import static com.fitzhi.Global.BACKEND_RETURN_MESSAGE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.external.BooleanDTO;
import com.fitzhi.data.external.StaffDTO;
import com.fitzhi.data.internal.Settings;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {


	@Autowired
	private Administration administration;

	@Autowired
	private StaffHandler staffHandler;

	Logger logger = LoggerFactory.getLogger(AdminController.class.getCanonicalName());

	/**
	 * Does Tixhì allow self registration ?
	 * <ul>
	 * <li>Either, everyone can create his own user, by simply connecting to the Tixhì URL</li>
	 * <li>Or a login must be already registered for the new user in the staff collection.</li>
	 * </ul>
	 */
	@Value("${allowSelfRegistration}")
	private boolean allowSelfRegistration;
	
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
	
	/**
	 * This method is used to create the first admin user.
	 * @param login the first admin user login
	 * @param password this first admin user password
	 * @return the newly created staff entry
	 */
	@GetMapping("/veryFirstUser")
	public ResponseEntity<StaffDTO> veryFirstUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  {
		
		if (logger.isDebugEnabled() && !this.staffHandler.getStaff().isEmpty()) {
				logger.debug ("the staff collection is not empty, see below...");
				logger.debug("------------------------------------------------");
				this.staffHandler.getStaff().values().stream().forEach(
					staff -> logger.debug(staff.toString()));
		}
		
		if (this.staffHandler.getStaff().isEmpty()) {
			return this.internalCreateNewUser(login, password);	
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

	/**
	 * This method creates a new user if the setting <code><b>allowSelfRegistration</b></code> is set 
	 * to <code>True</code>.
	 * @param login the user login
	 * @param password this user password
	 * @return the newly created staff entry
	 */
	@GetMapping("/register")
	public ResponseEntity<StaffDTO> autoRegister(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  {
		return this.internalCreateNewUser(login, password);	
	}	
	
	@GetMapping("/newUser")
	public ResponseEntity<StaffDTO> createNewUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  {
		return internalCreateNewUser(login, password);
	}
		
	/**
	 * Create a user and return the corresponding staff member.
	 * @param login the user login
	 * @param password his password
	 * @return The staff member created for this user/password
	 */
	private ResponseEntity<StaffDTO> internalCreateNewUser (String login, String password) {
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
	
	/**
	 * @return a generated header.
	 */
	private HttpHeaders headers() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return headers;
	}
}
