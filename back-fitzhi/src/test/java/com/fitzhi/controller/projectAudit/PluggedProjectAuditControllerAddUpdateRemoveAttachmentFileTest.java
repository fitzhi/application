package com.fitzhi.controller.projectAudit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectAuditController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.AttachmentFile;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;
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
 * Test of the class {@link ProjectAuditController#saveAttachmentFile(com.fitzhi.controller.in.BodyParamProjectAttachmentFile)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectAuditControllerAddUpdateRemoveAttachmentFileTest {

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
	public void before() throws ApplicationException {
		
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
		
		MvcResult result = this.mvc.perform(get("/api/project/"+ ID_PROJECT))
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
		AttachmentFile af = new AttachmentFile(0, "given fileName", FileType.valueOf(3), "given fileLabel");
	
		this.mvc.perform(put("/api/project/1789/audit/1/attachmentFile")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(af)))
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
		AttachmentFile af = new AttachmentFile(1, "testingFileName", FileType.valueOf(3), "testingFileLabel");
	
		this.mvc.perform(put("/api/project/1789/audit/1/attachmentFile")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(af)))
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
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, 
			new AttachmentFile(0, "theFileName", FileType.valueOf(0), "theLabel"));		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, 
			new AttachmentFile(1, "stupidFileName.doc", FileType.valueOf(0), "stupid label"));		
		projectAuditHandler.updateAttachmentFile(ID_PROJECT, ID_TOPIC_1, 
			new AttachmentFile(2, "lastFileName.doc", FileType.valueOf(0), "last label"));		

		this.mvc.perform(delete("/api/project/1789/audit/1/attachmentFile/1")
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();

		Project project = this.getProject(ID_PROJECT);
		Assert.assertEquals(2, project.getAudit().get(ID_TOPIC_1).getAttachmentList().size());
	
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(ID_PROJECT);
	}
}
