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
 * Test the method {@link ProjectController#inactivateProject(int)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerReactivateProjectTest {

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
		Project project1789 = new Project(1789, "The great revolutionary project");
		projectHandler.addNewProject(project1789);
		project1789.setActive(false);
	}
	
	@Test
	@WithMockUser
	public void testInactivateUnknownProject() throws Exception {
		this.mvc.perform(post("/api/project/rpc/reactivation/" + UNKNOWN_ID_PROJECT)).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testReactivateProjectOk() throws Exception {
		this.mvc.perform(post("/api/project/rpc/reactivation/" + 1789)).andExpect(status().isOk());
		Project p = projectHandler.get(1789);
		Assert.assertTrue(p.isActive());
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(1789);
	}
	
}
