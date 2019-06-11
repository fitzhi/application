/**
 * 
 */
package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fr.skiller.security.TokenLoader;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PingControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void pingGet() throws Exception {

		this.mockMvc
				.perform(get("/test/ping")
						.header(HttpHeaders.AUTHORIZATION,
						"Bearer " + TokenLoader.obtainAccessMockToken(mockMvc))
						)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.TEXT_HTML))
				.andExpect(content().string(CoreMatchers.containsString("pong")));
	}

	@Test
	public void pingPost() throws Exception {
		this.mockMvc
				.perform(post("/test/pong")
						.header(HttpHeaders.AUTHORIZATION,
						"Bearer " + TokenLoader.obtainAccessMockToken(mockMvc))
						)
				.andExpect(content().contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
				.andExpect(content().string(CoreMatchers.containsString("ping")));
	}

}