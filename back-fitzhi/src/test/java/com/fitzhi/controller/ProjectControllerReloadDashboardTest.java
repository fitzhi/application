package com.fitzhi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.source.crawler.RepoScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
 * Testing the method {@link ProjectController#reloadDashboard(int)}
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
	public void testResetDashboardUnknownProject() throws Exception {
		this.mvc.perform(get("/api/project/reloadDashboard/" + UNKNOWN_ID_PROJECT)).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testResetDashboardKnownProject() throws Exception {
        when(cacheDataHandler.removeRepository(any())).thenReturn(true);
        when(repoScanner.generateAsync(any(), any())).thenReturn(null);
		this.mvc.perform(get("/api/project/reloadDashboard/1789")).andExpect(status().isOk());
		Mockito.verify(cacheDataHandler, times(1)).removeRepository(any());
		Assert.assertNotNull("The location repository should NOT be reset", projectHandler.get(1789).getLocationRepository());
	}

}
