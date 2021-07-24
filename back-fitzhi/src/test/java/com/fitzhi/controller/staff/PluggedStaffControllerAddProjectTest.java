package com.fitzhi.controller.staff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedStaffControllerAddProjectTest {

	private static final String STAFF_PROJECTS_2 = "/api/staff/2/project";

	private static final int ID_PROJECT_1235 = 1235;
	private static final String PROJECT_1235 = "TEST_1235";

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;
	
	@Before 
	public void before() throws ApplicationException {
		projectHandler.addNewProject(new Project(ID_PROJECT_1235, PROJECT_1235));
	}
	
	@Test
	@WithMockUser
	public void addAndRemoveAProjectForAStaffMember() throws Exception {
		
		this.mvc.perform(get(STAFF_PROJECTS_2)).andExpect(status().isOk()).andExpect(content().string("[]"));	
		
		this.mvc.perform(put("/api/staff/2/project/1235")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andExpect(status().isOk());
		
		List<Mission> missions = new ArrayList<>();
		missions.add(new Mission (2, ID_PROJECT_1235, PROJECT_1235));
		this.mvc.perform(get(STAFF_PROJECTS_2))
				.andExpect(status().isOk())
				.andExpect(content().json(gson.toJson(missions)));
		
		this.mvc.perform(delete("/api/staff/2/project/1235")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk());
		
		missions.clear();
		this.mvc.perform(get(STAFF_PROJECTS_2))
			.andExpect(status().isOk())
			.andExpect(content().json(gson.toJson(missions)));
		
	}	

	@Before 
	public void after() throws ApplicationException {
		projectHandler.removeProject(ID_PROJECT_1235);
	}

}