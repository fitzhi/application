package com.tixhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.controller.ProjectController;
import com.tixhi.controller.in.BodyParamSonarEntry;
import com.tixhi.data.internal.Project;
import com.tixhi.data.internal.Skill;
import com.tixhi.data.internal.SonarProject;

/**
 * <p>
 * Test the method {@link ProjectController#save(com.tixhi.data.internal.Project)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerCreateAndUpdateProjectTest {
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;
	
	@Test
	public void test() throws Exception {

		Project newProject = new Project(-1, "name of the project");
		newProject.getSkills().add(new Skill(1, "title of skill"));
		newProject.getSonarProjects().add(new SonarProject("idProjectSonar", "name of project"));
		
		System.out.println(gson.toJson(newProject));
		
		this.mvc.perform(post("/api/project/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(newProject)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		
		Optional<Project> oProject = projectHandler.lookup("name of the project");
		Assert.assertTrue(oProject.isPresent());
		Assert.assertTrue(oProject.get().getSkills().size() == 1);
		Assert.assertTrue(oProject.get().getSonarProjects().size() == 1);
		Assert.assertTrue("idProjectSonar".contentEquals(oProject.get().getSonarProjects().get(0).getKey()));
		Assert.assertTrue("name of project".contentEquals(oProject.get().getSonarProjects().get(0).getName()));

		int id = oProject.get().getId();
		
		// 
		// ADDING A SONAR ENTRY.
		//
		SonarProject entry = new SonarProject("otherId", "other name");
		BodyParamSonarEntry bpse = new BodyParamSonarEntry(id, entry);
		this.mvc.perform(post("/api/project/sonar/saveEntry")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpse)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		
		Project p = projectHandler.get(id);
		Assert.assertTrue(p != null);
		Assert.assertTrue(p.getSonarProjects().size() == 2);
		Assert.assertTrue("otherId".contentEquals(oProject.get().getSonarProjects().get(1).getKey()));
		Assert.assertTrue("other name".contentEquals(oProject.get().getSonarProjects().get(1).getName()));
		
		SonarProject sp = p.getSonarProjects().get(1);
		Assert.assertEquals(4, sp.getProjectSonarMetricValues().size());
		Assert.assertEquals("bugs", sp.getProjectSonarMetricValues().get(0).getKey());
		Assert.assertEquals(40, sp.getProjectSonarMetricValues().get(0).getWeight());
		
		// 
		// DELETING A SONAR ENTRY.
		//
		entry = new SonarProject("otherId", "new other name");
		bpse = new BodyParamSonarEntry(id, entry);
		this.mvc.perform(post("/api/project/sonar/removeEntry")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpse)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		
		p = projectHandler.get(id);
		Assert.assertTrue(p != null);
		Assert.assertTrue(p.getSonarProjects().size() == 1);
	
	}

}
