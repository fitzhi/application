package com.fitzhi.controller.projectSonar;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectSonarController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
 * Test of the method {@link ProjectSonarController#s
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSonarControllerSaveUrlSonarServerTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@Test
	@WithMockUser
	public void saveUrlSonarServer() throws Exception {
		
		when(projectHandler.getProject(1805)).thenReturn(new Project(1805, "Testing project"));
	
		this.mvc.perform(put("/api/project/1805/sonar/url")
				.contentType(MediaType.TEXT_PLAIN)
				.content("URL_OF_SONAR"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"));

		Mockito.verify(projectHandler, times(1)).saveUrlSonarServer(
				new Project(1805, "Testing project"), 
				"URL_OF_SONAR");
		Mockito.verify(projectHandler, times(1)).getProject(1805);
	}

	@Test
	@WithMockUser
	public void saveUrlSonarServerKO() throws Exception {
		
		doThrow(new ApplicationException(CODE_PROJECT_NOFOUND, "Project 1805 not found"))
			.when(projectHandler)
			.getProject(1805);

		this.mvc.perform(put("/api/project/1805/sonar/url")
				.contentType(MediaType.TEXT_PLAIN)
				.content("URL_OF_SONAR"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is("Project 1805 not found")))
				.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)));
		
		Mockito.verify(projectHandler, times(1)).getProject(1805);
	}

}
