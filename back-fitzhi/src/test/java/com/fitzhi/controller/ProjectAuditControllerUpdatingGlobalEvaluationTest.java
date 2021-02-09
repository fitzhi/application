package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
import com.fitzhi.controller.in.BodyParamAuditEntries;
import com.fitzhi.controller.in.BodyParamAuditEntry;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * <p>
 * Test of the class {@link ProjectAuditController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAuditControllerUpdatingGlobalEvaluationTest {


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

	final int ID_TOPIC_1 = 1;
	final int ID_TOPIC_2 = 2; 
	
	/**
	 * Test the value of the evaluation.
	 * @param idProject given project
	 * @param expectedValue expected value.
	 * @throws Exception
	 */
	void testGlobalAuditEvaluation(int idProject, int expectedValue) throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/"+ ID_PROJECT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		Project project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		
		Assert.assertEquals(expectedValue + " is the default weight", expectedValue, project.getAuditEvaluation());
	}

	@Before
	public void before() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);

		Map<Integer, AuditTopic> mapAudit = new HashMap<>();
		mapAudit.put(ID_TOPIC_1, new AuditTopic(ID_TOPIC_1, 80, 40));
		mapAudit.put(ID_TOPIC_2, new AuditTopic(ID_TOPIC_2, 30, 60));
		project.setAudit(mapAudit);
	}
	
	
	@Test
	@WithMockUser
	public void processAuditEvalutionForUnknownProject() throws Exception {
		
		//
		// Update the evaluation for the project 666
		//
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(666);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2, 60, 0));
	
		MvcResult result = this.mvc.perform(post("/api/project/audit/saveEvaluation")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isInternalServerError())
			.andDo(print())
			.andReturn();

		Assert.assertTrue(String.valueOf(CODE_PROJECT_NOFOUND).equals(result.getResponse().getHeader(Global.BACKEND_RETURN_CODE)));
	}
	
	@Test
	@WithMockUser
	public void updateEvaluation() throws Exception {
		
		//
		// Update the evaluation of a topic
		//
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2, 60, 0));
	
		this.mvc.perform(post("/api/project/audit/saveEvaluation")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());
		
		testGlobalAuditEvaluation(ID_PROJECT, 68);
	}

	@Test
	@WithMockUser
	public void updateReport() throws Exception {
		
		//
		// Update the evaluation of a topic
		//
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_1, 0, 0, "Test report"));
	
		this.mvc.perform(post("/api/project/audit/saveReport")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
		Project project = projectHandler.get(ID_PROJECT);

		Assert.assertEquals("The report has to be saved.", "Test report", project.getAudit().get(ID_TOPIC_1).getReport());

		
	}

	@Test
	@WithMockUser
	public void updateWeightsNominal() throws Exception {

		//
		// Update the evaluation of a topic
		//
		BodyParamAuditEntries bpae = new BodyParamAuditEntries();
		bpae.setIdProject(ID_PROJECT);
		bpae.setDataEnvelope(new AuditTopic[2]);
		bpae.getDataEnvelope()[0] = new AuditTopic(ID_TOPIC_1, 0, 10);
		bpae.getDataEnvelope()[1] = new AuditTopic(ID_TOPIC_2, 0, 90);
	
		this.mvc.perform(post("/api/project/audit/saveWeights")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpae)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"))
				.andDo(print());
		
		testGlobalAuditEvaluation(ID_PROJECT, 35);
	}
	
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		project.getAudit().clear();
				
	}
	
}
