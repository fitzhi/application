package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerProjectTest {

	private static final String STAFF_PROJECT_ADD = "/staff/project/add";

	private static final String STAFF_PROJECTS_2 = "/staff/projects/2";

	private static final int ID_PROJECT_1234 = 1234;
	private static final String PROJECT_1234 = "TEST_1234";
	
	private static final int ID_PROJECT_1235 = 1235;
	private static final String PROJECT_1235 = "TEST_1235";

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	@Autowired
	private ProjectHandler projectHandler;
	
	@Before 
	public void before() throws SkillerException {
		projectHandler.getProjects().put(ID_PROJECT_1235, new Project(ID_PROJECT_1235, PROJECT_1235));
	}
	
	@Test
	@WithMockUser
	public void addAndRemoveAProjectForAStaffMember() throws Exception {
		
		this.mvc.perform(get(STAFF_PROJECTS_2)).andExpect(status().isOk()).andExpect(content().string("[]"));	
		
		String body = "{ idStaff: 2, idProject: 1235}";
		this.mvc.perform(post(STAFF_PROJECT_ADD).content(body)).andExpect(status().isOk());		
		
		List<Mission> missions = new ArrayList<>();
		missions.add(new Mission (2, ID_PROJECT_1235, PROJECT_1235));
		this.mvc.perform(get(STAFF_PROJECTS_2))
				.andExpect(status()
				.isOk())
				.andExpect(content().json(gson.toJson(missions)));
		
		body = "{ idStaff: 2, idProject: 1235 }";
		this.mvc.perform(post("/staff/project/del").content(body)).andExpect(status().isOk());
		
		missions.clear();
		this.mvc.perform(get(STAFF_PROJECTS_2)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(missions)));
		
		
	}	

	@Before 
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(ID_PROJECT_1235);
	}

}