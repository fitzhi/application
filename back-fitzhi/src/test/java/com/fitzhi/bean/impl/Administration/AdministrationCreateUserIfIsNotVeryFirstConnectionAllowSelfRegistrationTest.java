/**
 * 
 */
package com.fitzhi.bean.impl.Administration;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
/**
 * Test the administration bean for the user creation.<br/>
 * <span style="color:red;font-size:16">IF IT IS THE VERY FIRST CONNECTION !</span>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "allowSelfRegistration=true" }) 
public class AdministrationCreateUserIfIsNotVeryFirstConnectionAllowSelfRegistrationTest {

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

	private final Logger logger = LoggerFactory.getLogger(AdministrationCreateUserIfIsNotVeryFirstConnectionAllowSelfRegistrationTest.class.getCanonicalName());

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
	public void testCreateUnregisteredUser() throws ApplicationException {
		
		Staff staff = administration.createNewUser(MY_LOGIN, "myPassword");
		idStaff = staff.getIdStaff();
	}

	
	@After
	public void after() {
		staffHandler.getStaff().remove(idStaff);
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
 	}
}
