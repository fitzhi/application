/**
 * 
 */
package com.fitzhi.controller;

import static com.fitzhi.security.AuthorizationServerConfiguration.TRUSTED_CLIENT_USERNAME;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdministrationControllerConnectTest {

	private static final String MY_LOGIN = "bill";

	private static final String MY_PSSWORD = "abc123";

	@Autowired
	private MockMvc mvc;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	Administration administration;

	Logger logger = LoggerFactory.getLogger(AdministrationControllerConnectTest.class.getCanonicalName());

	int idStaff;

	private static final String GRANT_TYPE = "grant_type";
	private static final String USER = "username";
	private static final String PSSWORD = "password";
	
	@Before
	public void before() throws SkillerException {
		final Staff staff = administration.createNewUser(MY_LOGIN, MY_PSSWORD);
		this.idStaff = staff.getIdStaff();
	}

	@Test
	public void accessUnauthorizedWithoutToken() throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add(GRANT_TYPE, PSSWORD);
	    params.add(USER, MY_LOGIN);
	    params.add(PSSWORD, MY_PSSWORD); // NOSONAR
	 
	    mvc.perform(post("/oauth/token")
	        .params(params)
	        .with(httpBasic(TRUSTED_CLIENT_USERNAME, "secret"))
	        .accept("application/json;charset=UTF-8"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType("application/json;charset=UTF-8"));

	}

	
	@After
	public void after() {
		staffHandler.getStaff().remove(idStaff);
	}
}
