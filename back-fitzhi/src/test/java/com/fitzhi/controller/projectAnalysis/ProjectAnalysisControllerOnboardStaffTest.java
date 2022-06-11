package com.fitzhi.controller.projectAnalysis;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectAuditController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.CommitRepository;
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
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
/**
 * <p>
 * Test of the class {@link ProjectAuditController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = ProjectAnalysisControllerOnboardStaffTest.Initializer.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = { "cache_duration=100000" }) 
public class ProjectAnalysisControllerOnboardStaffTest {


	/**
	 * This initializer is there to setup the {@code cache.working.dir} with the appropriate path for the OS environment.
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"cache.working.dir=" +  MessageFormat.format(".{0}src{0}test{0}resources{0}cacheDirRepository{0}", File.separator))
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;

	@Autowired
	private StaffHandler staffHandler;
	
	@Autowired
	CacheDataHandler cacheDataHandler;
	
	CommitRepository savedRepository;
	
	@Before
	public void before() throws ApplicationException {
		Project project = new Project(1917, "The Red Rev project");
		savedRepository = cacheDataHandler.getRepository(project);
	}
	
	@Test
	@WithMockUser
	public void testOnBoardingNominal() throws Exception {
		Project project = new Project(1917, "The Red Rev project");
		projectHandler.addNewProject(project);
		
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "altF4", "fvidal", "frvidal@void.com", "OIM");
		staffHandler.getStaff().put(1, staff);
		
		MvcResult result = this.mvc.perform(post("/api/project/1917/analysis/onboard/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		Boolean b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);
	}
	

	@After
	public void after() throws ApplicationException {
		Project project = new Project(1917, "The Red Rev project");
		cacheDataHandler.saveRepository(project, savedRepository);
	}
	
}
