package fr.skiller.controller;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.external.BooleanDTO;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;

/**
 * <p>Testing the action of adding or removing a skill inside a project.</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControlerSkillsManagementTest {

	/**
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(ProjectControlerSkillsManagementTest.class.getCanonicalName());

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
		
	@Autowired
	private ProjectController projectController;
	
	@Test
	@WithMockUser
	public void test() throws Exception {
		
		Skill skill = skillHandler.getSkill(2);
		if (logger.isDebugEnabled()) {
			logger.debug("Skill found " + skill.getTitle());
		}
		
		ProjectController.ParamProjectSkill ps = projectController.new ParamProjectSkill();
		ps.idProject = 1;
		ps.idSkill = 2;

		String jsonInput = gson.toJson(ps);
		
		this.mvc.perform(post("/project/skill/add")
		.content(jsonInput))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().json(gson.toJson(new BooleanDTO())));
		
		Project p = projectHandler.get(1);
		Assert.assertEquals("Project 1 has one skill width id 2", 1, 
			p.getSkills().stream().map(Skill::getId)
				.filter(id -> id == 2)
				.count());
								
		this.mvc.perform(post("/project/skill/del")
		.content(jsonInput))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().json(gson.toJson(new BooleanDTO())));

		Assert.assertEquals("Project 1 has not anymore the skill 2 declared within its", 0, 
				p.getSkills().stream().map(Skill::getId)
					.filter(id -> id == 2)
					.count());
	}
	
}
