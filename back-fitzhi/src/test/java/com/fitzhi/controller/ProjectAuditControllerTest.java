package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_PROJECT_INVALID_WEIGHTS;
import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static com.fitzhi.Error.CODE_PROJECT_TOPIC_UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
public class ProjectAuditControllerTest {


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
	final int ID_TOPIC_2 = 2; // We add this topic in the testing project
	
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		project.setAuditEvaluation(30);
		Map<Integer, AuditTopic> mapAudit = new HashMap<>();
		AuditTopic at = new AuditTopic(ID_TOPIC_2, 30, 100);
		mapAudit.put(ID_TOPIC_2, at);
		project.setAudit(mapAudit);
	}
	
	/**
	 * Test the value of the evaluation.
	 * @param idProject given project
	 * @param expectedValue expected value.
	 * @throws Exception
	 */
	void testGlobalAuditEvaluation(int idProject, int expectedValue) throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/id/"+ ID_PROJECT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		Project project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		
		Assert.assertEquals(expectedValue + " is the default weight", expectedValue, project.getAuditEvaluation());
	}
	
	@Test
	@WithMockUser
	public void addTopic() throws Exception {
		
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_1));
	
		this.mvc.perform(post("/api/project/audit/saveTopic")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());

		MvcResult result = this.mvc.perform(get("/api/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_1))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		// The topic has been successfully added
		Assert.assertTrue(auditProject.getIdTopic() == ID_TOPIC_1);

		Assert.assertEquals("The weight should beshared between 2 topics", 50, auditProject.getWeight());
		Assert.assertEquals("Evaluation 0 for the new topic", 0, auditProject.getEvaluation());

		testGlobalAuditEvaluation(ID_PROJECT, 15);
	}

	
	@Test
	@WithMockUser
	public void addExistingTopic() throws Exception {
		
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2));
	
		this.mvc.perform(post("/api/project/audit/saveTopic")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();

		Project project = projectHandler.get(ID_PROJECT);
		assertEquals("Only one topic", project.getAudit().size(), 1);
		assertNotNull(project.getAudit().get(ID_TOPIC_2));
		// We do not change the original data
		AuditTopic at = project.getAudit().get(ID_TOPIC_2);
		Assert.assertEquals("Same evaluation", 30, at.getEvaluation());
		Assert.assertEquals("Same weight", 100, at.getWeight());
		
		// The actual topic and its underlying data have not been overridden by this call 
		// We keep the same global evaluation.
		testGlobalAuditEvaluation(ID_PROJECT, 30);
	}
	
	@Test
	@WithMockUser
	public void addTopicUnknownProject() throws Exception {
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(666);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2));
	
		MvcResult result = this.mvc.perform(post("/api/project/audit/saveTopic")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpae)))
				.andExpect(status().isInternalServerError())
				.andDo(print())
				.andReturn();
		
		Assert.assertTrue(String.valueOf(CODE_PROJECT_NOFOUND).equals(result.getResponse().getHeader(Global.BACKEND_RETURN_CODE)));
	}
	
	@Test
	@WithMockUser
	public void removeTopic() throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		// The topic has been successfully added
		Assert.assertTrue(auditProject.getIdTopic() == ID_TOPIC_2);
		

		//
		// Removing the topic
		//
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2));
	
		this.mvc.perform(post("/api/project/audit/removeTopic")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());
			
		//
		// Testing the fact that the topic has been effectively removed
		//
		result = this.mvc.perform(get("/api/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
				.andExpect(status().isInternalServerError())
				.andDo(print())
				.andReturn();
		
		Assert.assertTrue(String.valueOf(CODE_PROJECT_TOPIC_UNKNOWN).equals(result.getResponse().getHeader(Global.BACKEND_RETURN_CODE)));
		
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
			
		//
		// Testing the fact that the topic has been effectively removed
		//
		MvcResult result = this.mvc.perform(get("/api/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();

		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		
		Assert.assertEquals("Evaluation has been saved", 60, auditProject.getEvaluation());

		// Global evaluation is also equal to 60 as well because there is only one topic in this audit.
		testGlobalAuditEvaluation(ID_PROJECT, 60);		
	}

	/**
	 * TEST : The SUM OF THE GIVEN WEIGHT HAS TO BE EQUAL TO 100!!
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void updateWeightsHasToBeEqualTo100() throws Exception {

		project.getAudit().put(ID_TOPIC_1, new AuditTopic(ID_TOPIC_1));
		// From that point, the project will have 2 topics declared
		
		//
		// Update the evaluation of a topic
		//
		BodyParamAuditEntries bpae = new BodyParamAuditEntries();
		bpae.setIdProject(ID_PROJECT);
		bpae.setDataEnvelope(new AuditTopic[2]);
		bpae.getDataEnvelope()[0] = new AuditTopic(ID_TOPIC_1, 60, 10);
		bpae.getDataEnvelope()[1] = new AuditTopic(ID_TOPIC_2, 60, 50);
	
		//
		// Cannot save a project with a sum of audit topics weights different to 100.
		//
		MvcResult result = this.mvc.perform(post("/api/project/audit/saveWeights")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpae)))
				.andExpect(status().isInternalServerError())
				.andDo(print())
				.andReturn();
		
		Assert.assertTrue(String.valueOf(CODE_PROJECT_INVALID_WEIGHTS)
				.equals(result.getResponse().getHeader(Global.BACKEND_RETURN_CODE)));
			
		
	}

	@Test
	@WithMockUser
	public void updateWeightsNominal() throws Exception {

		
		project.getAudit().put(ID_TOPIC_1, new AuditTopic(ID_TOPIC_1, 80, 0));
		// From that point, the project will have 2 topics declared
		
		//
		// Update the evaluation of a topic
		//
		BodyParamAuditEntries bpae = new BodyParamAuditEntries();
		bpae.setIdProject(ID_PROJECT);
		bpae.setDataEnvelope(new AuditTopic[2]);
		bpae.getDataEnvelope()[0] = new AuditTopic(ID_TOPIC_1, 0, 40);
		bpae.getDataEnvelope()[1] = new AuditTopic(ID_TOPIC_2, 0, 60);
	
		this.mvc.perform(post("/api/project/audit/saveWeights")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpae)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"))
				.andDo(print());
		
		MvcResult result = this.mvc.perform(get("/api/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_1))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		Assert.assertEquals("Weight has been saved", 40, auditProject.getWeight());
		
		result = this.mvc.perform(get("/api/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		Assert.assertEquals("Weight has been saved", 60, auditProject.getWeight());
	
		//
		// 2 Topics :
		// The 1st : evaluation 30 for a weight of 60
		// The 2nd : evaluation 80 for a weight of 40
		//
		// We expect a global evaluation of 60
		//
		this.testGlobalAuditEvaluation(ID_PROJECT, 50);
		
	}
	
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		project.getAudit().clear();
				
	}
	
}
