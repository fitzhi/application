package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.DataHandler.PathsType;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
public class ProjectControllerSavePathsTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	public ProjectHandler projectHandler;

	@MockBean
	public DataHandler dataHandler;

	@Autowired
	private ObjectMapper objectMapper;

	private List<String> builPathsList() {
		List<String> list = new ArrayList<>();
		list.add("path/one");
		list.add("path/two");
		list.add("path/three");
		list.add("path/four");
		return list;
	}

	@Test
	public void projectNotFound() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(false);

		// Bad project identifier : 1788.
		this.mvc.perform(put("/api/project/1788/pathsAdded")
			.content(objectMapper.writeValueAsBytes(builPathsList()))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("There is no project for the identifier 1788")))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andDo(print());

		verify(projectHandler, never()).getProject(1789);
	}

	@Test
	public void empty() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(dataHandler).savePaths(p, new ArrayList<>(), PathsType.PATHS_ADDED);

		this.mvc.perform(put("/api/project/1789/pathsAdded")
			.content(objectMapper.writeValueAsString(new ArrayList<>(new HashSet<String>())))
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).savePaths(p, new ArrayList<>(), PathsType.PATHS_ADDED);
	}

	@Test
	public void saveNominalPathsAdded() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(dataHandler).savePaths(p, builPathsList(), PathsType.PATHS_ADDED);

		this.mvc.perform(put("/api/project/1789/pathsAdded")
			.content(objectMapper.writeValueAsString(builPathsList()))
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).savePaths(p, builPathsList(), PathsType.PATHS_ADDED);
	}

	@Test
	public void saveNominalPathsModified() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(dataHandler).savePaths(p, builPathsList(), PathsType.PATHS_ADDED);

		this.mvc.perform(put("/api/project/1789/pathsModified")
			.content(objectMapper.writeValueAsString(builPathsList()))
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).savePaths(p, builPathsList(), PathsType.PATHS_MODIFIED);
	}

	@Test
	public void saveNominalPathsCandidate() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(dataHandler).savePaths(p, builPathsList(), PathsType.PATHS_ADDED);

		this.mvc.perform(put("/api/project/1789/pathsCandidate")
			.content(objectMapper.writeValueAsString(builPathsList()))
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).savePaths(p, builPathsList(), PathsType.PATHS_CANDIDATE);
	}

}
