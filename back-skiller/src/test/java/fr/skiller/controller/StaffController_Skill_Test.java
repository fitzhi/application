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
import fr.skiller.data.internal.Skill;

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

		this.mvc.perform(get("/staff/skills/2")).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{ idStaff: 2, formerSkillTitle: \"\", newSkillTitle: \"Spring\"}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		
		
		List<Skill> skills = new ArrayList<Skill>();
		skills.add(new Skill(2, "Spring"));
		this.mvc.perform(get("/staff/skills/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));

		body = "{ idStaff: 2, formerSkillTitle: \"Spring\", newSkillTitle: \"Java\"}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		
		
		skills.clear();
		skills.add(new Skill (1, "Java"));
		this.mvc.perform(get("/staff/skills/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));

		staffHandler.init();
	}

	
	@Test
	public void addAndRemoveASkillForAStaffMember() throws Exception {
		
		this.mvc.perform(get("/staff/skills/2")).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{idStaff: 2, formerSkillTitle: \"\", newSkillTitle: \"Spring\"}";
		this.mvc.perform(post("/staff/skill/save").content(body)).andExpect(status().isOk());		

	
		List<Skill> skills = new ArrayList<Skill>();
		skills.add(new Skill (2, "Spring"));
		this.mvc.perform(get("/staff/skills/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));
		
		body = "{idStaff: 2, idSkill: 2}";
		this.mvc.perform(post("/staff/skills/del").content(body)).andExpect(status().isOk());
		
		skills.clear();
		this.mvc.perform(get("/staff/skills/2")).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));
		
		staffHandler.init();
		
	}	
	
}