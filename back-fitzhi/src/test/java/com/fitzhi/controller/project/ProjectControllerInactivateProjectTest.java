package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
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
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;

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
public class ProjectControllerInactivateProjectTest {

	private int UNKNOWN_ID_PROJECT = 999999;

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;
	
	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "The great revolutionary project");
		projectHandler.addNewProject(project1789);
		project1789.setActive(true);
	}
	
	@Test
	@WithMockUser
	public void testInactivateUnknownProject() throws Exception {
		this.mvc.perform(post("/api/project/rpc/inactivation/" + UNKNOWN_ID_PROJECT)).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testInactivateProjectOk() throws Exception {
		this.mvc.perform(post("/api/project/rpc/inactivation/" + 1789)).andExpect(status().isOk());
		Project p = projectHandler.get(1789);
		Assert.assertFalse(p.isActive());
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(1789);
	}
	
}
