package com.fitzhi.controller.project;

import static com.fitzhi.service.ConnectionSettingsType.PUBLIC_LOGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.data.internal.Project;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

/**
 * <p>
 * Test the method {@link ProjectController#branches(int)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectControllerBranchesTest {

	private int UNKNOWN_ID_PROJECT = 999999;
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
		project1789.setConnectionSettings(PUBLIC_LOGIN);
		project1789.setUrlRepository("https://github.com/fitzhi/application.git");;
		projectHandler.addNewProject(project1789);
	}
	
	@Test
	@WithMockUser
	public void testBranchesUnknownProject() throws Exception {
		this.mvc.perform(get(String.format("/api/project/%d/branches", UNKNOWN_ID_PROJECT))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser
	public void testBranchesProjectOk() throws Exception {

		MvcResult result = this.mvc.perform(get(String.format("/api/project/%d/branches", 1789)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))				
			.andDo(print())
			.andReturn();

		String[] branches = gson.fromJson(result.getResponse().getContentAsString(), String[].class);
		Assert.assertTrue("The master branch is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("master")));
		Assert.assertTrue("The release 1-1 is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("release-1-1")));
		Assert.assertTrue("The release 1-2 is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("release-1.2")));
		Assert.assertTrue("The release 1-3 is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("release-1.3")));
		Assert.assertTrue("The release 1-4 is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("release-1.4")));
		Assert.assertTrue("The release 1-5 is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("release-1.5")));
		Assert.assertTrue("The release 1-6 is expected to be here", Arrays.stream(branches).anyMatch(b -> b.equals("release-1.6")));
	}
	
	@After
	public void after() throws Exception {
		projectHandler.removeProject(1789);
	}
	
}
