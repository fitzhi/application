package com.fitzhi.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.source.crawler.RepoScanner;

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
 * Testing the method {@link ProjectController#resetDashboard(int)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerResetDashboardTest {
	
	private int UNKNOWN_ID_PROJECT = 999999;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	// Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	@MockBean
	CacheDataHandler cacheDataHandler;


	@MockBean
	DataHandler dataHandler;

	@MockBean
	RepoScanner repoScanner;

	@MockBean
	StaffHandler staffHandler;

	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "revolutionary project");
		project1789.setLocationRepository("myLocationRepository");
		projectHandler.addNewProject(project1789);
	}
	
	@Test
	@WithMockUser
	public void testResetDashboardUnknownProject() throws Exception {
		this.mvc.perform(delete(String.format("/api/project/%d/sunburst", UNKNOWN_ID_PROJECT))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testResetDashboardKnownProject() throws Exception {
		when(cacheDataHandler.removeRepository(any())).thenReturn(true);
		when(staffHandler.getLocker()).thenReturn(new Object());
		doNothing().when(staffHandler).removeProject(1789);
		doNothing().when(repoScanner).generateAsync(any(), any());
		this.mvc.perform(delete("/api/project/1789/sunburst")).andExpect(status().isAccepted());
		Mockito.verify(cacheDataHandler, times(1)).removeRepository(any());
		Mockito.verify(dataHandler, times(1)).removeCrawlerFiles(any());
		Mockito.verify(staffHandler, times(1)).removeProject(1789);
		Assert.assertNull("The location repository should be reset", projectHandler.get(1789).getLocationRepository());
	}

}
