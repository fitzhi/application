/**
 * 
 */
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.SkillHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.Skill;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerSkillTest {

	private static final String STAFF_EXPERIENCES_SAVE = "/staff/experiences/save";

	private static final String STAFF_EXPERIENCES_1 = "/staff/experiences/1";

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	@Autowired
	private SkillHandler skillHandler;
	
	@Before
	public void before() {
		if (!skillHandler.containsSkill(1)) {
			skillHandler.addNewSkill(new Skill(1, "JAVA"));
		}
		if (!skillHandler.containsSkill(2)) {
			skillHandler.addNewSkill(new Skill(2, ".NET"));
		}
	}
	
	@Test
	public void addAndUpdateASkillForAStaffMember() throws Exception {

		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{ idStaff: 1, formerSkillTitle: \"\", newSkillTitle: \""+ skillHandler.getSkills().get(2).getTitle()  +"\", level: 2}";
		this.mvc.perform(post(STAFF_EXPERIENCES_SAVE).content(body)).andExpect(status().isOk());		
		
		List<Experience> assets = new ArrayList<>();
		
		assets.add(new Experience(2, skillHandler.getSkills().get(2).getTitle(), 2));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		body = "{ idStaff: 1, formerSkillTitle: \"" + skillHandler.getSkills().get(2).getTitle() + "\", newSkillTitle: \"Java\", level: 3}";
		this.mvc.perform(post(STAFF_EXPERIENCES_SAVE).content(body)).andExpect(status().isOk());		
		
		assets.clear();
		assets.add(new Experience (1, skillHandler.getSkills().get(1).getTitle(), 3));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		/*
		 * We down-grade the level from 3 to 1.
		 */
		body = "{ idStaff: 1, formerSkillTitle: \"" + skillHandler.getSkills().get(2).getTitle() + "\", newSkillTitle: \"" + skillHandler.getSkills().get(1).getTitle() +"\", level: 1}";
		this.mvc.perform(post(STAFF_EXPERIENCES_SAVE).content(body)).andExpect(status().isOk());		

		assets.clear();
		assets.add(new Experience (1, skillHandler.getSkills().get(1).getTitle(), 1));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		staffHandler.init();
	}

	
	@Test
	public void addAndRemoveASkillForAStaffMember() throws Exception {
		
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{idStaff: 1, formerSkillTitle: \"\", newSkillTitle: \""+skillHandler.getSkills().get(2).getTitle()+"\", level: 1}";
		this.mvc.perform(post(STAFF_EXPERIENCES_SAVE).content(body)).andExpect(status().isOk());		

	
		List<Experience> assets = new ArrayList<>();
		assets.add(new Experience (2, skillHandler.getSkills().get(2).getTitle(), 1));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		body = "{idStaff: 1, idSkill: 2}";
		this.mvc.perform(post("/staff/experiences/del").content(body)).andExpect(status().isOk());
		
		assets.clear();
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		staffHandler.init();
		
	}	
	
}