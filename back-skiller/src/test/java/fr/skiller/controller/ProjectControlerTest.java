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

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.SkillHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControlerTest {

	private static final String ID_PROJECT = "{ idProject: ";

	private static final String PROJECT_SKILLS = "/project/skills/";

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;
	
	@Autowired
	private SkillHandler skillHandler;
		
	private static final int ID_INFOTER = 11;
	
	@Before 
	public void before() throws SkillerException {
		if (!projectHandler.lookup("INFOTER").isPresent()) {
			projectHandler.getProjects().put(ID_INFOTER, new Project(ID_INFOTER, "INFOTER"));
		}
		if (!skillHandler.containsSkill(1)) {
			skillHandler.addNewSkill(new Skill(1, "Java"));
		}
		if (!skillHandler.containsSkill(2)) {
			skillHandler.addNewSkill(new Skill(2, ".NET"));
		}
	}
	
	@Test
	public void addSkillInsideAProject() throws Exception {
		
		this.mvc.perform(get(PROJECT_SKILLS+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().string("[]"));	

		String body = ID_PROJECT + ID_INFOTER + ", formerSkillTitle: \"\", newSkillTitle: \"Java\"}";
		this.mvc.perform(post("/project/skills/save").content(body)).andExpect(status().isOk());		
		List<Skill> skills = new ArrayList<>();
		skills.add(new Skill (1, "Java"));
		this.mvc.perform(get(PROJECT_SKILLS+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));
	
		body = ID_PROJECT + ID_INFOTER + ", formerSkillTitle: \"Java\", newSkillTitle: \""+skillHandler.getSkills().get(2).getTitle()+"\"}";
		this.mvc.perform(post("/project/skills/save").content(body)).andExpect(status().isOk());		
		skills.clear();
		skills.add(new Skill (2, skillHandler.getSkills().get(2).getTitle()));
		this.mvc.perform(get(PROJECT_SKILLS+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));

		body = ID_PROJECT + ID_INFOTER + ", idSkill: 2}";
		this.mvc.perform(post("/project/skills/del").content(body)).andExpect(status().isOk());		
		skills.clear();
		this.mvc.perform(get(PROJECT_SKILLS+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));
		projectHandler.init();
	}
}
