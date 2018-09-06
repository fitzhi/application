/**
 * 
 */
package fr.skiller.controller;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Experience;

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
public class StaffController_Skill_Test {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	
	@Test
	public void addAndUpdateASkillForAStaffMember() throws Exception {

		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{ idStaff: 2, formerSkillTitle: \"\", newSkillTitle: \"Spring\", level: 2}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		
		
		List<Experience> assets = new ArrayList<Experience>();
		assets.add(new Experience(2, "Spring", 2));
		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		body = "{ idStaff: 2, formerSkillTitle: \"Spring\", newSkillTitle: \"Java\", level: 3}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		
		
		assets.clear();
		assets.add(new Experience (1, "Java", 3));
		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		/*
		 * We down-grade the level from 3 to 1.
		 */
		body = "{ idStaff: 2, formerSkillTitle: \"Spring\", newSkillTitle: \"Java\", level: 1}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		

		assets.clear();
		assets.add(new Experience (1, "Java", 1));
		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		staffHandler.init();
	}

	
	@Test
	public void addAndRemoveASkillForAStaffMember() throws Exception {
		
		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{idStaff: 2, formerSkillTitle: \"\", newSkillTitle: \"Spring\", level: 1}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		

	
		List<Experience> assets = new ArrayList<Experience>();
		assets.add(new Experience (2, "Spring", 1));
		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		body = "{idStaff: 2, idSkill: 2}";
		this.mvc.perform(post("/staff/experiences/del").content(body)).andExpect(status().isOk());
		
		assets.clear();
		this.mvc.perform(get("/staff/experiences/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		staffHandler.init();
		
	}	
	
}