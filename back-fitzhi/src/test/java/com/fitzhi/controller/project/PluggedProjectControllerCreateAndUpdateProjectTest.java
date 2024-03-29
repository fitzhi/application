package com.fitzhi.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.SonarProject;
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
import org.springframework.test.web.servlet.MvcResult;

/**
 * <p>
 * Test the method {@link ProjectController#save(com.fitzhi.data.internal.Project)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectControllerCreateAndUpdateProjectTest {
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	@Test
	@WithMockUser
	public void creationOfAnExistingProject() throws Exception {
		Project projectOne = new Project(1, "Project one");
		this.mvc.perform(post("/api/project/")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(projectOne)))
			.andExpect(status().isConflict());
	}
	
	@Test
	@WithMockUser
	public void workflow() throws Exception {

		Project newProject = new Project(-1, "name of the project");
		newProject.getSkills().put(1, new ProjectSkill(1));
		newProject.getSonarProjects().add(new SonarProject("idProjectSonar", "name of project"));
		
		int numberOfProjects = projectHandler
				.getProjects()
				.keySet()
				.stream()
				.mapToInt(v -> v)
				.max()
				.orElseThrow(NoSuchElementException::new);
		
		//
		// WE CREATE A NEW PROJECT.
		// The controller should return the CREATED (201) status and the location of the new entry
		//
		MvcResult result = this.mvc.perform(post("/api/project/")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(newProject)))
			.andExpect(status().isCreated())
			.andExpect(header().string("location", String.format("http://localhost/api/project/%d", numberOfProjects+1)))
			.andReturn();
		String location = result.getResponse().getHeader("location");
		
		//
		// WE RETRIEVE THE NEWLY CREATED PROJECT
		//
		result = this.mvc.perform(get(location))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andDo(print())
			.andReturn();

		Project project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		Assert.assertEquals(numberOfProjects+1, project.getId());
		Assert.assertEquals("name of the project", project.getName());
		Assert.assertTrue(project.getSkills().size() == 1);
		Assert.assertTrue(project.getSonarProjects().size() == 1);
		Assert.assertTrue("idProjectSonar".contentEquals(project.getSonarProjects().get(0).getKey()));
		Assert.assertTrue("name of project".contentEquals(project.getSonarProjects().get(0).getName()));

		// 
		// WE ADD A NEW SONAR ENTRY.
		//
		SonarProject entry = new SonarProject("otherId", "other name");
		this.mvc.perform(put("/api/project/" + project.getId() + "/sonar")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(entry)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		
		Project p = projectHandler.lookup(project.getId());
		Assert.assertTrue(p != null);
		Assert.assertTrue(p.getSonarProjects().size() == 2);
		Assert.assertTrue("otherId".contentEquals(p.getSonarProjects().get(1).getKey()));
		Assert.assertTrue("other name".contentEquals(p.getSonarProjects().get(1).getName()));
		
		SonarProject sp = p.getSonarProjects().get(1);
		Assert.assertEquals(4, sp.getProjectSonarMetricValues().size());
		Assert.assertEquals("bugs", sp.getProjectSonarMetricValues().get(0).getKey());
		Assert.assertEquals(40, sp.getProjectSonarMetricValues().get(0).getWeight());
		
		// 
		// WE REMOVE THE SONAR ENTRY
		//
		this.mvc.perform(delete("/api/project/" + project.getId() + "/sonar/otherId")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		
		p = projectHandler.lookup(project.getId());
		Assert.assertTrue(p != null);
		Assert.assertTrue(p.getSonarProjects().size() == 1);
	
	}

}
