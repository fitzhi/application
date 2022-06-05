package com.fitzhi.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;

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
 * <p>
 * Test the method {@link ProjectController#updateProjectAnalysis(int, com.fitzhi.data.internal.ProjectAnalysis)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSaveProjectAnalysisTest {

	private int UNKNOWN_ID_PROJECT = 999999;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ProjectHandler projectHandler;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@WithMockUser
	public void testUpdateUnknownProject() throws Exception {
		when(projectHandler.containsProject(UNKNOWN_ID_PROJECT)).thenReturn(false);

		this.mvc.perform(put("/api/project/" + UNKNOWN_ID_PROJECT)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(objectMapper.writeValueAsString(new ProjectAnalysis(UNKNOWN_ID_PROJECT))))
			.andExpect(status().isNotFound());

		verify(projectHandler, never()).getProject(UNKNOWN_ID_PROJECT);
		verify(projectHandler, never()).saveProjectAnalysis(any(ProjectAnalysis.class));
	}
	
	@Test
	@WithMockUser
	public void testUpdateProjectOk() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(true);
		Project p = new Project(1789, "The French Revolution");
		p.setActive(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(projectHandler).saveProjectAnalysis(any(ProjectAnalysis.class));

		this.mvc.perform(put("/api/project/1789/analysis")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(new ProjectAnalysis(1789))))
				.andExpect(status().isNoContent());

		verify(projectHandler, times(1)).getProject(1789);
		verify(projectHandler, times(1)).containsProject(1789);
		verify(projectHandler, times(1)).saveProjectAnalysis(any(ProjectAnalysis.class));
	}
	
	@Test
	@WithMockUser
	public void testUpdateInactiveProjectForbidden() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(true);
		Project p = new Project(1789, "The French Revolution");
		p.setActive(false);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(projectHandler).saveProjectAnalysis(any(ProjectAnalysis.class));

		this.mvc.perform(put("/api/project/1789/analysis")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(new ProjectAnalysis(1789))))
				.andExpect(status().isMethodNotAllowed());

		verify(projectHandler, times(1)).getProject(1789);
		verify(projectHandler, times(1)).containsProject(1789);
		verify(projectHandler, never()).saveProjectAnalysis(any(ProjectAnalysis.class));
	}

	@Test
	@WithMockUser
	public void dataInconsistency() throws Exception {
		this.mvc.perform(put("/api/project/312/analysis")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(new ProjectAnalysis(392))))
				.andExpect(status().isBadRequest());

	}

}
