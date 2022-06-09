package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.MessageFormat;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.RepoScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerResetSunburstChartTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	public ProjectHandler projectHandler;
	
	@MockBean
	public StaffHandler staffHandler;

	@MockBean
	public CacheDataHandler cacheDataHandler;
	
	@MockBean
	private AsyncTask tasks;
	
	@MockBean
	private RepoScanner scanner;
	
	@MockBean
	private DataHandler dataHandler;
	
	/**
	 * Reset the Sunburst data
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void resetSunburstData() throws Exception {

		Project p = new Project(1789, "The revolutionary project");
		
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(projectHandler).saveLocationRepository(1789, null);
		doNothing().when(staffHandler).removeProject(1789);
		when(cacheDataHandler.removeRepository(p)).thenReturn(true);
		doNothing().when(dataHandler).removeCrawlerFiles(p);
		doNothing().when(scanner).generateAsync(p, new SettingsGeneration(1789));

		this.mvc.perform(delete("/api/project/1789/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new SettingsGeneration())))
			.andExpect(status().isAccepted());

		verify(projectHandler, times(1)).getProject(1789);
		verify(projectHandler, times(1)).saveLocationRepository(1789, null);
		verify(staffHandler, times(1)).removeProject(1789);
		verify(cacheDataHandler, times(1)).removeRepository(p);
		verify(dataHandler, times(1)).removeCrawlerFiles(p);
		verify(scanner, times(1)).generateAsync(p, new SettingsGeneration(1789));
	}

	/**
	 * Trying to reset the Sunburst data of an unknown project.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void resetSunburstDataKO() throws Exception {

		when(projectHandler.getProject(666)).thenThrow(
			new NotFoundException(CODE_PROJECT_NOFOUND, MessageFormat.format(MESSAGE_PROJECT_NOFOUND, 666)));

		this.mvc.perform(delete("/api/project/666/sunburst")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new SettingsGeneration())))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andExpect(jsonPath("$.message", is("There is no project for the identifier 666")))
			.andExpect(status().isNotFound());

		verify(projectHandler, times(1)).getProject(666);
		verify(projectHandler, never()).saveLocationRepository(anyInt(), anyString());
		verify(staffHandler, never()).removeProject(anyInt());
		verify(cacheDataHandler, never()).removeRepository(any(Project.class));
		verify(dataHandler, never()).removeCrawlerFiles(any(Project.class));
		verify(scanner, never()).generateAsync(any(Project.class), any(SettingsGeneration.class));
	}

}
