package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;
import com.fitzhi.data.internal.Staff;

/**
 * <p>
 * Test the method {@link ProjectController#updateListStaff(int, java.util.List)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSlaveSaveStaffTest {

	private int UNKNOWN_ID_PROJECT = 999999;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ProjectHandler projectHandler;

	@MockBean
	private StaffHandler staffHandler;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@WithMockUser
	public void unknownProject() throws Exception {
		when(projectHandler.containsProject(UNKNOWN_ID_PROJECT)).thenReturn(false);

		this.mvc.perform(put("/api/project/1789/staff")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(objectMapper.writeValueAsString(Collections.EMPTY_LIST).getBytes()))

			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("There is no project for the identifier 1789")))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andDo(print());

		verify(projectHandler, never()).getProject(UNKNOWN_ID_PROJECT);
		verify(projectHandler, never()).saveProjectAnalysis(any(ProjectAnalysis.class));
	}
	
	private List<Staff> staff() {
		List<Staff> l = new ArrayList<>();
		l.add(new Staff(1, "firstName 1", "lastName 1", "nickName 1", "login 1", "email 1", "level 1"));
		l.add(new Staff(2, "firstName 2", "lastName 2", "nickName 2", "login 2", "email 2", "level 2"));
		return l;
	}

	@Test
	@WithMockUser
	public void update() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(true);
		Project p = new Project(1789, "The French Revolution");
		p.setActive(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(staffHandler).updateStaffAfterAnalysis(any(Project.class), anyList());

		this.mvc.perform(put("/api/project/1789/staff")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(staff())))
				.andExpect(status().isNoContent());

		verify(projectHandler, times(1)).getProject(1789);
		verify(projectHandler, times(1)).containsProject(1789);
		verify(staffHandler, times(1)).updateStaffAfterAnalysis(any(Project.class), anyList());
	}

	@Test
	@WithMockUser
	public void realData() throws Exception {

		File file = new File("./src/test/resources/slave-save-data/staff.json");
		final BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder staff = br.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
		br.close();

		when(projectHandler.containsProject(1789)).thenReturn(true);
		Project p = new Project(1789, "The French Revolution");
		p.setActive(true);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(staffHandler).updateStaffAfterAnalysis(any(Project.class), anyList());

		this.mvc.perform(put("/api/project/1789/staff")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(staff.toString()))
				.andExpect(status().isNoContent());

		verify(projectHandler, times(1)).getProject(1789);
		verify(projectHandler, times(1)).containsProject(1789);
		verify(staffHandler, times(1)).updateStaffAfterAnalysis(any(Project.class), anyList());
	}
	
	@Test
	@WithMockUser
	public void inactiveProjectReadOnly() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(true);
		Project p = new Project(1789, "The French Revolution");
		p.setActive(false);
		when(projectHandler.getProject(1789)).thenReturn(p);
		doNothing().when(projectHandler).saveProjectAnalysis(any(ProjectAnalysis.class));

		this.mvc.perform(put("/api/project/1789/staff")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(staff())))
				.andExpect(status().isMethodNotAllowed());

		verify(projectHandler, times(1)).getProject(1789);
		verify(projectHandler, times(1)).containsProject(1789);
		verify(staffHandler, never()).updateStaffAfterAnalysis(any(Project.class), anyList());
	}


}
