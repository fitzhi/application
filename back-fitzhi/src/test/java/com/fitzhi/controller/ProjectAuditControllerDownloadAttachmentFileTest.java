package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.impl.storageservice.AuditAttachmentStorageProperties;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jdk.internal.jline.internal.Log;
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
public class ProjectAuditControllerDownloadAttachmentFileTest {

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
	AuditAttachmentStorageProperties storageProperties;
	
	private final static String UPLOAD_PATHNAME_PDF = "/auditAttachments/audit.pdf";
	
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
	
	@Test
	@WithMockUser
	public void testDownloadAttachment() throws Exception {
		
		uploadFile(UPLOAD_PATHNAME_PDF, FileType.FILE_TYPE_PDF.getValue());
	
		MvcResult result = this.mvc.perform(get(
			String.format("/api/project/audit/attachmentFile/%d/%d/%d", ID_PROJECT, ID_TOPIC_1, 0)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/pdf"))
				.andDo(print())
				.andReturn();
		
		Assert.assertTrue(result.getResponse().getOutputStream().isReady());	
	}

	
	
	@After
	public void after() throws SkillerException {
		Project project = projectHandler.get(ID_PROJECT);
		project.getAudit().clear();
		
		File attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.docx", ID_PROJECT, ID_TOPIC_1));
		if (attachment.exists()) {
			if (!attachment.delete()) {
				log.error(String.format("Cannot delete %", attachment.getAbsolutePath()));
			}
		}
		attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.pdf", ID_PROJECT, ID_TOPIC_1));
		if (attachment.exists()) {
			if (!attachment.delete()) {
				log.error(String.format("Cannot delete %", attachment.getAbsolutePath()));
			}
		}
	}
	
}
