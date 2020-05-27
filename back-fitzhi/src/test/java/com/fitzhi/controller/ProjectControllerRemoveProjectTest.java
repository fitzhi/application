package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.fitzhi.controller.in.BodyParamSonarEntry;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.SonarProject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Test the method {@link ProjectController#removeProject(int)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerRemoveProjectTest {

	private int UNKNOWN_ID_PROJECT = 999999;
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

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
	public void testRemoveUnknownProject() throws Exception {
		this.mvc.perform(delete("/api/project/" + UNKNOWN_ID_PROJECT)).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testRemoveProjectOk() throws Exception {
		this.mvc.perform(delete("/api/project/" + 1789)).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	public void testRemoveNonEmptyProject() throws Exception {	
		projectHandler.get(1789).getSkills().put(1, new ProjectSkill(1));
		this.mvc.perform(delete("/api/project/" + 1789)).andExpect(status().isInternalServerError());		
	}
	@Test
	@WithMockUser
	public void testRemoveAllProjects() throws Exception {	
		this.mvc.perform(delete("/api/project/")).andExpect(status().isMethodNotAllowed());		
	}

	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(1789);
	}
	
}
