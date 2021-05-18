package com.fitzhi.controller.projectSonar;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.Error;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectGhostController;
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
 * Test of the class {@link ProjectGhostController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectSonarControllerSaveUrlSonarServerTest {

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
	
	private final String KEY_SONAR_1 = "key-sonar-1";
	
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.get(1);
		project.setUrlSonarServer("http://formerSonarServer");
		SonarProject sp = new SonarProject();
		sp.setKey(KEY_SONAR_1);
		sp.getProjectSonarMetricValues().add(new ProjectSonarMetricValue("bugs", 10, 1));
		project.getSonarProjects().add(sp);
		
	}
	
	/**
	 * Testing the fact that if project does not exist, the service will return {@code False}
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void testThrowIfProjectUnknown() throws Exception {
		
		this.mvc.perform(put("/api/project/666/sonar/url")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("url"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code", is(Error.CODE_PROJECT_NOFOUND)))
				.andDo(print())
				.andReturn();
		
	}
	
	/**
	 * Testing the impact of the change of the URL
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void testChangeTheURLSonarServer() throws Exception {
	
		MvcResult result = this.mvc.perform(put("/api/project/1/sonar/url")
				.contentType(MediaType.TEXT_PLAIN)
				.content("https://theNewUrlSonarServer"))
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
		
		Project p = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		Assert.assertNotNull(p);

		//
		// The project has no more Sonar project declared when changing the URL of the Sonar server.
		//
		Assert.assertTrue("https://theNewUrlSonarServer".equals(p.getUrlSonarServer()));
		Assert.assertTrue(p.getSonarProjects().isEmpty());
	}
	
	/**
	 * Testing "no change" = "no impact".
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void testNoChangeNoImpact() throws Exception {
		
		MvcResult result = this.mvc.perform(put("/api/project/1/sonar/url")
				.contentType(MediaType.TEXT_PLAIN)
				.content("http://formerSonarServer"))
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
		
		Project p = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		Assert.assertNotNull(p);

		//
		// The project has no more Sonar project declared when changing the URL of the Sonar server.
		//
		Assert.assertTrue("http://formerSonarServer".equals(p.getUrlSonarServer()));
		Assert.assertFalse(p.getSonarProjects().isEmpty());
	}
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.get(1);
		project.getSonarProjects().clear();
				
	}
	
}
