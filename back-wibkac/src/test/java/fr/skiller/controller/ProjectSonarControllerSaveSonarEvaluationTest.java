package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyParamProjectSonarEvaluation;
import fr.skiller.controller.util.LocalDateAdapter;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.SonarEvaluation;
import fr.skiller.data.internal.SonarProject;
import fr.skiller.exception.SkillerException;

/**
 * <p>
 * Test of the method {@link ProjectSonarController#saveEvaluation(fr.skiller.controller.in.BodyParamProjectSonarEvaluation)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSonarControllerSaveSonarEvaluationTest {

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
	public void before() throws SkillerException {
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
		BodyParamProjectSonarEvaluation bppse = new BodyParamProjectSonarEvaluation();
		bppse.setIdProject(ID_PROJECT);
		bppse.setSonarKey(KEY_SONAR_1);
		SonarEvaluation sonarEvaluation = new SonarEvaluation();
		sonarEvaluation.setEvaluation(50);
		sonarEvaluation.setTotalNumberLinesOfCode(3414);
		bppse.setSonarEvaluation(sonarEvaluation);
	
		MvcResult result = this.mvc.perform(post("/api/project/sonar/saveEvaluation")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bppse)))
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
		Assert.assertNotNull(sp.getSonarEvaluation());
		Assert.assertEquals(50, sp.getSonarEvaluation().getEvaluation());
		Assert.assertEquals(3414, sp.getSonarEvaluation().getTotalNumberLinesOfCode());

		SonarProject spEmpty = project.getSonarProjects().get(1);
		Assert.assertNull(spEmpty.getSonarEvaluation());
	}
	
	@After
	public void after() throws SkillerException {
		project = projectHandler.get(ID_PROJECT);
		project.getSonarProjects().clear();
				
	}
	
}
