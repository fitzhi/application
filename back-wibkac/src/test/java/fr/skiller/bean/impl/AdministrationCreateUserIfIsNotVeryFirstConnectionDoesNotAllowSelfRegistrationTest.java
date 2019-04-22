/**
 * 
 */
package fr.skiller.bean.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
/**
 * Test the administration bean for the user creation.<br/>
 * <span style="color:red;font-size:16">IF IT IS THE VERY FIRST CONNECTION !</span>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "allowSelfRegistration=false" }) 
public class AdministrationCreateUserIfIsNotVeryFirstConnectionDoesNotAllowSelfRegistrationTest {

	private static final String MY_LOGIN = "UNREGISTERED";

	@Autowired
	Administration administration;

	@Autowired
	StaffHandler staffHandler;
		
	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	private final Logger logger = LoggerFactory.getLogger(AdministrationCreateUserIfIsNotVeryFirstConnectionDoesNotAllowSelfRegistrationTest.class.getCanonicalName());

	int idStaff;

	@Before
	public void before() throws IOException {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		if ( (!firstConnection.toFile().createNewFile()) && (logger.isDebugEnabled())) {
				logger.debug("Creation of connection.tx failedt");
		}
		
	}
		
	@Test
	public void testCreateUnregisteredUser() throws SkillerException {
		
		try {
			final Staff staff = administration.createNewUser(MY_LOGIN, "myPassword");
			idStaff = staff.getIdStaff();
			Assert.fail ("Exception is missing because the login 'UNREGISTERED' does not exist, and we are in mode allowSelfRegistration=false");
		} catch (final SkillerException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						String.format("exception has been correctly thrown with message %s", e.getMessage()));
			}
		}
	}

	
	@After
	public void after() {
		staffHandler.getStaff().remove(idStaff);
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
 	}
}
