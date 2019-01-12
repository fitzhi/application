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

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControlerTest {

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
		
	private static int ID_INFOTER = 11;
	
	@Before 
	public void before() throws Exception {
		if (!projectHandler.lookup("INFOTER").isPresent()) {
			projectHandler.getProjects().put(ID_INFOTER, new Project(ID_INFOTER, "INFOTER"));
		}
	}
	
	@Test
	public void addSkillInsideAProject() throws Exception {
		
		this.mvc.perform(get("/project/skills/"+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().string("[]"));	

		String body = "{ idProject: " + ID_INFOTER + ", formerSkillTitle: \"\", newSkillTitle: \"Java\"}";
		this.mvc.perform(post("/project/skills/save").content(body)).andExpect(status().isOk());		
		List<Skill> skills = new ArrayList<Skill>();
		skills.add(new Skill (1, "Java"));
		this.mvc.perform(get("/project/skills/"+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));
	
		body = "{ idProject: " + ID_INFOTER + ", formerSkillTitle: \"Java\", newSkillTitle: \""+skillHandler.getSkills().get(2).title+"\"}";
		this.mvc.perform(post("/project/skills/save").content(body)).andExpect(status().isOk());		
		skills.clear();
		skills.add(new Skill (2, skillHandler.getSkills().get(2).title));
		this.mvc.perform(get("/project/skills/"+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));

		body = "{ idProject: " + ID_INFOTER + ", idSkill: 2}";
		this.mvc.perform(post("/project/skills/del").content(body)).andExpect(status().isOk());		
		skills.clear();
		this.mvc.perform(get("/project/skills/"+ID_INFOTER)).andExpect(status().isOk()).andExpect(content().json(gson.toJson(skills)));
		projectHandler.init();
	}
}
