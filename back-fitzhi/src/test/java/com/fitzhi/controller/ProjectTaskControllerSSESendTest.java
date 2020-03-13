package com.fitzhi.controller;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncListener;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Task;
import com.fitzhi.data.internal.TaskLog;
import com.fitzhi.exception.SkillerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Testing the methods {@link ProjectTasksController#emitTaskLog(String, int)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectTaskControllerSSESendTest {

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

	private String MARK_END_OF_OPERATION = "end of operation";
			
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
	@Before
	public void before() throws SkillerException {
		Project p = new Project (ID_PROJECT, "Revolutionnary project");
		projectHandler.addNewProject(p);
		asyncTask.addTask("nopeOperation", "mockProject", ID_PROJECT);
		asyncTask.logMessage("nopeOperation", "mockProject", ID_PROJECT, "my message");
		
	    executorService.schedule(new Runnable() {
	        @Override
	        public void run() {
	        	try {
					asyncTask.completeTask("nopeOperation", MARK_END_OF_OPERATION, ID_PROJECT);
				} catch (SkillerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }, 2, TimeUnit.SECONDS);
		
		
	}

	/**
	 * Controller is sending data to a mock front.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void testReadTaskNotFound() throws Exception {
	
		StringWriter writer = new StringWriter();
		
		mvc.perform(get("/api/project/tasks/stream/nopeOperation/1789").contentType(MediaType.ALL))
			.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(request().asyncStarted())
        	.andExpect(request().asyncResult(nullValue()))
        	.andExpect(header().string("Content-Type", MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8"))
        	.andDo(print(writer));
	}
	
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(ID_PROJECT);
		asyncTask.removeTask("nopeOperation", "mockProject", 1789);
	}

}
