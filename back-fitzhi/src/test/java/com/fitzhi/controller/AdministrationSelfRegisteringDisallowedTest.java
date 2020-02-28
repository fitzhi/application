/**
 * 
 */
package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties="allowSelfRegistration=false")
public class AdministrationSelfRegisteringDisallowedTest {

	@Value("${applicationOutDirectory}")
	private String rootLocation;

	private static final String TEST_USER = "test-user";

	@Autowired
	private MockMvc mvc;

	@Autowired
	Administration administration;

	Logger logger = LoggerFactory.getLogger(AdministrationSelfRegisteringDisallowedTest.class.getCanonicalName());

	@Autowired
	StaffHandler staffHandler;

	@Before
	public void before() throws IOException {
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
		
		if (firstConnection.toFile().createNewFile()) {
			logger.error("Cannot create new file");
		}
	}
	
	@Test
	@WithMockUser
	public void register() throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("login", TEST_USER);
	    params.add("password", "test-pass"); // NOSONAR

	    mvc.perform(get("/api/admin/register")
	        .params(params)
	        .accept("application/json;charset=UTF-8"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType("application/json;charset=UTF-8"));
	    
	    Optional<Staff> oStaff = staffHandler.findStaffWithLogin(TEST_USER);
	    Assert.assertTrue("The 'test-user' user should not exist", !oStaff.isPresent());
	    
	}

	@After
	public void after() throws IOException {
		Optional<Staff> oStaff = staffHandler.findStaffWithLogin(TEST_USER);
	    if (oStaff.isPresent()) {
	    	staffHandler.getStaff().remove(oStaff.get().getIdStaff());
	    }
        final Path root = Paths.get(rootLocation);
		final Path firstConnection = root.resolve("connection.txt");
	    Files.delete(firstConnection);
	}

}