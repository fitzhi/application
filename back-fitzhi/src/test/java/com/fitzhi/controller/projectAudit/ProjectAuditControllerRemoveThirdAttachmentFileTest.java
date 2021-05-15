package com.fitzhi.controller.projectAudit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectAuditController;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.impl.storageservice.AuditAttachmentStorageProperties;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProjectAuditControllerRemoveThirdAttachmentFileTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	MockMvc mvc;
	
	@Autowired
	private ProjectHandler projectHandler;

	@Autowired
	AuditAttachmentStorageProperties storageProperties;
	

	private final static String UPLOAD_PATHNAME_PDF_0 = "/auditAttachments/audit-0.pdf";
	private final static String UPLOAD_PATHNAME_PDF_1 = "/auditAttachments/audit-1.pdf";
	private final static String UPLOAD_PATHNAME_PDF_2 = "/auditAttachments/audit-2.pdf";
	private final static String UPLOAD_PATHNAME_PDF_3 = "/auditAttachments/audit-3.pdf";
	
	private final static String UPLOAD_FILENAME_PDF_0 = "audit-0.pdf";
	private final static String UPLOAD_FILENAME_PDF_1 = "audit-1.pdf";
	private final static String UPLOAD_FILENAME_PDF_2 = "audit-2.pdf";
	private final static String UPLOAD_FILENAME_PDF_3 = "audit-3.pdf";
	
	private final int ID_PROJECT = 1;

	private final int ID_TOPIC_1 = 1;
	private final int ID_TOPIC_2 = 2; 
	
	@Before
	public void before() throws ApplicationException {
		Project project = projectHandler.get(ID_PROJECT);
		Map<Integer, AuditTopic> mapAudit = new HashMap<>();
		AuditTopic at = new AuditTopic(ID_TOPIC_1, 30, 100);
		mapAudit.put(ID_TOPIC_1, at);
		at = new AuditTopic(ID_TOPIC_2, 30, 100);
		mapAudit.put(ID_TOPIC_2, at);
		project.setAudit(mapAudit);
		
	}
	
	private void uploadFile(String filename, int fileType) {
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

	private void removeFirstAttachmentFile(int idFile) throws Exception {

		this.mvc.perform(delete("/api/project/1/audit/1/attachmentFile/" + idFile)
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
	}
	
	@Test
	@WithMockUser
	public void testRemoveThirdAttachment() throws Exception {
		
		uploadFile(UPLOAD_PATHNAME_PDF_0, FileType.FILE_TYPE_PDF.getValue());
		uploadFile(UPLOAD_PATHNAME_PDF_1, FileType.FILE_TYPE_PDF.getValue());
		uploadFile(UPLOAD_PATHNAME_PDF_2, FileType.FILE_TYPE_PDF.getValue());
		uploadFile(UPLOAD_PATHNAME_PDF_3, FileType.FILE_TYPE_PDF.getValue());
		
		Project project = projectHandler.get(ID_PROJECT);
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_0,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getFileName());
		Assert.assertEquals(
				0,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getIdFile());
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_1,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getFileName());
		Assert.assertEquals(
				1,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getIdFile());
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_2,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(2).getFileName());
		Assert.assertEquals(
				2,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(2).getIdFile());
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_3,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(3).getFileName());
		Assert.assertEquals(
				3,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(3).getIdFile());
		
		
		removeFirstAttachmentFile(2);
		project = projectHandler.get(ID_PROJECT);
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_0,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getFileName());
		Assert.assertEquals(
				0,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(0).getIdFile());
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_1,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getFileName());
		Assert.assertEquals(
				1,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(1).getIdFile());
		Assert.assertEquals(
				UPLOAD_FILENAME_PDF_3,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(2).getFileName());
		Assert.assertEquals(
				2,
				project.getAudit().get(ID_TOPIC_1).getAttachmentList().get(2).getIdFile());
	}
	
	@After
	public void after() throws Exception {
		File dir = new File(storageProperties.getLocation());
		for (File file : dir.listFiles()) {
			if (!file.delete()) {
				log.error(String.format("Cannot delete %", file.getAbsolutePath()));
			}
		}
	}
	
}
