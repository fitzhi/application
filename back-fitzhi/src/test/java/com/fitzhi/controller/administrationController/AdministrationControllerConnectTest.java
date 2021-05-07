package com.fitzhi.controller.administrationController;

import static com.fitzhi.security.AuthorizationServerConfiguration.TRUSTED_CLIENT_USERNAME;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.fitzhi.Error.CODE_INVALID_LOGIN_PASSWORD;
import static com.fitzhi.Error.MESSAGE_INVALID_LOGIN_PASSWORD;

import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdministrationControllerConnectTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	Administration administration;

	@Test
	public void connect() throws Exception {

		when(administration.connect("userForTest", "passForTest"))
			.thenReturn(new Staff(1789, "adminForTest", "passForTest"));

		//
		// We disable this line for the Sonar analysis to avoid a useless password security check. 
		// This fake password is useless for any hacker
		//
		this.mvc.perform(get("/api/admin/connect") //NOSONAR
					.param("login", "userForTest") 
					.param("password", "passForTest"))  
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idStaff", is(1789)))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	/**
	 * Bad password connection failure.
	 * @throws Exception
	 */
	@Test
	public void connectKO() throws Exception {

		when(administration.connect("userForTest", "passForTest"))
			.thenThrow(new ApplicationException(CODE_INVALID_LOGIN_PASSWORD, MESSAGE_INVALID_LOGIN_PASSWORD));

		//
		// We disable this line for the Sonar analysis to avoid a useless password security check. 
		// This fake password is useless for any hacker
		//
		this.mvc.perform(get("/api/admin/connect") //NOSONAR
					.param("login", "userForTest") 
					.param("password", "passForTest"))  
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code", is(CODE_INVALID_LOGIN_PASSWORD)))
				.andExpect(jsonPath("$.message", is(MESSAGE_INVALID_LOGIN_PASSWORD)))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

}
