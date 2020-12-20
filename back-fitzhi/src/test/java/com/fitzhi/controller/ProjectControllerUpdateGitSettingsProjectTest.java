package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.fitzhi.Global;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * In this test, we will challenge & test the behavior of the application when the end-user change the GIT settings, such as 
 * </p>
 * 
 * <ul>
 * <li>the URL of the repository</li>
 * <li>the branch name</li>
 * </ul>
 * <br/>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerUpdateGitSettingsProjectTest {
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	@Before
	public void before() throws Exception {
		Project project1789 = new Project(1789, "The great revolutionary project");
		project1789.setActive(true);
		project1789.setConnectionSettings(Global.NO_USER_PASSWORD_ACCESS);
		project1789.setUrlRepository("https://github.com/fitzhi/application.git");
		project1789.setBranch("release-x.x");
		projectHandler.addNewProject(project1789);
	}

	@Test
	@WithMockUser
	public void test() throws Exception {

		Project project = projectHandler.get(1789);
		project.setBranch("master");
		
		this.mvc.perform(put("/api/project/1789")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(project)))
			.andExpect(status().isNoContent());

		project = projectHandler.get(1789);
		Assert.assertEquals("master", project.getBranch());
		
	}

	@After
	public void after() throws Exception {
		projectHandler.removeProject(1789);
	}

}
