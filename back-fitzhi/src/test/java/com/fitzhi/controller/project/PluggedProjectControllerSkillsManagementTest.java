package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Testing the action of adding or removing a skill inside a project.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class PluggedProjectControllerSkillsManagementTest {

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
		if (log.isDebugEnabled()) {
			log.debug("Skill found " + skill.getTitle());
		}
				
		this.mvc.perform(put("/api/project/1/skill/2"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"));
		
		Project p = projectHandler.lookup(1);
		Assert.assertEquals("Project 1 has one skill", 1, p.getSkills().size());
		Assert.assertTrue("Project 1 has one skill with id 2", p.getSkills().containsKey(2));
		
		this.mvc.perform(delete("/api/project/1/skill/2"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"));

		Assert.assertTrue("Project 1 has not anymore any skill declared", p.getSkills().isEmpty());
	}
	
}
