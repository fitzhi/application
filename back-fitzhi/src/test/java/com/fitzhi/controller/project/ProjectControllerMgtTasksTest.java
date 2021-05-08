package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Task;
import com.fitzhi.data.internal.TaskLog;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Testing the methods {@link ProjectController#readTask(String, int)} and {@link AsyncTask#getTask(String, String, int)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerMgtTasksTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;

	@Autowired
	AsyncTask asyncTask;
	
	final int ID_PROJECT = 1789;

	@Before
	public void before() throws ApplicationException {
		Project p = new Project (ID_PROJECT, "Revolutionnary project");
		projectHandler.addNewProject(p);
		asyncTask.addTask("nopeOperation", "mockProject", ID_PROJECT);
		asyncTask.logMessage("nopeOperation", "mockProject", ID_PROJECT, "my message", 0);
		
	}

	/**
	 * Controller is called with a wrong project id.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void testReadTaskNotFound() throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/tasks/fakeOperation/1789"))
				.andExpect(status().isNotFound())
				.andDo(print())
				.andReturn();
		Assert.assertNotNull(result);
	}
	
	/**
	 * Nominal behavior.
	 * @throws ApplicationException
	 */
	@Test
	@WithMockUser
	public void testReadTaskNominal() throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/tasks/nopeOperation/1789"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
	
		Task task = gson.fromJson(result.getResponse().getContentAsString(), Task.class);
		TaskLog tl = task.getActivityLogs().get(0);
		Assert.assertEquals("Task add is nominal", "my message", tl.getMessage());
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(ID_PROJECT);
		asyncTask.removeTask("nopeOperation", "mockProject", 1789);
	}

}
