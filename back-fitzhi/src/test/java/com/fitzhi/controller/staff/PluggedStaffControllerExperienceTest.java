package com.fitzhi.controller.staff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <P>
 * This class contains "PLUGGED" tests for the endpoints of {@link StaffController} 
 * in charge of add/udpate/remove/ an experience for a developer.
 * </P>
 * <P>
 * "PLUGGED" means that the handlers behind each end-point are not mocked.
 * </P>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class PluggedStaffControllerExperienceTest {

	private static final String STAFF_EXPERIENCE = "/api/staff/%s/experience";
	
	private static final String STAFF_EXPERIENCE_REMOVE = "/api/staff/%s/experience/%s";

	private static final String STAFF_EXPERIENCES_1 = "/api/staff/1/experience";

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
	public void before() throws NotFoundException {
		// We clear the experiences of the staff
		staffHandler.getStaff(1).setExperiences(new ArrayList<Experience>());

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

		String body = "{ \"id\": 2, \"level\": 2}";
		this.mvc.perform(post(String.format(STAFF_EXPERIENCE, 1))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());		
		
		List<Experience> assets = new ArrayList<>();
		
		assets.add(new Experience(2,  2));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));

		body = "{ \"id\": 2 , \"level\": 4}";
		this.mvc.perform(post(String.format(STAFF_EXPERIENCE, 1))
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
		String body = "{ \"id\": 2, \"level\": 2}";
		this.mvc.perform(post(String.format(STAFF_EXPERIENCE, 1))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());		
		
		List<Experience> assets = new ArrayList<>();
		
		assets.add(new Experience(2,  2));
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		body = "{\"idStaff\": 1, \"idSkill\": 2}";
		this.mvc.perform(delete(String.format(STAFF_EXPERIENCE_REMOVE, 1, 2))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(body))
			.andExpect(status().isOk());
		
		assets.clear();
		this.mvc.perform(get(STAFF_EXPERIENCES_1)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(assets)));
		
		staffHandler.init();
		
	}	
	
}