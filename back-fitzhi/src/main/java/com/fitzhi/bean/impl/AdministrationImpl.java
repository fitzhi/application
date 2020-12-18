package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_CANNOT_SELF_CREATE_USER;
import static com.fitzhi.Error.CODE_INVALID_LOGIN_PASSWORD;
import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_LOGIN_ALREADY_EXIST;
import static com.fitzhi.Error.MESSAGE_CANNOT_SELF_CREATE_USER;
import static com.fitzhi.Error.MESSAGE_INVALID_LOGIN_PASSWORD;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_LOGIN_ALREADY_EXIST;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

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
	 * Does Fitzhì allow self registration ?
	 * <ul>
	 * <li>Either, everyone can create his own user, by simply connecting to the Fitzhì URL</li>
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
		if (log.isDebugEnabled()) {
			log.debug(String.format ("%s exists ? %b", firstConnection.toAbsolutePath(), firstConnection.toFile().exists()));
		}	
		return !firstConnection.toFile().exists();
	}

	@Override
	public void saveVeryFirstConnection() throws ApplicationException {
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
				throw new ApplicationException(CODE_IO_ERROR, MESSAGE_IO_ERROR, FIRST_CONNECTION_FILE);			
			}
		} catch (final IOException ioe) {
			throw new ApplicationException(CODE_IO_ERROR, MESSAGE_IO_ERROR, ioe, FIRST_CONNECTION_FILE);
		}
	}

	@Override
	public Staff createNewUser(String login, String password) throws ApplicationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format("createNewUser('%s','%s')", login, password));
		}
		Optional<Staff> oStaff = staffHandler.findStaffWithLogin(login);
		final Staff staff = oStaff.isPresent() ? oStaff.get() : null;
		if (log.isDebugEnabled()) {
			log.debug (String.format("Staff found %s", ((staff != null) ? staff.fullName() : "(none)") ));
		}
		final String encryptedPassword = DataEncryption.encryptMessage(password);
		
		/**
		 * The very first user created is the very first administrative user in Fitzhì.
		 * Therefore the self registration is obviously allowed
		 */
		if (isVeryFirstConnection()) {
			if (log.isDebugEnabled()) {
				log.debug (String.format("This is the very first connection in Fitzhi)"));
			}
			if (staff != null) {
				staffHandler.savePassword(staff, encryptedPassword);
				return staff;
			} else {
				return staffHandler.addNewStaffMember(new Staff(-1, login, encryptedPassword));
			}
		} 

		if (this.allowSelfRegistration)  {
			if (log.isDebugEnabled()) {
				log.debug ("So we allow the self-registration for new users; and hackers... (brrr! Dangerous !)");
			}			
			if (staff == null) {
				return staffHandler.addNewStaffMember(new Staff(-1, login, encryptedPassword));				
			} else {
				return updatePassword (staff, encryptedPassword);
			}
		} else {
			if (staff == null) {
				throw new ApplicationException(CODE_CANNOT_SELF_CREATE_USER, 
						MESSAGE_CANNOT_SELF_CREATE_USER);
			} else {
				return updatePassword (staff, encryptedPassword);
			}
		}
	}
	
	/**
	 * Update the encrypted password of a staff member
	 * @param staff the staff 
	 * @param password the chosen password.
	 * @return the Staff updated with his encrypted password.
	 * @throws ApplicationException thrown if any problem occurs
	 */
	private Staff updatePassword (final Staff staff, String password) throws ApplicationException {
		//
		// We cannot override the password of an existing user.
		// This is supposed to be the CREATION of a staff member.
		//
		if (!staff.isEmpty()) {
			throw new ApplicationException(CODE_LOGIN_ALREADY_EXIST, 
				MessageFormat.format(MESSAGE_LOGIN_ALREADY_EXIST, 
				staff.getLogin(), staff.getFirstName(), staff.getLastName()));					
		} else {
			String encryptedPassword = DataEncryption.encryptMessage(password);
			staffHandler.savePassword(staff, encryptedPassword);
			return staff;
		}		
	}
	
	@Override
	public Staff connect(String login, String password) throws ApplicationException {
		
		Staff staff = staffHandler.findStaffWithLogin(login)
				.orElseThrow(() -> new ApplicationException(CODE_INVALID_LOGIN_PASSWORD, MESSAGE_INVALID_LOGIN_PASSWORD));
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("login found %s", staff.fullName()));
		}
		
		if (!staff.isValidPassword(password)) {
			throw new ApplicationException(CODE_INVALID_LOGIN_PASSWORD, MESSAGE_INVALID_LOGIN_PASSWORD);
		}
		
		return staff;
	}

	
}
