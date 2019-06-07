/**
 * 
 */
package fr.skiller.controller;

import static fr.skiller.Error.CODE_LOGIN_ALREADY_EXIST;
import static fr.skiller.Error.CODE_UNREGISTERED_LOGIN;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.skiller.bean.StaffHandler;
import fr.skiller.security.TokenLoader;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "allowSelfRegistration=false" }) 
public class AdministrationControllerCreateNewUserTest {

	private static final String PASSWORD = "password"; //NOSONAR

	private static String pass = "passvoid";
	
	@Autowired
	private MockMvc mvc;

	/**
	 * Directory where the footprint of the very first solution is made.
	 */
	@Value("${applicationOutDirectory}")
	private String rootLocation;

	@Autowired
	StaffHandler staffHandler;

	Logger logger = LoggerFactory.getLogger(AdministrationControllerCreateNewUserTest.class.getCanonicalName());

	@Before
	public void before() throws IOException {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		if ( (!firstConnection.toFile().createNewFile()) && (logger.isDebugEnabled())) {
				logger.debug("Creation of connection.tx failedt");
		}
		
	}
		
	@Test
	public void creationNewUser() throws Exception {

		int crewSize = staffHandler.getStaff().size();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Crew size %d", crewSize));
		}
		this.mvc.perform(get("/admin/newUser")
				.param("login", "user")
				.param(PASSWORD, pass)
				.header(HttpHeaders.AUTHORIZATION,
						"Bearer " + TokenLoader.obtainAccessMockToken(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.staff.idStaff", is(0)))
				.andExpect(jsonPath("$.code", is(CODE_UNREGISTERED_LOGIN)));

	}
	
	@Test
	public void creationFailedForExistingUser() throws Exception {
		this.mvc.perform(get("/admin/newUser")
				.param("login", "frvidal")
				.param(PASSWORD, pass)
				.header(HttpHeaders.AUTHORIZATION,
						"Bearer " + TokenLoader.obtainAccessMockToken(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.staff.idStaff", is(0)))
				.andExpect(jsonPath("$.code", is(CODE_LOGIN_ALREADY_EXIST)));
		
	}

	@After
	public void after() {
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
 	}
	
}
