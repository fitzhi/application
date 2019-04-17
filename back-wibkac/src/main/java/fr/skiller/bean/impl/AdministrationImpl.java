package fr.skiller.bean.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import static fr.skiller.Error.CODE_IO_ERROR;
import static fr.skiller.Error.MESSAGE_IO_ERROR;
import static fr.skiller.Error.CODE_LOGIN_ALREADY_EXIST;
import static fr.skiller.Error.MESSAGE_LOGIN_ALREADY_EXIST;

/**
 * Main (and unique) implementation of the administration interface.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service("admin")
public class AdministrationImpl implements Administration {

	/**
	 * The logger.
	 */
	private Logger logger = LoggerFactory.getLogger(AdministrationImpl.class.getCanonicalName());

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

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
		if (logger.isInfoEnabled()) {
			logger.info(
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

		Staff staff = staffHandler.lookup(login);

		if (staff != null) {
			throw new SkillerException(CODE_LOGIN_ALREADY_EXIST, 
					MessageFormat.format(MESSAGE_LOGIN_ALREADY_EXIST, 
							login, staff.getFirstName(), staff.getLastName()));
		}
		
		return staffHandler.addNewStaffMember(new Staff(-1, login, password));
	}

}
