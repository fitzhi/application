/**
 * 
 */
package com.fitzhi.security;

import static com.fitzhi.security.TokenLoader.obtainAccessMockToken;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;



/**
 * Test of the login authentication.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "development.unplugged.security=0" }) 
public class LoginTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void accessUnauthorizedWithoutToken() throws Exception {
	    mvc.perform(get("/api/project/1"))
	      .andExpect(status().isUnauthorized());
	}

	@Test
	public void accessWithToken() throws Exception {
	    String accessToken = obtainAccessMockToken(mvc);
	    mvc.perform(get("/api/test/ping")
	    	.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(MediaType.TEXT_HTML))
	    	.andExpect(content().string(containsString("pong")));
	    	
	}
	
 }
