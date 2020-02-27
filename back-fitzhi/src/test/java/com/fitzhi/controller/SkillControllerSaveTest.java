package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectionPattern;
import com.fitzhi.data.internal.SkillDetectorType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Testing the method
 * {@link SkillController#save(com.fitzhi.data.internal.Skill)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SkillControllerSaveTest {

	private static final String SKILL_SAVE = "/api/skill/save";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SkillHandler skillHandler;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	int countSkills = 0;

	@Before
	public void before() {
		countSkills = skillHandler.getSkills().size();
	}

	@Test
	@WithMockUser
	public void saveUnknownSkill() throws Exception {

		Skill skill = new Skill(9999, "unknown skill");
		this.mvc.perform(post(SKILL_SAVE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(skill)))
				.andExpect(status().isNotFound());

	}

	@Test
	@WithMockUser
	public void saveNewSkill() throws Exception {

		Skill skill = new Skill(0, "skill",
				new SkillDetectionPattern(SkillDetectorType.FILENAME_DETECTOR_TYPE, "*.skill$"));
		MvcResult result = this.mvc
				.perform(post(SKILL_SAVE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(skill)))
				.andExpect(status().isOk()).andReturn();

		Skill retSkill = gson.fromJson(result.getResponse().getContentAsString(), Skill.class);

		Assert.assertEquals(countSkills + 1, retSkill.getId());
		Assert.assertTrue(this.skillHandler.containsSkill(countSkills + 1));
		Assert.assertEquals("skill", retSkill.getTitle());
		Assert.assertEquals(SkillDetectorType.FILENAME_DETECTOR_TYPE,
				retSkill.getDetectionPattern().getDetectionType());
		Assert.assertEquals("*.skill$", retSkill.getDetectionPattern().getPattern());
	}

	@Test
	@WithMockUser
	public void saveNewSkillWithoutDetectionPattern() throws Exception {
		Skill skill = new Skill(0, "empty Skill");
		MvcResult result = this.mvc
				.perform(post(SKILL_SAVE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(skill)))
				.andExpect(status().isOk()).andReturn();

		Skill retSkill = gson.fromJson(result.getResponse().getContentAsString(), Skill.class);
		Assert.assertEquals(countSkills + 1, retSkill.getId());
		Assert.assertNull(retSkill.getDetectionPattern());
	}

	@Test
	@WithMockUser
	public void saveSkillUpdateDetectionPattern() throws Exception {

		Skill skill = new Skill(0, "empty Skill");
		MvcResult result = this.mvc
				.perform(post(SKILL_SAVE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(skill)))
				.andExpect(status().isOk()).andReturn();

		Skill skill2 = gson.fromJson(result.getResponse().getContentAsString(), Skill.class);
		skill2.setTitle("skill");
		skill2.setDetectionPattern(new SkillDetectionPattern(SkillDetectorType.FILENAME_DETECTOR_TYPE, "*.skill$"));

		result = this.mvc
				.perform(post(SKILL_SAVE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(skill2)))
				.andExpect(status().isOk()).andReturn();
		Skill skill3 = gson.fromJson(result.getResponse().getContentAsString(), Skill.class);

		Assert.assertEquals(countSkills + 1, skill3.getId());
		Assert.assertEquals("skill", skill3.getTitle());
		Assert.assertNotNull(skill3.getDetectionPattern());
		Assert.assertEquals(SkillDetectorType.FILENAME_DETECTOR_TYPE, skill3.getDetectionPattern().getDetectionType());
		Assert.assertEquals("*.skill$", skill3.getDetectionPattern().getPattern());

	}

	@After
	public void after() {
		skillHandler.getSkills().remove(777);
	}
}
