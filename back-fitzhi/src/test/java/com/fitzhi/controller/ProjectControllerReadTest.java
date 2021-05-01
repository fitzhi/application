package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;

/**
 * <p>
 * Test the method {@link ProjectController#read(string)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerReadTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;
	
	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "project.to.find");
		projectHandler.addNewProject(project1789);
	}
	
	@Test
	@WithMockUser
	public void notFound() throws Exception {
		this.mvc.perform(get("/api/project/name/unknown"))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void found() throws Exception {
		this.mvc.perform(get("/api/project/name/project.to.find"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(CoreMatchers.containsString("1789")));;
	}

	@Test
	@WithMockUser
	public void doNotTransportPassword() throws Exception {
		projectHandler.get(1789).setPassword("my.password");
		this.mvc.perform(get("/api/project/name/project.to.find"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value("1789"))
			.andExpect(jsonPath("$.password").isEmpty());
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(1789);
	}
	
}
