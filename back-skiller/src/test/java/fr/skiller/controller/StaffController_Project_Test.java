package fr.skiller.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffController_Project_Test {

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
	
	private static int ID_VEGEO = 10;
	private static int ID_INFOTER = 11;
	
	@Before 
	public void before() throws Exception {
		if (!projectHandler.lookup("VEGEO").isPresent()) {
			projectHandler.getProjects().put(ID_VEGEO, new Project(ID_VEGEO, "VEGEO"));
		}
		if (!projectHandler.lookup("INFOTER").isPresent()) {
			projectHandler.getProjects().put(ID_INFOTER, new Project(ID_INFOTER, "INFOTER"));
		}
	}
	@Test
	public void addAndUpdateAProjectForAStaffMember() throws Exception {

		this.mvc.perform(get("/staff/projects/2")).andExpect(status().isOk()).andExpect(content().string("[]"));	
	
		String body = "{ idStaff: 2, formerProjectName: \"\", newProjectName: \"VEGEO\"}";
		this.mvc.perform(post("/staff/project/save").content(body)).andExpect(status().isOk());		
		List<Mission> missions = new ArrayList<Mission>();
		missions.add(new Mission (ID_VEGEO, "VEGEO"));
		this.mvc.perform(get("/staff/projects/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(missions)));

		body = "{ idStaff: 2, formerProjectName: \"VEGEO\", newProjectName: \"INFOTER\"}";
		this.mvc.perform(post("/staff/project/save").content(body)).andExpect(status().isOk());		
		
		missions.clear();
		missions.add(new Mission (ID_INFOTER, "INFOTER"));
		this.mvc.perform(get("/staff/projects/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(missions)));

		staffHandler.init();
	}

	
	@Test
	public void addAndRemoveAProjectForAStaffMember() throws Exception {
		
		this.mvc.perform(get("/staff/projects/2")).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{ idStaff: 2, formerProjectName: \"\", newProjectName: \"VEGEO\"}";
		this.mvc.perform(post("/staff/project/save").content(body)).andExpect(status().isOk());		

	
		List<Mission> missions = new ArrayList<Mission>();
		missions.add(new Mission (ID_VEGEO, "VEGEO"));
		this.mvc.perform(get("/staff/projects/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(missions)));
		
		body = "{ idStaff: 2, idProject: "+ ID_VEGEO +"}";
		this.mvc.perform(post("/staff/project/del").content(body)).andExpect(status().isOk());
		
		missions.clear();
		this.mvc.perform(get("/staff/projects/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(missions)));
		
		staffHandler.init();
		
	}	

}