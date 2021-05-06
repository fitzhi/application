package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerReadWithIdTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		Project project1789 = new Project(1789, "the French Revolution");
		projectHandler.addNewProject(project1789);
	}

	@Test
	@WithMockUser
	public void found() throws Exception {
		this.mvc.perform(get("/api/project/1789"))
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value("1789"))
			.andExpect(jsonPath("$.name").value("the French Revolution"));
	}


	@Test
	@WithMockUser
	public void doNotTransportPassword() throws Exception {

		projectHandler.get(1789).setPassword("my-pass-word");
		
		this.mvc.perform(get("/api/project/1789"))
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value("1789"))
			.andExpect(jsonPath("$.password").isEmpty());
	}

	@Test
	@WithMockUser
	public void notfound() throws Exception {
		this.mvc.perform(get("/api/project/666"))
			.andExpect(status().is(HttpStatus.NOT_FOUND.value()))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}


}
