package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.external.SunburstDTO;
import com.fitzhi.data.internal.Project;
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
	
	@Autowired
	public ProjectHandler projectHandler;
	
	@MockBean
	public CacheDataHandler cacheDataHandler;
	
	@MockBean
	private AsyncTask tasks;
	
	@Test
	@WithMockUser
	public void addReadKnownProject() throws Exception {

		Project p = projectHandler.get(1);
		Assert.assertNotNull(p);

		SettingsGeneration param = new SettingsGeneration();
		param.setIdProject(1);
		
		Mockito.when(cacheDataHandler.hasCommitRepositoryAvailable(p)).thenReturn(false);
		Mockito.when(tasks.hasActiveTask(Global.DASHBOARD_GENERATION, "project", 1))
			.thenReturn(true);

		String jsonInput = gson.toJson(param);

		SunburstDTO expected = new SunburstDTO(1, -1, com.fitzhi.Error.CODE_MULTIPLE_TASK,
				"A dashboard generation has already been launched for TEST 1");
		
		this.mvc.perform(post("/api/project/sunburst")
		.contentType(MediaType.APPLICATION_JSON_UTF8)
		.content(jsonInput))
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().json(gson.toJson(expected)));
	}

}
