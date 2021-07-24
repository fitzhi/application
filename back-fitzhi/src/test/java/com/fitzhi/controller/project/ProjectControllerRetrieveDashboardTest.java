package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static com.fitzhi.Error.CODE_MULTIPLE_TASK;
import static com.fitzhi.Error.CODE_DASHBOARD_START;
import static com.fitzhi.Error.MESSAGE_DASHBOARD_START;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.source.crawler.RepoScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerRetrieveDashboardTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	public ProjectHandler projectHandler;
	
	@MockBean
	public CacheDataHandler cacheDataHandler;
	
	@MockBean
	private AsyncTask tasks;
	
	@MockBean
	private RepoScanner scanner;
	
	/**
	 * User asks for a project-staff risks data. 
	 * System replies that a generation has already been launched.
	 * So just wait.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void pleaseWaitGenerationAlreadyLaunched() throws Exception {

		Project p = new Project(1789, "The revolutionary project");
		
		when(projectHandler.getProject(1789)).thenReturn(p);
		when(scanner.hasAvailableGeneration(p)).thenReturn(false);
		// A generation has already been launched.
		when(tasks.hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1789)).thenReturn(true);

		this.mvc.perform(put("/api/project/1789/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new SettingsGeneration())))
			.andExpect(status().isProcessing())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_MULTIPLE_TASK)))
			.andExpect(jsonPath("$.message", is("A dashboard generation has already been launched for The revolutionary project")));

		verify(projectHandler, times(1)).getProject(1789);
		verify(scanner, times(1)).hasAvailableGeneration(p);
		verify(tasks, times(1)).hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1789);
		verify(scanner, never()).generateAsync(p, new SettingsGeneration());
	}

	/**
	 * User asks for a project-staff risks data. System accepts and replies that a generation has been launched.
	 * So just wait.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void launchTheGeneration() throws Exception {

		Project p = new Project(1789, "The revolutionary project");
		
		when(projectHandler.getProject(1789)).thenReturn(p);
		when(scanner.hasAvailableGeneration(p)).thenReturn(false);
		doNothing().when(scanner).generateAsync(p, new SettingsGeneration());
		// A generation has already been launched.
		when(tasks.hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1789)).thenReturn(false);

		this.mvc.perform(put("/api/project/1789/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new SettingsGeneration())))
			.andExpect(status().isProcessing())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_DASHBOARD_START)))
			.andExpect(jsonPath("$.message", is(MESSAGE_DASHBOARD_START)));

		verify(projectHandler, times(1)).getProject(1789);
		verify(scanner, times(1)).hasAvailableGeneration(p);
		verify(tasks, times(1)).hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1789);
		verify(scanner).generateAsync(p, new SettingsGeneration());
	}

	/**
	 * User asks for a project-staff risks data. System accepts and replies that a generation has been launched.
	 * So just wait.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void retrieveFormerGeneration() throws Exception {

		Project p = new Project(1789, "The revolutionary project");
		
		when(projectHandler.getProject(1789)).thenReturn(p);
		when(scanner.hasAvailableGeneration(p)).thenReturn(true);
		when(scanner.generate(p, new SettingsGeneration())).thenReturn(new RiskDashboard(null, null));
		doNothing().when(scanner).generateAsync(p, new SettingsGeneration());
		// A generation has already been launched.
		when(tasks.hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1789)).thenReturn(false);

		this.mvc.perform(put("/api/project/1789/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new SettingsGeneration())))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

		verify(projectHandler, times(1)).getProject(1789);
		verify(scanner, times(1)).hasAvailableGeneration(p);
		verify(tasks, never()).hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1789);
		verify(scanner, never()).generateAsync(p, new SettingsGeneration());
	}

}
