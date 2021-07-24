package com.fitzhi.controller.projectAudit;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.controller.ProjectAuditController;

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

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectAuditHandler projectAuditHandler;

	@Test
	@WithMockUser
	public void removeTopic() throws Exception {
		
		this.mvc.perform(delete("/api/project/1805/audit/topic/1815")
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"));

		Mockito.verify(projectAuditHandler, times(1)).removeTopic(1805, 1815, false);
		Mockito.verify(projectAuditHandler, times(1)).processAndSaveGlobalAuditEvaluation(1805);
		
	}
	

}
