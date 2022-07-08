package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
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

	@Autowired
	private StaffHandler staffHandler;
	
	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "revolutionary project");
		projectHandler.addNewProject(project1789);
		projectHandler.disableDataSaving();
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
	public void testRemoveProjectWithSkills() throws Exception {	
		projectHandler.lookup(1789).getSkills().put(1, new ProjectSkill(1));
		this.mvc.perform(delete("/api/project/" + 1789)).andExpect(status().isInternalServerError());		
	}
	
	@Test
	@WithMockUser
	public void testRemoveProjectWithLocation() throws Exception {	
		projectHandler.lookup(1789).setLocationRepository("locationRepository");
		this.mvc.perform(delete("/api/project/" + 1789)).andExpect(status().isInternalServerError());		
	}
	
	@Test
	@WithMockUser
	public void testRemoveProjectReferencedInStaff() throws Exception {	
		staffHandler.lookup(1).addMission(new Mission(1, 1789, "revolutionary project"));
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
		staffHandler.lookup(1).getMissions().clear();
	}
	
}
