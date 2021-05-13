package com.fitzhi.controller.administrationController;

import static com.fitzhi.Error.CODE_CANNOT_SELF_CREATE_USER;
import static com.fitzhi.Error.MESSAGE_CANNOT_SELF_CREATE_USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApiError;
import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This test controls that a user cannot create himself, if the administrator didn't disallow that feature.
 * The setting in property file is {@code allowSelfRegistration=true/false}
 * </p>
 * <p>
 * the URL tested is "/api/admin/register"
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties="allowSelfRegistration=false")
@Slf4j
public class AdministrationControllerSelfRegisteringDisallowedTest {

	@Value("${applicationOutDirectory}")
	private String rootLocation;

	private static final String TEST_USER = "test-user";

	@Autowired
	private MockMvc mvc;

	@Autowired
	Administration administration;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void before() throws IOException {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		
		if (firstConnection.toFile().createNewFile()) {
			log.error("Cannot create new file");
		}
	}
	
	@Test
	@WithMockUser
	public void register() throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("login", TEST_USER);
	    params.add("password", "test-pass"); // NOSONAR

	    MvcResult result = mvc.perform(post("/api/admin/register")
	        .params(params)
	        .accept("application/json;charset=UTF-8"))
	        .andExpect(status().isInternalServerError())
	        .andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(status().isInternalServerError()).andReturn();
				
		ApiError error = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
		Assert.assertEquals(CODE_CANNOT_SELF_CREATE_USER, error.getCode());
		Assert.assertEquals(MESSAGE_CANNOT_SELF_CREATE_USER, error.getMessage());
	    
	}

	@After
	public void after() throws IOException {
		Optional<Staff> oStaff = staffHandler.findStaffOnLogin(TEST_USER);
	    if (oStaff.isPresent()) {
	    	staffHandler.getStaff().remove(oStaff.get().getIdStaff());
	    }
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
	    Files.delete(firstConnection);
	}

}
