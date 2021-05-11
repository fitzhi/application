package com.fitzhi.controller.projectAnalysis;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

/**
 * <p>Class in charge of the test of {@link com.fitzhi.controller.ProjectAnalysisController#lookupDir(int, String)}</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectAnalysisControllerLookupDirTest {

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
	public void before() throws ApplicationException {
		project = new Project(9999, "Project 9999");
		project.setBranch("master");
		projectHandler.addNewProject(project);
	}
	
	@Test
	@WithMockUser	
	public void lookup() throws Exception {
	
		List<String> continents  = new ArrayList<>();
		continents.add("africa");
		continents.add("america");
		continents.add("antartic");
		continents.add("asia");
		
		mvc.perform(get("/api/project/9999/analysis/lib-dir/a"))
		    .andExpect(status().isOk())
		    .andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().json(gson.toJson(continents)));
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(9999);
	}
	
}
