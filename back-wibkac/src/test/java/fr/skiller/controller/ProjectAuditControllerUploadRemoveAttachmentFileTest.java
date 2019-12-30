package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.controller.in.BodyParamProjectAttachmentFile;
import fr.skiller.controller.util.LocalDateAdapter;
import fr.skiller.data.internal.AttachmentFile;
import fr.skiller.data.internal.AuditTopic;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.FileType;
import fr.skiller.service.impl.storageservice.AuditAttachmentStorageProperties;
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
public class ProjectAuditControllerUploadRemoveAttachmentFileTest {

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
	
	private final static String UPLOAD_PATHNAME_DOCX = "/auditAttachments/audit.docx";

	private final static String UPLOAD_FILENAME_DOCX = "audit.docx";

	private final static String UPLOAD_PATHNAME_PDF = "/auditAttachments/audit.pdf";
	
	private final static String UPLOAD_FILENAME_PDF = "audit.pdf";
	
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
	
	private void removeFirstAttachmentFile(String filename) throws Exception {

		BodyParamProjectAttachmentFile bppaf = new BodyParamProjectAttachmentFile();
		bppaf.setIdProject(ID_PROJECT);
		bppaf.setIdTopic(ID_TOPIC_1);
		bppaf.setAttachmentFile(new AttachmentFile(0, filename, FileType.FILE_TYPE_DOCX, "file to be removed"));
	
		this.mvc.perform(post("/api/project/audit/removeAttachmentFile")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bppaf)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
	}
	
	@Test
	@WithMockUser
	public void testAddFirstAttachment() throws Exception {
		
		uploadFile(UPLOAD_PATHNAME_DOCX, FileType.FILE_TYPE_DOCX.getValue());
		
		//
		// The file is correctly uploaded
		//
		File attachment = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.docx", ID_PROJECT, ID_TOPIC_1));
		Assert.assertTrue(attachment.exists());

		removeFirstAttachmentFile(UPLOAD_FILENAME_DOCX);
		Assert.assertFalse(attachment.exists());
	}
	
	@Test
	@WithMockUser
	public void testAddSecondAttachment() throws Exception {
		
		uploadFile(UPLOAD_PATHNAME_DOCX, FileType.FILE_TYPE_DOCX.getValue());
		uploadFile(UPLOAD_PATHNAME_PDF, FileType.FILE_TYPE_PDF.getValue());

		//
		// The file is correctly uploaded
		//
		File attachmentDOCX = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.docx", ID_PROJECT, ID_TOPIC_1));
		File attachmentPDF = new File (storageProperties.getLocation() + 
				String.format("/%d-%d-audit.pdf", ID_PROJECT, ID_TOPIC_1));
		Assert.assertTrue(attachmentDOCX.exists());
		Assert.assertTrue(attachmentPDF.exists());
	
		removeFirstAttachmentFile(UPLOAD_FILENAME_DOCX);
		Assert.assertFalse(attachmentDOCX.exists());
		Assert.assertTrue(attachmentPDF.exists());
		
		removeFirstAttachmentFile(UPLOAD_FILENAME_PDF);
		Assert.assertFalse(attachmentDOCX.exists());
		Assert.assertFalse(attachmentPDF.exists());
		
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
