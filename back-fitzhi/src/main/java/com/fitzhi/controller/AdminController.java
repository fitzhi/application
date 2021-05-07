package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@Api(
	tags="Administration controller.",
	description = "This API provides endpoints to initialize the application as well as create and connect user into it."
)
public class AdminController extends BaseRestController {

	@Autowired
	private Administration administration;

	@Autowired
	private StaffHandler staffHandler;

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
		return new ResponseEntity<>(isVeryFirstConnection, headers(), HttpStatus.OK);
	}
	
	@PostMapping("/saveVeryFirstConnection")
	public ResponseEntity<Boolean> keepVeryFirstConnecion() throws ApplicationException {
		administration.saveVeryFirstConnection();
		return new ResponseEntity<>(true, headers(), HttpStatus.OK);
	}
	
	/**
	 * This method is used to create the first admin user.
	 * @param login the first admin user login
	 * @param password this first admin user password
	 * @throws ApplicationException thrown if any problem occurs.
	 * @return the newly created staff entry
	 */
	@GetMapping("/veryFirstUser")
	public ResponseEntity<Staff> veryFirstUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password) throws ApplicationException {
		
		if (log.isDebugEnabled() && !this.staffHandler.getStaff().isEmpty()) {
				log.debug ("the staff collection is not empty and has 'may-be' already registered users, see below...");
				log.debug("------------------------------------------------");
				this.staffHandler.getStaff().values().stream()
				.filter(staff -> staff.getPassword() != null)
				.forEach(
					staff -> log.debug(String.format("%d %s", staff.getIdStaff(), staff.getLogin())));
		}
		
		// We calculate the number of users declared with a non empty password.
		// If at least one user exists, then the first ADMIN user has already been created,
		// because this user is due to be the FIRST connected user.
		// (Some users migth already exist if they are created by the automatic crawling process) 
		long numberOfUsersAlreadyRegistered = this.staffHandler.getStaff().values()
			.stream()
			.map(Staff::getPassword)
			.filter(s -> (s != null))
			.count();
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d users are registered with a password", numberOfUsersAlreadyRegistered));
		}
		
		if (numberOfUsersAlreadyRegistered == 0) {
			return this.internalCreateNewUser(login, password);	
		} else {
			throw new ApplicationException(
				CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED, 
				MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED);
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
	public ResponseEntity<Staff> autoRegister(
			@RequestParam("login") String login,
			@RequestParam("password") String password) throws ApplicationException {
		return this.internalCreateNewUser(login, password);	
	}	
	
	@GetMapping("/newUser")
	public ResponseEntity<Staff> createNewUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  throws ApplicationException {
		return internalCreateNewUser(login, password);
	}
		
	/**
	 * Create a user and return the corresponding staff member.
	 * @param login the user login
	 * @param password the user password
	 * @return The staff member created for this user/password
	 * @throws ApplicationException thrown if any problem occurs such as, for example, 'login already registered'
	 */
	private ResponseEntity<Staff> internalCreateNewUser (String login, String password) throws ApplicationException {
		Staff staff = administration.createNewUser(login, password);
		return new ResponseEntity<>(staff, new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Connect a user
	 * @param login the given login 
	 * @param password the given password
	 * @return the staff retrieved if the server is sends back a 200 code,
	 * @throws ApplicationException
	 */
	@GetMapping("/connect")
	public ResponseEntity<Staff> connect(
			@RequestParam("login") String login,
			@RequestParam("password") String password) throws ApplicationException {
		Staff staff = administration.connect(login, password);
		return new ResponseEntity<>(staff, headers(), HttpStatus.OK);
	}	

}
