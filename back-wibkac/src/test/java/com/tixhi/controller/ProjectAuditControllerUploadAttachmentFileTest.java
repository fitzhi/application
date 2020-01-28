package com.tixhi.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.controller.ProjectAuditController;
import com.tixhi.controller.util.LocalDateAdapter;
import com.tixhi.data.internal.AttachmentFile;
import com.tixhi.data.internal.AuditTopic;
import com.tixhi.data.internal.Project;
import com.tixhi.exception.SkillerException;
import com.tixhi.service.FileType;
import com.tixhi.service.impl.storageservice.AuditAttachmentStorageProperties;
/**
 * <p>
 * Test of the class {@link ProjectAuditController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProjectAuditControllerUploadAttachmentFileTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;
	
	@Autowired
	private ProjectAuditController projectAuditController;

	@Autowired
	AuditAttachmentStorageProperties storageProperties;
	
	private final String UPLOAD_FILENAME_DOCX = "/auditAttachments/audit.docx";
	
	private final String UPLOAD_FILENAME_PDF = "/auditAttachments/audit.pdf";
	
	private final int ID_PROJECT = 1;

	private final int ID_TOPIC_1 = 1;
	private final int ID_TOPIC_2 = 2; 
	
	@Before
	public void before() throws SkillerException {
		Project project = projectHandler.get(ID_PROJECT);
		Map<Integer, AuditTopic> mapAudit = new HashMap<>();
		AuditTopic at = new AuditTopic(ID_TOPIC_1, 30, 100);
		mapAudit.put(ID_TOPIC_1, at);
		at = new AuditTopic(ID_TOPIC_2, 30, 100);
		mapAudit.put(ID_TOPIC_2, at);
		project.setAudit(mapAudit);
		
	}
	
	@Test
	@WithMockUser
	public void testAddFirstAttachment() throws Exception {
		
		uploadfile(UPLOAD_FILENAME_DOCX, FileType.FILE_TYPE_DOCX.getValue());
		
		//
		// The file is correctly uploaded
		//
		File attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.docx", ID_PROJECT, ID_TOPIC_1));
		Assert.assertTrue(attachment.exists());
		Assert.assertTrue(attachment.delete());
		
		//
		// The attachment is well recorded in the project audit
		//
		Project project = projectHandler.get(ID_PROJECT);
		List<AttachmentFile> attachments = project.getAudit().get(ID_TOPIC_1).getAttachmentList();
		Assert.assertEquals(1, attachments.size());
		Assert.assertEquals("audit.docx", attachments.get(0).getFileName());
		Assert.assertEquals(FileType.FILE_TYPE_DOCX.getValue(), attachments.get(0).getTypeOfFile().getValue());
	}

	private void uploadfile(String filename, int fileType) {
		ClassPathResource resource = new ClassPathResource(filename);
		HttpHeaders headers = new HttpHeaders();

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", resource);
		map.add("idProject", ID_PROJECT);
		map.add("idTopic", ID_TOPIC_1);
		map.add("label", String.format("testing label for %s", filename));
		map.add("type", fileType);
		
		ResponseEntity<Boolean> response = this.restTemplate
			.exchange(
				"/api/project/audit/uploadAttachement", 
				HttpMethod.POST, 
				new HttpEntity<>(map, headers), 
				Boolean.class);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
	}
	
	@Test
	@WithMockUser
	public void testAddSecondAttachment() throws Exception {
		
		uploadfile(UPLOAD_FILENAME_DOCX, FileType.FILE_TYPE_DOCX.getValue());
		uploadfile(UPLOAD_FILENAME_PDF, FileType.FILE_TYPE_PDF.getValue());

		//
		// The file is correctly uploaded
		//
		File attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.pdf", ID_PROJECT, ID_TOPIC_1));
		Assert.assertTrue(attachment.exists());
		Assert.assertTrue(attachment.delete());
		
		//
		// The attachment is well recorded in the project audit
		//
		Project project = projectHandler.get(ID_PROJECT);
		List<AttachmentFile> attachments = project.getAudit().get(ID_TOPIC_1).getAttachmentList();
		Assert.assertEquals(2, attachments.size());
		Assert.assertEquals("audit.docx", attachments.get(0).getFileName());
		Assert.assertEquals("audit.pdf", attachments.get(1).getFileName());
		Assert.assertEquals(FileType.FILE_TYPE_DOCX.getValue(), attachments.get(0).getTypeOfFile().getValue());
		Assert.assertEquals(FileType.FILE_TYPE_PDF.getValue(), attachments.get(1).getTypeOfFile().getValue());
	}
	
	@After
	public void after() throws SkillerException {
		Project project = projectHandler.get(ID_PROJECT);
		project.getAudit().clear();

		
		File attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.docx", ID_PROJECT, ID_TOPIC_1));
		if (attachment.exists()) {
			attachment.delete();
		}
		attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.pdf", ID_PROJECT, ID_TOPIC_1));
		if (attachment.exists()) {
			attachment.delete();		
		}
	}
	
}
