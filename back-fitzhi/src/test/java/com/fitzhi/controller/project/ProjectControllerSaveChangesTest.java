package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.SourceControlChanges;

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

/**
 * Test of the method {@link ProjectController#saveAnalysis(int, com.fitzhi.data.internal.RepositoryAnalysis)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSaveChangesTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	public ProjectHandler projectHandler;

	@MockBean
	public DataHandler dataHandler;

	@Autowired
	private ObjectMapper objectMapper;

	private RepositoryAnalysis buildRepositoryAnalysis() {
		RepositoryAnalysis analysis = new RepositoryAnalysis(new Project(1890, "my project"));
		return analysis;
	}

	@Test
	public void projectNotFound() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(false);

		// Bad project identifier : 1788.
		this.mvc.perform(put("/api/project/1788/changes")
			.content(objectMapper.writeValueAsString(buildRepositoryAnalysis()))
			.contentType(MediaType.TEXT_PLAIN_VALUE))

			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("There is no project for the identifier 1788")))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andDo(print());

		verify(projectHandler, never()).getProject(1789);
	}

	@Test
	public void saveNominal() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(dataHandler).saveRepositoryAnalysis(p, buildRepositoryAnalysis());

		String body = HttpDataHandlerImpl.serializeChanges(buildRepositoryAnalysis().getChanges());
		this.mvc.perform(put("/api/project/1789/changes")
			.content(body)
			.contentType(MediaType.TEXT_PLAIN_VALUE))
			.andExpect(status().isOk());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).saveChanges(any(Project.class), any(SourceControlChanges.class));
	}

	@Test
	@WithMockUser
	public void saveWithRealData() throws Exception {

		File file = new File("./src/test/resources/slave-save-changes/changes.csv");
		final BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder analysis = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		br.close();
		
		Project p = new Project(2, "Two");
		when(projectHandler.containsProject(2)).thenReturn(true);
		when(projectHandler.getProject(2)).thenReturn(p);
		doNothing().when(dataHandler).saveRepositoryAnalysis(p, buildRepositoryAnalysis());

		this.mvc.perform(put("/api/project/2/changes")
			.content(analysis.toString().getBytes())
			.contentType(MediaType.TEXT_PLAIN_VALUE))
			.andExpect(status().isOk());

		verify(projectHandler, times(1)).getProject(2);
		verify(dataHandler, times(1)).saveChanges(any(Project.class), any(SourceControlChanges.class));
		
	}


}
