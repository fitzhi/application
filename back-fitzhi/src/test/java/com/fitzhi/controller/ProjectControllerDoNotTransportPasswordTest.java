package com.fitzhi.controller;

import static com.fitzhi.Global.USER_PASSWORD_ACCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import com.fitzhi.Global;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Testing the URL /staff/save
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerDoNotTransportPasswordTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;
	
	private int ID_PROJECT = 1789;
	
	@Before 
	public void before() throws SkillerException {
		Project p = new Project(ID_PROJECT, "testingProject");
		p.setPassword("password");
		p.setConnectionSettings(USER_PASSWORD_ACCESS);
		projectHandler.addNewProject(p);
	}
	
	/**
	 * We test here that the password is not transported.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void doNotTransportPassword() throws Exception {

		MvcResult result = this.mvc.perform(get("/api/project/id/"+ ID_PROJECT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		Project project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		Assert.assertNull(project.getPassword());
		
		result = this.mvc.perform(get("/api/project/name/testingProject"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		Assert.assertNull(project.getPassword());

		result = this.mvc.perform(get("/api/project/all"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		Project[] projects = gson.fromJson(result.getResponse().getContentAsString(), Project[].class);
		for (Project p : projects) {
			Assert.assertNull(p.getPassword());
		}
		
		project = projectHandler.get(ID_PROJECT);
		Assert.assertNotNull(project.getPassword());
		
	}
	
	/**
	 * If we save the project (without password), we do not loose the password when we update the project.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void doNotLoosePassword() throws Exception {

		MvcResult result = this.mvc.perform(get("/api/project/id/" + ID_PROJECT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		Project project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		Assert.assertNull(project.getPassword());

		
		this.mvc.perform(put("/api/project/" + ID_PROJECT)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(project)))
				.andExpect(status().isNoContent());
		
		project = projectHandler.get(ID_PROJECT);
		String password = DataEncryption.decryptMessage(project.getPassword());
		Assert.assertEquals("password", password);
		
		project = (Project) Global.deepClone(project);
		project.setPassword("newPassword");
		this.mvc.perform(put("/api/project/" + ID_PROJECT)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(project)))
				.andExpect(status().isNoContent());
		
		project = projectHandler.get(ID_PROJECT);
		password = DataEncryption.decryptMessage(project.getPassword());
		Assert.assertEquals("newPassword", password);
	}
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(ID_PROJECT);
	}
}