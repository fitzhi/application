package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fitzhi.bean.ProjectHandler;
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
public class ProjectAnalysisControllerLookupDirTest {

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
		project.setBranch("master");
		projectHandler.addNewProject(project);
	}
	
	@Test
	@WithMockUser	
	public void testA() throws Exception {
	
		List<String> continents  = new ArrayList<>();
		continents.add("africa");
		continents.add("america");
		continents.add("antartic");
		continents.add("asia");
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("idProject", "9999");
	    params.add("criteria", "a"); 
	    
		mvc.perform(get("/api/project/analysis/lib-dir/lookup")
		    .params(params)
		    .accept("application/json;charset=UTF-8"))
		    .andExpect(status().isOk())
		    .andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().json(gson.toJson(continents)));
	}

	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(9999);
	}
	
}
