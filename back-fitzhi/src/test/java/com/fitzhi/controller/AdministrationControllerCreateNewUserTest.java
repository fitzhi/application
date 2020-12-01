/**
 * 
 */
package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_CANNOT_SELF_CREATE_USER;
import static com.fitzhi.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.CODE_LOGIN_ALREADY_EXIST;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.security.TokenLoader;

import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@TestPropertySource(properties = { "allowSelfRegistration=false" })
public class AdministrationControllerCreateNewUserTest {

	private static final String LOGIN = "login";

	private static final String CST_CODE = "$.code";

	private static final String CST_STAFF_ID_STAFF = "$.staff.idStaff";

	private static final String PASS_WORD = "password"; // NOSONAR

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

	@Autowired
	public Administration administration;

	@Before
	public void before() throws IOException {
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		if ((!firstConnection.toFile().createNewFile()) && (log.isDebugEnabled())) {
			log.debug("Creation of connection.tx failedt");
		}

	}

	@Test
	public void creationVeryFirstUserKO() throws Exception {
		int crewSize = staffHandler.getStaff().size();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Crew size %d", crewSize));
		}
		this.mvc.perform(get("/api/admin/veryFirstUser") // NOSONAR
				.param(LOGIN, "adminForTest").param(PASS_WORD, "passForTest")).andExpect(status().isOk())
				.andExpect(jsonPath(CST_STAFF_ID_STAFF, is(0)))
				.andExpect(jsonPath(CST_CODE, is(CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED)));

	}

	@Test
	public void creationNewUser() throws Exception {

		int crewSize = staffHandler.getStaff().size();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Crew size %d", crewSize));
		}
		//
		// We disable this line for Sonar to avoid the useless password security check. 
		// This fake password is useless for any hacker.
		//
		this.mvc.perform(get("/api/admin/newUser").param(LOGIN, "user").param(PASS_WORD, pass) //NOSONAR 
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenLoader.obtainAccessMockToken(mvc)))
				.andExpect(status().isOk()).andExpect(jsonPath(CST_STAFF_ID_STAFF, is(0)))
				.andExpect(jsonPath(CST_CODE, is(CODE_CANNOT_SELF_CREATE_USER)));
	}

	@Test
	public void creationFailedForExistingUser() throws Exception {
		Staff s = new Staff(777, "frvidal", "pass");
		s.setLastName("VIDAL");
		this.staffHandler.addNewStaffMember(s);
		//
		// We disable this line for Sonar to avoid the useless password security check. 
		// This fake password is useless for any hacker.
		//
		this.mvc.perform(get("/api/admin/newUser").param(LOGIN, "frvidal").param(PASS_WORD, pass) //NOSONAR
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenLoader.obtainAccessMockToken(mvc)))
				.andExpect(status().isOk()).andExpect(jsonPath(CST_CODE, is(CODE_LOGIN_ALREADY_EXIST)))
				.andExpect(jsonPath(CST_STAFF_ID_STAFF, is(0)));

	}

	@After
	public void after() {
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
	}

}
