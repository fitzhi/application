package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
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
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;

/**
 * Testing the URL {@code /api/staff/analysis} handled by the controller {@link StaffController}.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerUpdateListStaffTest {

	private final String STAFF_SAVE = "/api/project/%d/staff";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@MockBean
	private StaffHandler staffHandler;
	
	@MockBean
	private ProjectHandler projectHandler;

	private List<Staff> staff()  {
		// We create one staff member with ID = 1000 
		Staff staff = new Staff(1000,"Frédéric", "VIDAL", "myNickName" , "myLogin", "frvidal@void.com", "level");
		List<Staff> list = new ArrayList<>();
		list.add(staff);
		return list;
	}
	
	/**
	 * Testing the end-point /ap/project/{}/staff.
	 */
	@Test
	@WithMockUser
	public void shouldCorrectlyUpdateTheUpdatedStaff() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(true);
		when(projectHandler.getProject(1789)).thenReturn(new Project(1789, "The revolutionary project"));
		doNothing().when(staffHandler).updateStaffAfterAnalysis(new Project(1789, "The revolutionary project"), staff());

		this.mvc.perform(put(String.format(STAFF_SAVE, 1789))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(objectMapper.writeValueAsBytes(staff())));

		verify(staffHandler, times(1)).updateStaffAfterAnalysis(new Project(1789, "The revolutionary project"), staff());
	}
	
	/**
	 * Testing the end-point /ap/project/{}/staff.
	 */
	@Test
	@WithMockUser
	public void projectNotFound() throws Exception {
		when(projectHandler.containsProject(1789)).thenReturn(false);

		this.mvc.perform(put(String.format(STAFF_SAVE, 1789))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(objectMapper.writeValueAsBytes(staff())))

			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("There is no project for the identifier 1789")))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andDo(print());

		verify(staffHandler, never()).updateStaffAfterAnalysis(any(Project.class), anyList());
	}
}