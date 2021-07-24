package com.fitzhi.controller.projectSonar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectSonarController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSonarMetricValue;
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
 * Test of the class {@link ProjectSonarController#getSonarProject(int, String)}.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSonarControllerGetSonarProjectTest {

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
		sp.getProjectSonarMetricValues().add(new ProjectSonarMetricValue("bugs", 10, 1));
		project.getSonarProjects().add(sp);
		
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {
	
		MvcResult result = this.mvc.perform(get("/api/project/1/sonar/key-sonar-1"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andDo(print())
			.andReturn();
		
		SonarProject sp = gson.fromJson(result.getResponse().getContentAsString(), SonarProject.class);
		Assert.assertNotNull(sp);

		//
		// THIS PROJECT HAS BEEN ADDED TO THIS STAFF MEMBER.
		//
		Assert.assertTrue(sp.getProjectSonarMetricValues().size() == 1);
		Assert.assertTrue("bugs".equals(sp.getProjectSonarMetricValues().get(0).getKey()));
		Assert.assertTrue(sp.getProjectSonarMetricValues().get(0).getWeight() == 10);
		Assert.assertTrue(sp.getProjectSonarMetricValues().get(0).getValue() == 1);

	}
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.lookup(1);
		project.getSonarProjects().clear();
				
	}
	
}
