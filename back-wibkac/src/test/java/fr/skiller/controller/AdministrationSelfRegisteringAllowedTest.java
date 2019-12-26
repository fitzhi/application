/**
 * 
 */
package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties="allowSelfRegistration=true")
public class AdministrationSelfRegisteringAllowedTest {


	private static final String TEST_USER = "test-user";

	@Autowired
	private MockMvc mvc;

	@Autowired
	Administration administration;

	Logger logger = LoggerFactory.getLogger(AdministrationSelfRegisteringAllowedTest.class.getCanonicalName());

	@Autowired
	StaffHandler staffHandler;
	
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
	    Assert.assertTrue("The 'test-user' user should exist", oStaff.isPresent());
	}
	
	@After
	public void after() {
		Optional<Staff> oStaff = staffHandler.findStaffWithLogin(TEST_USER);
	    if (oStaff.isPresent()) {
	    	staffHandler.getStaff().remove(oStaff.get().getIdStaff());
	    }
	}
}
