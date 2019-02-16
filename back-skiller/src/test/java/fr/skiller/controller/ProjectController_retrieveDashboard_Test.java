package fr.skiller.controller;

import static fr.skiller.Error.CODE_MULTIPLE_TASK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.AsyncTask;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.ProjectController;
import fr.skiller.controller.ProjectController.SettingsGeneration;
import fr.skiller.data.external.SunburstDTO;
import fr.skiller.data.internal.Project;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectController_retrieveDashboard_Test {

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
	public void addReadKnownProject() throws Exception {

		Project p = projectHandler.get(1);
		Assert.assertNotNull(p);

		SettingsGeneration param = new ProjectController().new SettingsGeneration();
		param.idProject = 1;
		
		Mockito.when(cacheDataHandler.hasCommitRepositoryAvailable(p)).thenReturn(false);
		Mockito.when(tasks.containsTask(ProjectController.DASHBOARD_GENERATION, "project", 1))
			.thenReturn(true);

		String jsonInput = gson.toJson(param);

		SunburstDTO expected = new SunburstDTO(1, fr.skiller.Error.CODE_MULTIPLE_TASK,
				"A dashboard generation has already been launched for TEST 1");
		
		this.mvc.perform(post("/project/sunburst")
		.content(jsonInput))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().json(gson.toJson(expected)));
	}

}
