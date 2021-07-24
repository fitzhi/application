package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.RepoScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
 * Test of the method {@link ProjectController#scmConnect(int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSCMConnectTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@MockBean
	private RepoScanner crawler;

	@Test
	@WithMockUser
	public void connectionSucceeds() throws Exception {

		Project p = new Project(1789, "1789");		
		when(projectHandler.getProject(1789)).thenReturn (p);
		when(crawler.testConnection(p)).thenReturn(true);

		this.mvc.perform(get("/api/project/1789/test"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print());

		Mockito.verify(projectHandler, times(1)).getProject(1789);
		Mockito.verify(crawler, times(1)).testConnection(p);
	}

	@Test
	@WithMockUser
	public void connectionFails() throws Exception {

		Project p = new Project(1789, "1789");		
		when(projectHandler.getProject(1789)).thenReturn (p);
		when(crawler.testConnection(p)).thenReturn(false);

		this.mvc.perform(get("/api/project/1789/test"))
			.andExpect(status().isOk())
			.andExpect(content().string("false"))
			.andDo(print());

		Mockito.verify(projectHandler, times(1)).getProject(1789);
		Mockito.verify(crawler, times(1)).testConnection(p);
	}

	@Test
	@WithMockUser
	public void loadSkillsKO() throws Exception {

		when(projectHandler.getProject(666)).thenThrow(new NotFoundException(CODE_PROJECT_NOFOUND, "Error message"));

		this.mvc.perform(get("/api/project/666/test"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andExpect(jsonPath("$.message", is("Error message")));

		Mockito.verify(projectHandler, times(1)).getProject(666);

	}
}
