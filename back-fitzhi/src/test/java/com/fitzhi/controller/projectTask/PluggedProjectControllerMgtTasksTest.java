package com.fitzhi.controller.projectTask;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

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

import static com.fitzhi.Error.CODE_TASK_NOT_FOUND;

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
public class PluggedProjectControllerMgtTasksTest {

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
	
	@Before
	public void before() throws ApplicationException {
		Project p = new Project (1789, "Revolutionnary project");
		projectHandler.addNewProject(p);
		asyncTask.addTask("nopeOperation", "mockProject", 1789);
		asyncTask.logMessage("nopeOperation", "mockProject", 1789, "my message", 0);
		
	}

	/**
	 * Controller is called with a wrong project id.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void testReadTaskNotFound() throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/1789/tasks/fakeOperation"))
				.andExpect(status().isNotFound())
				.andDo(print())
				.andExpect(jsonPath("$.code", is(CODE_TASK_NOT_FOUND)))
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
		
		MvcResult result = this.mvc.perform(get("/api/project/1789/tasks/nopeOperation"))
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
		projectHandler.getProjects().remove(1789);
		asyncTask.removeTask("nopeOperation", "mockProject", 1789);
	}

}
