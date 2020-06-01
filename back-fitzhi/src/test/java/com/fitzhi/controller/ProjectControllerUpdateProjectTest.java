package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.in.BodyParamSonarEntry;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.SonarProject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Test the method {@link ProjectController#updateProject(int, Project)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerUpdateProjectTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	private int UNKNOWN_ID_PROJECT = 999999;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "revolutionary project");
		projectHandler.addNewProject(project1789);
	}
	
	@Test
	@WithMockUser
	public void testUpdateUnknownProject() throws Exception {
		
		this.mvc.perform(put("/api/project/" + UNKNOWN_ID_PROJECT)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new Project(UNKNOWN_ID_PROJECT, ""))))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testUpdateProjectOk() throws Exception {
		Project project = projectHandler.get(1789);
		project.setName("The revolutionary project");
		this.mvc.perform(put("/api/project/1789")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(project)))
				.andExpect(status().isNoContent());
		project = projectHandler.get(1789);
		Assert.assertEquals("The revolutionary project", project.getName());
	}
	
	@Test
	@WithMockUser
	public void testUpdateInactiveProjectForbidden() throws Exception {
		Project project = projectHandler.get(1789);
		project.setActive(false);
		this.mvc.perform(put("/api/project/1789")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(project)))
				.andExpect(status().isMethodNotAllowed());
	}

	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(1789);
	}
	
}
