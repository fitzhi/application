package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_ENDPOINT_SLAVE_ONLY;
import static com.fitzhi.Error.CODE_ENDPOINT_SLAVE_URL_GIT_MANDATORY;
import static com.fitzhi.Error.CODE_PROJECT_NOT_FOUND_URL_GIT;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;
import com.fitzhi.data.internal.ProjectLookupCriteria;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.source.crawler.RepoScanner;

/**
 * <p>
 * Test of the method {@link ProjectController#slaveGate}.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSlaveGateTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProjectHandler projectHandler;

	@MockBean
	private StaffHandler staffHandler;

	@MockBean
	private DataHandler dataHandler;

	@MockBean
	RepoScanner scanner;

	@Test
	public void slaveOnly() throws Exception {

		when(dataHandler.isLocal()).thenReturn(true);

		SettingsGeneration settings = new SettingsGeneration();

		this.mvc.perform(put("/api/project/analysis")
			.content(objectMapper.writeValueAsString(settings))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("/api/project/analysis is an end-point dedicated to slaves.")))
			.andExpect(jsonPath("$.code", is(CODE_ENDPOINT_SLAVE_ONLY)))

			.andDo(print())
			.andReturn();
		
	}

	@Test
	public void urlRepositoryMandatory() throws Exception {

		when(dataHandler.isLocal()).thenReturn(false);

		SettingsGeneration settings = new SettingsGeneration();

		this.mvc.perform(put("/api/project/analysis")
			.content(objectMapper.writeValueAsString(settings))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("The URL of the GIT repository is mandatory.")))
			.andExpect(jsonPath("$.code", is(CODE_ENDPOINT_SLAVE_URL_GIT_MANDATORY)))

			.andDo(print())
			.andReturn();
		
	}

	/**
	 * Test project not found when autoProjectCreation is FALSE.
	 * @throws Exception
	 */
	@Test
	public void projectNotFound() throws Exception {

		when(dataHandler.isLocal()).thenReturn(false);

		SettingsGeneration settings = new SettingsGeneration();
		settings.setUrlRepository("https://myUrlRepository");

		this.mvc.perform(put("/api/project/analysis")
			.content(objectMapper.writeValueAsString(settings))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("Cannot find a project associated with the GIT url https://myUrlRepository.")))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOT_FOUND_URL_GIT)))

			.andDo(print())
			.andReturn();
		
	}

	@Test
	public void Ok() throws Exception {

		when(dataHandler.isLocal()).thenReturn(false);
		Project p = new Project(1789, "Revolution", "https://myUrlRepository");
		p.setActive(true);
		when(projectHandler.lookup("https://myUrlRepository", ProjectLookupCriteria.UrlRepository)).thenReturn(
			Optional.of(p));

		when(scanner.generate(any(Project.class), any(SettingsGeneration.class))).thenReturn(
			new RiskDashboard(new DataChart(""), Collections.emptyList()));

		doNothing().when(staffHandler).createOffSetStaff();
		doNothing().when(dataHandler).saveStaff(any(Project.class), anyMap());
		doNothing().when(dataHandler).saveProjectAnalysis(any(ProjectAnalysis.class));

		SettingsGeneration settings = new SettingsGeneration();
		settings.setUrlRepository("https://myUrlRepository");

		this.mvc.perform(put("/api/project/analysis")
			.content(objectMapper.writeValueAsString(settings))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isNoContent())

			.andDo(print())
			.andReturn();
		
		verify(dataHandler, times(1)).saveStaff(any(Project.class), anyMap());
		verify(dataHandler, times(1)).saveProjectAnalysis(any(ProjectAnalysis.class));
	}


}
