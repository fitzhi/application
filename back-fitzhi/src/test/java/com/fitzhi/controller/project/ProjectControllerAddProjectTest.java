package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_STAFF_NOFOUND;
import static com.fitzhi.Error.MESSAGE_STAFF_NOFOUND;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.text.MessageFormat;
import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectGhostController;
import com.fitzhi.controller.in.GhostAssociation;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
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
 * Test of the method {@link ProjectGhostController#saveGhost(int, GhostAssociation)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerAddProjectTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@MockBean
	private StaffHandler staffHandler;

	@Test
	@WithMockUser
	public void addProjectOk() throws Exception {

		when(staffHandler.getStaff(1789)).thenReturn(new Staff(1789, "login", ""));
		when(projectHandler.find(1805)).thenReturn(new Project(1805, "Austerlitz"));
		doNothing().when(staffHandler).addMission(1789, 1805, "Austerlitz");

		this.mvc.perform(put("/api/staff/1789/project/1805"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(staffHandler, times(1)).getStaff(1789);
		Mockito.verify(projectHandler, times(1)).find(1805);
		Mockito.verify(staffHandler, times(1)).addMission(1789, 1805, "Austerlitz");
	}

	@Test
	@WithMockUser
	public void addProjectKO() throws Exception {

		when(staffHandler.getStaff(1789)).thenReturn(null);

		this.mvc.perform(put("/api/staff/1789/project/1805"))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_STAFF_NOFOUND)))
			.andExpect(jsonPath("$.message", is(MessageFormat.format(MESSAGE_STAFF_NOFOUND, 1789))))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(staffHandler, times(1)).getStaff(1789);
	}

}
