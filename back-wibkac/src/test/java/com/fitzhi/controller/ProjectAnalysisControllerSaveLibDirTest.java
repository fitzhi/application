package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>Class in charge of the test of {@link com.fitzhi.controller.ProjectAnalysisController#lookupDir(int, String)}</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAnalysisControllerSaveLibDirTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	Project project;
	@Before
	public void before() throws SkillerException {
		project = new Project(9999, "Project 9999");
		projectHandler.addNewProject(project);
	}
	
	@Test
	@WithMockUser	
	public void testA() throws Exception {
	
		List<Library> continents  = new ArrayList<>();
		continents.add(new Library("/africa", 1));
		continents.add(new Library("/america", 1));
		continents.add(new Library("/antartic", 1));
		continents.add(new Library("/asia", 1));
		
		String jsonInput = gson.toJson(continents);
		
		this.mvc.perform(post("/api/project/analysis/lib-dir/save/9999")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
		
		List<Library> libs = project.getLibraries();
		Assert.assertEquals("4 records in the libraries list", 4, libs.size());
	}

	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(9999);
	}
	
}
