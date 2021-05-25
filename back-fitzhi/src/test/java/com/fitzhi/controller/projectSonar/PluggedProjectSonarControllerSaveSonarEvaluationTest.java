package com.fitzhi.controller.projectSonar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectSonarController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarEvaluation;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;
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
 * Test of the method {@link ProjectSonarController#saveEvaluation(int, String, SonarEvaluation)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectSonarControllerSaveSonarEvaluationTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;

	Project project;
	
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.lookup(1);
		SonarProject sp = new SonarProject();
		sp.setKey("key-sonar-1");
		project.getSonarProjects().add(sp);
		sp = new SonarProject();
		sp.setKey("key-sonar-2");
		project.getSonarProjects().add(sp);
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {
		SonarEvaluation sonarEvaluation = new SonarEvaluation();
		sonarEvaluation.setEvaluation(50);
		sonarEvaluation.setTotalNumberLinesOfCode(3414);
	
		MvcResult result = this.mvc.perform(put("/api/project/1/sonar/key-sonar-1/evaluation")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(sonarEvaluation)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		
		Boolean b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);

		result = this.mvc.perform(get("/api/project/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		
		SonarProject sp = project.getSonarProjects().get(0);
		
		//
		// THIS PROJECT HAS BEEN ADDED TO THIS STAFF MEMBER.
		//
		Assert.assertNotNull(sp.getSonarEvaluation());
		Assert.assertEquals(50, sp.getSonarEvaluation().getEvaluation());
		Assert.assertEquals(3414, sp.getSonarEvaluation().getTotalNumberLinesOfCode());

		SonarProject spEmpty = project.getSonarProjects().get(1);
		Assert.assertNull(spEmpty.getSonarEvaluation());
	}
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.lookup(1);
		project.getSonarProjects().clear();
				
	}
	
}
