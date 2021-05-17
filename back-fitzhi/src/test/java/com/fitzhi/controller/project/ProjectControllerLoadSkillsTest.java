package com.fitzhi.controller.project;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Test of the method {@link ProjectController#loadSkills(int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerLoadSkillsTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@Test
	@WithMockUser
	public void loadSkillsOK() throws Exception {

		Project p = new Project(1789, "1789");
		p.getSkills().put(1, new ProjectSkill(1));
		p.getSkills().put(2, new ProjectSkill(2));
		when(projectHandler.find(1789)).thenReturn (p);

		this.mvc.perform(get("/api/project/1789/skills"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].idSkill", is(1)))
			.andExpect(jsonPath("$[1].idSkill", is(2)))
			.andDo(print());

		Mockito.verify(projectHandler, times(1)).find(1789);

	}


	@Test
	@WithMockUser
	public void loadSkillsKO() throws Exception {

		when(projectHandler.find(666)).thenThrow(new NotFoundException(CODE_PROJECT_NOFOUND, "Error message"));

		this.mvc.perform(get("/api/project/666/skills"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)))
			.andExpect(jsonPath("$.message", is("Error message")));

		Mockito.verify(projectHandler, times(1)).find(666);

	}
}
