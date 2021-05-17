package com.fitzhi.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.controller.in.SettingsGeneration;
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
import org.springframework.http.MediaType;
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

		doNothing().when(repoScanner).generateAsync(any(), any());

		SettingsGeneration sg = new SettingsGeneration(99999, -1);

		this.mvc.perform(patch("/api/project/99999/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(sg)))
			.andExpect(status().isNotFound());

		Mockito.verify(repoScanner, never()).generateAsync(any(), any());
		Mockito.verify(repoScanner, never()).hasAvailableGeneration(any());
	}
	
	@Test
	@WithMockUser
	public void testReloadDashboardKnownProject() throws Exception {
		
		doNothing().when(repoScanner).generateAsync(any(), any());

		SettingsGeneration sg = new SettingsGeneration(1789, -1);

		this.mvc.perform(patch("/api/project/1789/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(sg)))
			.andExpect(status().isAccepted());

		Mockito.verify(cacheDataHandler, times(1)).removeRepository(any());

		Project project = projectHandler.get(1789);
		Assert.assertNotNull(project);
		Assert.assertNotNull("The location repository should NOT be reset", project.getLocationRepository());
		Assert.assertEquals("myLocationRepository", project.getLocationRepository());
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
}
