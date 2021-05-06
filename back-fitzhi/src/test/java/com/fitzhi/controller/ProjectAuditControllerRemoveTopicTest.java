package com.fitzhi.controller;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.controller.in.BodyParamAuditEntry;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.AuditTopic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
/**
 * <p>
 * Test of the method {@link ProjectAuditController#removeTopic(BodyParamAuditEntry)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAuditControllerRemoveTopicTest {


	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectAuditHandler projectAuditHandler;

	@Test
	@WithMockUser
	public void removeTopic() throws Exception {
		
		//
		// Removing the topic
		//
		BodyParamAuditEntry bpae = new BodyParamAuditEntry();
		bpae.setIdProject(1805);
		bpae.setAuditTopic(new AuditTopic(1815));
	
		this.mvc.perform(post("/api/project/audit/removeTopic")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpae)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"));

		Mockito.verify(projectAuditHandler, times(1)).removeTopic(1805, 1815, false);
		Mockito.verify(projectAuditHandler, times(1)).processAndSaveGlobalAuditEvaluation(1805);
		
	}
	

}
