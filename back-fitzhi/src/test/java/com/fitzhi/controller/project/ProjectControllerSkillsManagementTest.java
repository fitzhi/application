package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.controller.in.BodyParamProjectSkill;
import com.fitzhi.data.external.BooleanDTO;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>Testing the action of adding or removing a skill inside a project.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSkillsManagementTest {

	/**
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(ProjectControllerSkillsManagementTest.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SkillHandler skillHandler;
		
	@Autowired
	private ProjectHandler projectHandler;
		
	@Test
	@WithMockUser
	public void test() throws Exception {
		
		Skill skill = skillHandler.getSkill(2);
		if (logger.isDebugEnabled()) {
			logger.debug("Skill found " + skill.getTitle());
		}
		
		BodyParamProjectSkill ps = new BodyParamProjectSkill();
		ps.setIdProject(1);
		ps.setIdSkill (2);

		String jsonInput = gson.toJson(ps);
		
		this.mvc.perform(post("/api/project/skill/add")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().json(gson.toJson(new BooleanDTO())));
		
		Project p = projectHandler.get(1);
		Assert.assertEquals("Project 1 has one skill", 1, p.getSkills().size());
		Assert.assertTrue("Project 1 has one skill with id 2", p.getSkills().containsKey(2));
		
		this.mvc.perform(post("/api/project/skill/del")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().json(gson.toJson(new BooleanDTO())));

		Assert.assertTrue("Project 1 has not anymore any skill declared", p.getSkills().isEmpty());
	}
	
}
