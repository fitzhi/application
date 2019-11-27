package fr.skiller.controller;

import static fr.skiller.Error.CODE_PROJECT_NOFOUND;
import static fr.skiller.Error.CODE_PROJECT_TOPIC_UNKNOWN;
import static fr.skiller.Error.CODE_PROJECT_INVALID_WEIGHTS;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.Global;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyParamAuditEntries;
import fr.skiller.controller.in.BodyParamAuditEntry;
import fr.skiller.controller.util.LocalDateAdapter;
import fr.skiller.data.internal.AuditTopic;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;
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
	public void before() throws SkillerException {
		project = projectHandler.get(ID_PROJECT);

		Map<Integer, AuditTopic> mapAudit = new HashMap<>();
		mapAudit.put(ID_TOPIC_2, new AuditTopic(ID_TOPIC_2));
		project.setAudit(mapAudit);
	}
	
	@Test
	@WithMockUser
	public void addTopic() throws Exception {
		
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_1));
	
		this.mvc.perform(post("/project/audit/saveTopic")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());

		MvcResult result = this.mvc.perform(get("/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_1))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		// The topic has been successfully added
		Assert.assertTrue(auditProject.getIdTopic() == ID_TOPIC_1);

		Assert.assertEquals("5 is the default weight", 5, auditProject.getWeight());

	}

	
	@Test
	@WithMockUser
	public void addExistingTopic() throws Exception {
		
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(ID_PROJECT);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2));
	
		this.mvc.perform(post("/project/audit/saveTopic")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();

		//TODO THE ACTUAL TOPIC, AND ITS UNDERLINING DATA, HAS NOT BEEN REMOVED BY THIS CALL
		// THIS TEST HAS TO BE COMPLETED.
		
	}
	
	@Test
	@WithMockUser
	public void addTopicUnknownProject() throws Exception {
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(666);
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2));
	
		MvcResult result = this.mvc.perform(post("/project/audit/saveTopic")
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
		
		MvcResult result = this.mvc.perform(get("/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
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
	
		this.mvc.perform(post("/project/audit/removeTopic")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());
			
		//
		// Testing the fact that the topic has been effectively removed
		//
		result = this.mvc.perform(get("/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
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
		bpae.setAuditTopic(new AuditTopic(ID_TOPIC_2, 60, 10));
	
		this.mvc.perform(post("/project/audit/saveEvaluation")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bpae)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());
			
		//
		// Testing the fact that the topic has been effectively removed
		//
		MvcResult result = this.mvc.perform(get("/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();

		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		
		Assert.assertEquals("Evaluation has been saved", 60, auditProject.getEvaluation());
		
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
		MvcResult result = this.mvc.perform(post("/project/audit/saveWeights")
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

		project.getAudit().put(ID_TOPIC_1, new AuditTopic(ID_TOPIC_1));
		// From that point, the project will have 2 topics declared
		
		//
		// Update the evaluation of a topic
		//
		BodyParamAuditEntries bpae = new BodyParamAuditEntries();
		bpae.setIdProject(ID_PROJECT);
		bpae.setDataEnvelope(new AuditTopic[2]);
		bpae.getDataEnvelope()[0] = new AuditTopic(ID_TOPIC_1, 60, 40);
		bpae.getDataEnvelope()[1] = new AuditTopic(ID_TOPIC_2, 60, 60);
	
		this.mvc.perform(post("/project/audit/saveWeights")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpae)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"))
				.andDo(print());
		
		MvcResult result = this.mvc.perform(get("/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_1))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		AuditTopic auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		Assert.assertEquals("Weight has been saved", 40, auditProject.getWeight());
		
		result = this.mvc.perform(get("/project/audit/loadTopic/"+ ID_PROJECT + "/" + ID_TOPIC_2))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		auditProject = gson.fromJson(result.getResponse().getContentAsString(), AuditTopic.class);
		Assert.assertEquals("Weight has been saved", 60, auditProject.getWeight());
				
	}
	
	
	@After
	public void after() throws SkillerException {
		project = projectHandler.get(ID_PROJECT);
		project.getAudit().clear();
				
	}
	
}
