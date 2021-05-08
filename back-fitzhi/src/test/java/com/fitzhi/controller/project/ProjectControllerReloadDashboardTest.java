package com.fitzhi.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <p>
 * Testing the method {@link ProjectController#reloadSunburstChart(int)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerReloadDashboardTest {
	
	private int UNKNOWN_ID_PROJECT = 999999;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	@MockBean
	CacheDataHandler cacheDataHandler;

	@MockBean
	RepoScanner repoScanner;

	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "revolutionary project");
		project1789.setLocationRepository("myLocationRepository");
		projectHandler.addNewProject(project1789);
	}
	
	@Test
	@WithMockUser
	public void testReloadDashboardUnknownProject() throws Exception {
		this.mvc.perform(post("/api/project/" + UNKNOWN_ID_PROJECT + "/sunburst")).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testReloadDashboardKnownProject() throws Exception {
		doNothing().when(repoScanner).generateAsync(any(), any());
		this.mvc.perform(post("/api/project/1789/sunburst")).andExpect(status().isAccepted());
		Mockito.verify(cacheDataHandler, times(1)).removeRepository(any());
		Assert.assertNotNull("The location repository should NOT be reset", projectHandler.get(1789).getLocationRepository());
		Assert.assertEquals("myLocationRepository", projectHandler.get(1789).getLocationRepository());
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
}
