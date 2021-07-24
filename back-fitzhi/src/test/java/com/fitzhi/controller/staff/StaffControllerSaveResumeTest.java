package com.fitzhi.controller.staff;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.ResumeSkill;
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
 * Testing the URL {@code /{idStaff}/experiences} and the controller {@link StaffController#saveResume(int, com.fitzhi.data.internal.ResumeSkill[])}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerSaveResumeTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private StaffHandler staffHandler;
	
	/**
	 * Testing the modification of the staff member with ID = 1000.
	 */
	@Test
	@WithMockUser
	public void saveResumeOk() throws Exception {
		
		ResumeSkill[] skills = new ResumeSkill[2];
		skills[0] = new ResumeSkill(1, "one", 100);
		skills[1] = new ResumeSkill(1, "two", 100);

		when(staffHandler.addExperiences(1789, skills)).thenReturn(new Staff(1789, "login", ""));

		this.mvc.perform(put("/api/staff/1789/resume")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(skills)))

			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.idStaff", is(1789)))
			.andDo(print())
			.andReturn();
			
		Mockito.verify(staffHandler, times(1)).addExperiences(1789, skills);
	}

	@Test
	@WithMockUser
	public void saveResumeKO() throws Exception {
		
		ResumeSkill[] skills = new ResumeSkill[1];
		skills[0] = new ResumeSkill(1, "one", 100);

		when(staffHandler.addExperiences(1789, skills))
			.thenThrow(new ApplicationException(666, "What's the hell!"));

		this.mvc.perform(put("/api/staff/1789/resume")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(skills)))

			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(666)))
			.andExpect(jsonPath("$.message", is("What's the hell!")))
			.andDo(print())
			.andReturn();
			
		Mockito.verify(staffHandler, times(1)).addExperiences(1789, skills);
	}

}