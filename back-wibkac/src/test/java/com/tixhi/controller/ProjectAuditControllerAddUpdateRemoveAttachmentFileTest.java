package com.tixhi.controller;

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
import com.tixhi.bean.ProjectAuditHandler;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.controller.ProjectAuditController;
import com.tixhi.controller.in.BodyParamProjectAttachmentFile;
import com.tixhi.controller.util.LocalDateAdapter;
import com.tixhi.data.internal.AttachmentFile;
import com.tixhi.data.internal.AuditTopic;
import com.tixhi.data.internal.Project;
import com.tixhi.exception.SkillerException;
import com.tixhi.service.FileType;
/**
 * <p>
 * Test of the class {@link ProjectAuditController#saveAttachmentFile(com.tixhi.controller.in.BodyParamProjectAttachmentFile)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAuditControllerAddUpdateRemoveAttachmentFileTest {


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
	private ProjectAuditHandler projectAuditHandler;
	
	final int ID_PROJECT = 1789;

	final int ID_TOPIC_1 = 1;
	final int ID_TOPIC_2 = 2; 
	
	@Before
	public void before() throws SkillerException {
		
		Project project = new Project(ID_PROJECT, "Revolutionary days");
		
		projectHandler.addNewProject(project);

		Map<Integer, AuditTopic> mapAudit = new HashMap<>();
		mapAudit.put(ID_TOPIC_1, new AuditTopic(ID_TOPIC_1, 80, 40));
		mapAudit.put(ID_TOPIC_2, new AuditTopic(ID_TOPIC_2, 30, 60));
		project.setAudit(mapAudit);
	}
	
	/**
	 * Load the project for testing purpose
	 * @param idProject given project
	 * @return the project retrieved from the controller.
	 * @throws Exception
	 */
	Project getProject(int idProject) throws Exception {
		
		MvcResult result = this.mvc.perform(get("/api/project/id/"+ ID_PROJECT))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		Project project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		return project;
	}

	
	@Test
	@WithMockUser
	public void processAuditProjectSaveFirstAttachmentFile() throws Exception {
		
		//
		// Add a new attachment file.
		//
		BodyParamProjectAttachmentFile bppaf = new BodyParamProjectAttachmentFile();
		bppaf.setIdProject(ID_PROJECT);
		bppaf.setIdTopic(ID_TOPIC_1);
		bppaf.setAttachmentFile(new AttachmentFile(0, "given fileName", FileType.valueOf(3), "given fileLabel"));
	
		this.mvc.perform(post("/api/project/audit/saveAttachmentFile")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bppaf)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();

		Project project = this.getProject(ID_PROJECT);
		Assert.assertEquals(1, project.getAudit().get(ID_TOPIC_1).getAttachmentList().size());
		Assert.assertEquals("given fileName", project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getFileName());
		Assert.assertEquals("given fileLabel", project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getLabel());
		Assert.assertEquals(3, project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getTypeOfFile().getValue());
	}
	
	@Test
	@WithMockUser
	public void processAuditProjectSaveMiddleAttachmentFile() throws Exception {
		
		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, new AttachmentFile(1, "stupidFileName.doc", FileType.valueOf(0), "stupid label"));		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, new AttachmentFile(2, "lastFileName.doc", FileType.valueOf(0), "last label"));		

		//
		// Update an attachment in the middle of the list
		//
		BodyParamProjectAttachmentFile bppaf = new BodyParamProjectAttachmentFile();
		bppaf.setIdProject(ID_PROJECT);
		bppaf.setIdTopic(ID_TOPIC_1);
		bppaf.setAttachmentFile(new AttachmentFile(1, "testingFileName", FileType.valueOf(3), "testingFileLabel"));
	
		this.mvc.perform(post("/api/project/audit/saveAttachmentFile")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bppaf)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();

		Project project = this.getProject(ID_PROJECT);
		Assert.assertEquals(3, project.getAudit().get(ID_TOPIC_1).getAttachmentList().size());
		Assert.assertEquals("testingFileName", project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getFileName());
		Assert.assertEquals("testingFileLabel", project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getLabel());
		Assert.assertEquals(3, project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getTypeOfFile().getValue());
	}
	
	@Test
	@WithMockUser
	public void processAuditProjectRemoveAttachmentFile() throws Exception {
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, new AttachmentFile(1, "stupidFileName.doc", FileType.valueOf(0), "stupid label"));		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, new AttachmentFile(2, "lastFileName.doc", FileType.valueOf(0), "last label"));		

		//
		// remove an attachment in the middle of the list
		//
		BodyParamProjectAttachmentFile bppaf = new BodyParamProjectAttachmentFile();
		bppaf.setIdProject(ID_PROJECT);
		bppaf.setIdTopic(ID_TOPIC_1);
		bppaf.setAttachmentFile(new AttachmentFile(1, "tobeRemoved", FileType.valueOf(1), "toBeRemoved"));
	
		this.mvc.perform(post("/api/project/audit/removeAttachmentFile")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bppaf)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();

		Project project = this.getProject(ID_PROJECT);
		Assert.assertEquals(2, project.getAudit().get(ID_TOPIC_1).getAttachmentList().size());
	
	}
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(ID_PROJECT);
	}
}
