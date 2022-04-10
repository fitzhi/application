package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.CODE_INVALID_OPENID_SERVER;
import static com.fitzhi.Error.MESSAGE_INVALID_OPENID_SERVER;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.ClassicCredentials;
import com.fitzhi.data.internal.OpenIdCredentials;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.google.TokenHandler;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;

import java.text.MessageFormat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Controller for Ping purpose
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@Api(
	tags="Administration controller API",
	description = "This API provides endpoints to initialize the application as well as create and connect user into it."
)
public class AdminController {

	@Autowired
	private Administration administration;

	@Autowired
	private StaffHandler staffHandler;

	@Autowired
	@Qualifier("GOOGLE")
	private TokenHandler googleTokenHandler;

	/**
	 * Does Fitzhi allow self registration ?
	 * <ul>
	 * <li>Either, everyone can create his own user, by simply connecting to the Tixhì URL</li>
	 * <li>Or a login must be already registered for the new user in the staff collection.</li>
	 * </ul>
	 */
	@Value("${allowSelfRegistration}")
	private boolean allowSelfRegistration;
	
	@ResponseBody
	@ApiOperation(
		value = "Test if the user is running the installation process of Fitzhi. This is the VERY FIRST connection."
	)
	@GetMapping("/isVeryFirstConnection")
	public boolean isVeryFirstConnection()  {
		return administration.isVeryFirstConnection();
	}
	
	@ResponseBody
	@ApiOperation(
		value = "Save a flag to register the very first connection. The installation is complete."
	)
	@PostMapping("/saveVeryFirstConnection")
	public boolean keepVeryFirstConnecion() throws ApplicationException {
		administration.saveVeryFirstConnection();
		return true;
	}
	
	/**
	 * This method is used to create the first admin user.
	 * @param login the first admin user login
	 * @param password this first admin user password
	 * @throws ApplicationException thrown if any problem occurs.
	 * @return the newly created staff entry
	 */
	@ResponseBody
	@ApiOperation(
		value="Create the FIRST admin user for Fitzhi with the classic way (user/password). This creation is executed during the installation."
	)
	@PostMapping("/classic/primeRegister")
	public Staff veryFirstUser(@RequestBody ClassicCredentials classicCredentials) throws ApplicationException {
		
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
			return this.internalCreateNewUser(classicCredentials.getLogin(), classicCredentials.getPassword());	
		} else {
			throw new ApplicationException(
				CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED, 
				MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED);
		}
	}	

	/**
	 * This method is used to create the first admin user.
	 * @param login the first admin user login
	 * @param password this first admin user password
	 * @throws ApplicationException thrown if any problem occurs.
	 * @return the newly created staff entry
	 */
	@ResponseBody
	@ApiOperation(
		value="Create the FIRST admin user for Fitzhi from the OpenId JWT. This creation is executed during the installation."
	)
	@PostMapping("/openId/primeRegister")
	public Staff veryFirstUser(@RequestBody OpenIdCredentials openIdToken) throws ApplicationException {

		if (GOOGLE_OPENID_SERVER.equals(openIdToken.getOpenIdServer())) {
			HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
			OpenIdToken oit = googleTokenHandler.takeInAccountToken(openIdToken.getIdToken(), HTTP_TRANSPORT, GsonFactory.getDefaultInstance());
			Staff staff = staffHandler.createStaffMember(oit);
			googleTokenHandler.storeStaffToken(staff, oit);
			return staff;
		}

		throw new ApplicationException(CODE_INVALID_OPENID_SERVER, MessageFormat.format(MESSAGE_INVALID_OPENID_SERVER, openIdToken.getOpenIdServer()));
	}

	/**
	 * This method creates a new user if the setting <code><b>allowSelfRegistration</b></code> is set 
	 * to <code>True</code>.
	 * @param login the user login
	 * @param password this user password
	 * @return the newly created staff entry
	 */
	@ResponseBody
	@ApiOperation(
		value="Creates a new user.",
		notes = " This creation is allowed if the global setting 'allowSelfRegistration' is set to 'True'."
	)
	@PostMapping("/classic/register")
	public Staff autoRegister(@RequestBody ClassicCredentials credentials) throws ApplicationException {
		return this.internalCreateNewUser(credentials.getLogin(), credentials.getPassword());	
	}	
	
	@ResponseBody
	@ApiOperation(
		value="Creates a new user inside the application."
	)
	@PostMapping("/newUser")
	public Staff createNewUser(
			@RequestParam("login") String login,
			@RequestParam("password") String password)  throws ApplicationException {
		return internalCreateNewUser(login, password);
	}
		
	/**
	 * Connect a user
	 * @param login the given login 
	 * @param password the given password
	 * @return the staff retrieved if the server is sends back a 200 code,
	 * @throws ApplicationException
	 */
	@ResponseBody
	@ApiOperation(
		value="Connect a user and return the corresponding staff."
	)
	@GetMapping("/connect")
	public Staff connect(
			@RequestParam("login") String login,
			@RequestParam("password") String password) throws ApplicationException {
		Staff staff = administration.connect(login, password);
		return staff;
	}	

	/**
	 * Create a user and return the corresponding staff member.
	 * @param login the user login
	 * @param password the user password
	 * @return The staff member created for this user/password
	 * @throws ApplicationException thrown if any problem occurs such as, for example, 'login already registered'
	 */
	private Staff internalCreateNewUser (String login, String password) throws ApplicationException {
		Staff staff = administration.createNewUser(login, password);
		return staff;
	}



}
