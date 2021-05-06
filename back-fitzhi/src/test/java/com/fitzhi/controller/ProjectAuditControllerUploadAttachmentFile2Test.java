package com.fitzhi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anySetOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.controller.in.BodyParamAuditEntry;
import com.fitzhi.controller.in.BodyParamProjectAttachmentFile;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.AttachmentFile;
import com.fitzhi.data.internal.AuditTopic;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.StorageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.fitzhi.Error.CODE_PROJECT_TOPIC_UNKNOWN;
/**
 * <p>
 * Test of the method {@link ProjectAuditController#uploadAttachmentFile(org.springframework.web.multipart.MultipartFile, int, int, int, String)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAuditControllerUploadAttachmentFile2Test {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectAuditHandler projectAuditHandler;

	@MockBean
	@Qualifier("Attachment")
	private StorageService storageService;

	@Test
	@WithMockUser
	public void uploadAttachmentFile() throws Exception {
		
		when(projectAuditHandler.getTopic(1805, 1815)).thenReturn(new AuditTopic(1815));
		doNothing().when(projectAuditHandler).updateAttachmentFile(anyInt(), anyInt(), any(AttachmentFile.class));

		this.mvc.perform(MockMvcRequestBuilders.multipart("/api/project/audit/uploadAttachement")
			.file("file", null)
			.param("idProject", "1805")
			.param("idTopic", "1815")
			.param("label", "Testing label for 1805-1815")
			.param("type", FileType.FILE_TYPE_DOC.toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"));

		Mockito.verify(storageService, times(1)).store(any(), any());
		Mockito.verify(projectAuditHandler, times(1)).updateAttachmentFile(anyInt(), anyInt(), any(AttachmentFile.class));
	}
	
	@Test
	@WithMockUser
	public void uploadAttachmentFileKO() throws Exception {
		
		doThrow(new ApplicationException(CODE_PROJECT_TOPIC_UNKNOWN, ""))
			.when(projectAuditHandler)
			.getTopic(1805, 1815);
		doNothing().when(projectAuditHandler).updateAttachmentFile(anyInt(), anyInt(), any(AttachmentFile.class));

		this.mvc.perform(MockMvcRequestBuilders.multipart("/api/project/audit/uploadAttachement")
			.file("file", null)
			.param("idProject", "1805")
			.param("idTopic", "1815")
			.param("label", "Testing label for 1805-1815")
			.param("type", FileType.FILE_TYPE_DOC.toString()))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_PROJECT_TOPIC_UNKNOWN)));
	}

}
