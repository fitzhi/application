package com.fitzhi.controller.administrationController;

import static com.fitzhi.Error.CODE_CANNOT_SELF_CREATE_USER;
import static com.fitzhi.Error.CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static com.fitzhi.Error.CODE_LOGIN_ALREADY_EXIST;
import static com.fitzhi.Error.MESSAGE_CANNOT_SELF_CREATE_USER;
import static com.fitzhi.Error.MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApiError;
import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.ClassicCredentials;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.security.TokenLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().
		registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

		private static final String LOGIN = "login";

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

	@Autowired
	private ObjectMapper objectMapper;
	
	@Before
	public void before() throws IOException {
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		if ((!firstConnection.toFile().createNewFile()) && (log.isDebugEnabled())) {
			log.debug("Creation of connection.tx failedt");
		}
	}

	@Test
	public void cannotCreate2timesTheFirstAdminUser() throws Exception {
		int crewSize = staffHandler.getStaff().size();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Crew size %d", crewSize));
			staffHandler.getStaff().values().stream().forEach(s -> log.debug(s.getIdStaff() + " " + s.getLogin() + " " + s.getPassword()));
		}
		
		ClassicCredentials cc = ClassicCredentials.of("adminForTest", "passForTest");

		MvcResult result = this.mvc.perform(post("/api/admin/classic/primeRegister") 
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(cc)))
				.andExpect(status().isInternalServerError())
				.andReturn();

		log.debug (result.getResponse().getContentAsString());
		
		ApiError error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
		Assert.assertEquals(CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED, error.getCode());
		Assert.assertEquals(MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED, error.getMessage());
	}

	@Test
	public void cannotCreateYourOwnUser() throws Exception {

		int crewSize = staffHandler.getStaff().size();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Crew size %d", crewSize));
		}
		//
		// We disable this line for Sonar to avoid the useless password security check. 
		// This fake password is useless for any hacker.
		//
		MvcResult result = this.mvc.perform(post("/api/admin/newUser").param(LOGIN, "user").param(PASS_WORD, pass) //NOSONAR 
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenLoader.obtainAccessMockToken(mvc)))
				.andExpect(status().isInternalServerError()).andReturn();
				
		ApiError error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
		Assert.assertEquals(CODE_CANNOT_SELF_CREATE_USER, error.getCode());
		Assert.assertEquals(MESSAGE_CANNOT_SELF_CREATE_USER, error.getMessage());
	}

	@Test
	public void creationFailedForExistingUser() throws Exception {
		Staff s = new Staff(-1, "myUniqueLogin", "pass");
		s.setLastName("VIDAL");
		Staff st = this.staffHandler.createWorkforceMember(s);
		//
		// We disable this line for Sonar to avoid the useless password security check. 
		// This fake password is useless for any hacker.
		//
		MvcResult result = this.mvc.perform(post("/api/admin/newUser").param(LOGIN, "myUniqueLogin").param(PASS_WORD, pass) //NOSONAR
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenLoader.obtainAccessMockToken(mvc)))
				.andExpect(status().isInternalServerError()).andReturn();
				
		ApiError error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
		Assert.assertEquals(CODE_LOGIN_ALREADY_EXIST, error.getCode());

		staffHandler.removeStaff(st.getIdStaff());
	}

	@After
	public void after() {
		final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		Files.delete(firstConnection.toFile());
	}

}
