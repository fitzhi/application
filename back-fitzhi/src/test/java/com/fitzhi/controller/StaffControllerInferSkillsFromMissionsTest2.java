/**
 * 
 */
package com.fitzhi.controller;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerInferSkillsFromMissionsTest2 {

	private static final String STAFF_EXPERIENCES_SAVE = "/api/staff/experiences/update";

	private static final String STAFF_EXPERIENCES_ADD = "/api/staff/experiences/add";
	
	private static final String STAFF_EXPERIENCES_REMOVE = "/api/staff/experiences/remove";

	private static final String STAFF_EXPERIENCES_1 = "/api/staff/experiences/1";

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
	@WithMockUser
	public void addAndUpdateASkillForAStaffMember() throws Exception {

		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{ \"idStaff\": 1, \"idSkill\": 2 , \"level\": 2}";
		this.mvc.perform(post(STAFF_EXPERIENCES_ADD)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());		
		
		List<Experience> assets = new ArrayList<>();
		
		assets.add(new Experience(2,  2));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		body = "{ \"idStaff\": 1, \"idSkill\": 2 , \"level\": 4}";
		this.mvc.perform(post(STAFF_EXPERIENCES_SAVE)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());		
		
		assets.clear();
		assets.add(new Experience (2, 4));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		staffHandler.init();
	}

	
	@Test
	@WithMockUser
	public void addAndRemoveASkillForAStaffMember() throws Exception {
		
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().string("[]"));	
		String body = "{ \"idStaff\": 1, \"idSkill\": 2, \"level\": 2}";
		this.mvc.perform(post(STAFF_EXPERIENCES_ADD)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());		
		
		List<Experience> assets = new ArrayList<>();
		
		assets.add(new Experience(2,  2));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		body = "{\"idStaff\": 1, \"idSkill\": 2}";
		this.mvc.perform(post(STAFF_EXPERIENCES_REMOVE)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());
		
		assets.clear();
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		staffHandler.init();
		
	}	
	
}