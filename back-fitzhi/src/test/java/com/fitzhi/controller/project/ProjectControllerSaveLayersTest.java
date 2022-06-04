package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;

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
 * Test of the method {@link ProjectController#saveLayers(int, List)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSaveLayersTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	public ProjectHandler projectHandler;

	@MockBean
	public DataHandler dataHandler;

	@Autowired
	private ObjectMapper objectMapper;

	private List<ProjectLayer> builLayersList() {
		List<ProjectLayer> list = new ArrayList<>();
		list.add(new ProjectLayer(1789, 2022, 31, 10, 2));
		return list;
	}

	@Test
	public void projectNotFound() throws Exception {

		when(projectHandler.containsProject(1789)).thenReturn(false);
		doNothing().when(dataHandler).saveSkylineLayers(any(Project.class), any(ProjectLayers.class));

		// Bad project identifier : 1788.
		this.mvc.perform(put("/api/project/1788/projectLayers")
			.content(objectMapper.writeValueAsBytes(builLayersList()))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("There is no project for the identifier 1788")))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andDo(print());

		verify(projectHandler, never()).getProject(1789);
		verify(dataHandler, never()).saveSkylineLayers(any(Project.class), any(ProjectLayers.class));
	}

	@Test
	public void empty() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(dataHandler).saveSkylineLayers(p, new ProjectLayers(p, Collections.emptyList()));

		this.mvc.perform(put("/api/project/1789/projectLayers")
			.content(objectMapper.writeValueAsString(new ArrayList<>(new HashSet<ProjectLayer>())))
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isNoContent());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).saveSkylineLayers(p, new ProjectLayers(p, Collections.emptyList()));
	}

	@Test
	public void saveSkylineLayers() throws Exception {
		Project p = new Project(1789, "Revolution");
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(p);

		doNothing().when(dataHandler).saveSkylineLayers(p, new ProjectLayers(p, builLayersList()));

		this.mvc.perform(put("/api/project/1789/projectLayers")
			.content(objectMapper.writeValueAsString(builLayersList()))
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isNoContent());

		verify(projectHandler, times(1)).getProject(1789);
		verify(dataHandler, times(1)).saveSkylineLayers(p, new ProjectLayers(p, builLayersList()));
	}

}
