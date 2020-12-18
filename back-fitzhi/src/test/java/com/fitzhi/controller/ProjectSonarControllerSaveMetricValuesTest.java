package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamProjectSonarMetricValues;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSonarMetricValue;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Test of the method {@link ProjectSonarController#updateMetricValues(BodyParamProjectSonarMetricValues)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSonarControllerSaveMetricValuesTest {

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
	
	final int ID_PROJECT = 1;
	
	private final String KEY_SONAR_1 = "key-sonar-1";
	private final String KEY_SONAR_2 = "key-sonar-2";
	
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		SonarProject sp = new SonarProject();
		sp.setKey(KEY_SONAR_1);
		project.getSonarProjects().add(sp);
		sp = new SonarProject();
		sp.setKey(KEY_SONAR_2);
		project.getSonarProjects().add(sp);
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {
		BodyParamProjectSonarMetricValues bppsmv = new BodyParamProjectSonarMetricValues();
		bppsmv.setIdProject(ID_PROJECT);
		bppsmv.setSonarKey(KEY_SONAR_1);
		List<ProjectSonarMetricValue> list = new ArrayList<>();
		list.add(new ProjectSonarMetricValue("bugs1", 40, 10));
		list.add(new ProjectSonarMetricValue("bugs2", 40, 15));
		list.add(new ProjectSonarMetricValue("bugs3", 20, 17));
		bppsmv.setMetricValues(list);
	
		MvcResult result = this.mvc.perform(post("/api/project/sonar/saveMetricValues")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bppsmv)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		
		Boolean b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);

		result = this.mvc.perform(get("/api/project/id/" + ID_PROJECT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		
		SonarProject sp = project.getSonarProjects().get(0);
		
		//
		// THIS PROJECT HAS BEEN ADDED TO THIS STAFF MEMBER.
		//
		Assert.assertTrue(sp.getProjectSonarMetricValues().size() == 3);
		Assert.assertTrue("bugs1".equals(sp.getProjectSonarMetricValues().get(0).getKey()));
		Assert.assertTrue("bugs2".equals(sp.getProjectSonarMetricValues().get(1).getKey()));
		Assert.assertTrue("bugs3".equals(sp.getProjectSonarMetricValues().get(2).getKey()));

		Assert.assertTrue(sp.getProjectSonarMetricValues().get(0).getWeight() == 40);
		Assert.assertTrue(sp.getProjectSonarMetricValues().get(1).getWeight() == 40);
		Assert.assertTrue(sp.getProjectSonarMetricValues().get(2).getWeight() == 20);

		Assert.assertTrue(sp.getProjectSonarMetricValues().get(0).getValue() == 10);
		Assert.assertTrue(sp.getProjectSonarMetricValues().get(1).getValue() == 15);
		Assert.assertTrue(sp.getProjectSonarMetricValues().get(2).getValue() == 17);

	}
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		project.getSonarProjects().clear();
				
	}
	
}
