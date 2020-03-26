/**
 * 
 */
package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.security.TokenLoader;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void pingGet() throws Exception {

		this.mockMvc
				.perform(get("/api/test/ping")
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
				.perform(post("/api/test/pong")
						.header(HttpHeaders.AUTHORIZATION,
						"Bearer " + TokenLoader.obtainAccessMockToken(mockMvc))
						)
				.andExpect(content().contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
				.andExpect(content().string(CoreMatchers.containsString("ping")));
	}

	/**
	 * The goal of this test is the serialization of LocalDate with Jackson.
	 * @throws Exception
	 */
	@Test
	public void pingSerializedDate() throws Exception {

		this.mockMvc
				.perform(get("/api/test/buildDate")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenLoader.obtainAccessMockToken(mockMvc)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("{\"localDate\":\"2020-03-16\"}"));
	}
}