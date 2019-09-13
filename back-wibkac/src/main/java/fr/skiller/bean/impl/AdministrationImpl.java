package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_CANNOT_SELF_CREATE_USER;
import static fr.skiller.Error.CODE_INVALID_LOGIN_PASSWORD;
import static fr.skiller.Error.CODE_IO_ERROR;
import static fr.skiller.Error.CODE_LOGIN_ALREADY_EXIST;
import static fr.skiller.Error.MESSAGE_CANNOT_SELF_CREATE_USER;
import static fr.skiller.Error.MESSAGE_INVALID_LOGIN_PASSWORD;
import static fr.skiller.Error.MESSAGE_IO_ERROR;
import static fr.skiller.Error.MESSAGE_LOGIN_ALREADY_EXIST;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import lombok.extern.slf4j.Slf4j;

/**
 * Main (and unique) implementation of the administration interface.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service("admin")
public class AdministrationImpl implements Administration {

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	/**
	 * Does Wibkac allow self registration ?
	 * <ul>
	 * <li>Either, everyone can create his own user, by simply connecting to the Wibkac URL</li>
	 * <li>Or a login must be already registered for the new user in the staff collection.</li>
	 * </ul>
	 */
	@Value("${allowSelfRegistration}")
	private boolean allowSelfRegistration;
	
	/**
	 * Name of the footprint file, which proves that the very first connection has been realized. 
	 */
	private static final String FIRST_CONNECTION_FILE = "connection.txt";
	
	/**
	 * Handler in charge of the staff.
	 */
	@Autowired
	StaffHandler staffHandler;
	
	@Override
	public boolean isVeryFirstConnection() {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve(FIRST_CONNECTION_FILE);
		return !firstConnection.toFile().exists();
	}

	@Override
	public void saveVeryFirstConnection() throws SkillerException {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve(FIRST_CONNECTION_FILE);
		if (log.isInfoEnabled()) {
			log.info(
					String.format(
					"Saving the footprint file for the first connection %s",
					firstConnection.toAbsolutePath().toString()));
		}
		try {
			if (!firstConnection.toFile().createNewFile()) {
				throw new SkillerException(CODE_IO_ERROR, MESSAGE_IO_ERROR, FIRST_CONNECTION_FILE);			
			}
		} catch (final IOException ioe) {
			throw new SkillerException(CODE_IO_ERROR, MESSAGE_IO_ERROR, ioe, FIRST_CONNECTION_FILE);
		}
	}

	@Override
	public Staff createNewUser(String login, String password) throws SkillerException {

		Optional<Staff> oStaff = staffHandler.findStaffWithLogin(login);
		final Staff staff = oStaff.isPresent() ? oStaff.get() : null;
		
		/**
		 * The very first user created is the very first administrative user in Wibkac.
		 * Therefore the self registration is obviously allowed
		 */
		if (isVeryFirstConnection()) {
			if (staff != null) {
				staff.setPassword(password);
				return staff;
			} else {
				return staffHandler.addNewStaffMember(new Staff(-1, login, password));
			}
		} 

		if (this.allowSelfRegistration)  {
			if (staff == null) {
				return staffHandler.addNewStaffMember(new Staff(-1, login, password));				
			} else {
				return updatePassword (staff, password);
			}
		} else {
			if (staff == null) {
				throw new SkillerException(CODE_CANNOT_SELF_CREATE_USER, 
						MESSAGE_CANNOT_SELF_CREATE_USER);
			} else {
				return updatePassword (staff, password);
			}
		}
	}
	
	private Staff updatePassword (final Staff staff, String password) throws SkillerException {
		// We cannot override an existing user
		if (!staff.isEmpty()) {
			throw new SkillerException(CODE_LOGIN_ALREADY_EXIST, 
				MessageFormat.format(MESSAGE_LOGIN_ALREADY_EXIST, 
				staff.getLogin(), staff.getFirstName(), staff.getLastName()));					
		} else {
			staff.setPassword(password);
			return staff;
		}		
	}
	
	@Override
	public Staff connect(String login, String password) throws SkillerException {
		
		Staff staff = staffHandler.findStaffWithLogin(login)
				.orElseThrow(() -> new SkillerException(CODE_INVALID_LOGIN_PASSWORD, MESSAGE_INVALID_LOGIN_PASSWORD));
		
		if (!staff.isValidPassword(password)) {
			throw new SkillerException(CODE_INVALID_LOGIN_PASSWORD, MESSAGE_INVALID_LOGIN_PASSWORD);
		}
		
		return staff;
	}

	
}
