/**
 * 
 */
package com.fitzhi.controller.administrationController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.ClassicCredentials;
import com.fitzhi.data.internal.Staff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties="allowSelfRegistration=true")
public class PluggedAdministrationControllerSelfRegisteringAllowedTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().
		registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	private static final String TEST_USER = "test-user";

	@Autowired
	private MockMvc mvc;

	@Autowired
	Administration administration;

	@Autowired
	StaffHandler staffHandler;
	
	@Test
	@WithMockUser
	public void register() throws Exception {

		ClassicCredentials cc = ClassicCredentials.of(TEST_USER, "test-pass");

	    mvc.perform(post("/api/admin/classic/register")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.content(gson.toJson(cc))
			.accept("application/json;charset=UTF-8"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType("application/json;charset=UTF-8"));
	    
	    Optional<Staff> oStaff = staffHandler.findStaffOnLogin(TEST_USER);
	    Assert.assertTrue("The 'test-user' user should exist", oStaff.isPresent());
	}
	
	@After
	public void after() {
		Optional<Staff> oStaff = staffHandler.findStaffOnLogin(TEST_USER);
	    if (oStaff.isPresent()) {
	    	staffHandler.removeStaff(oStaff.get().getIdStaff());
	    }
	}
}
