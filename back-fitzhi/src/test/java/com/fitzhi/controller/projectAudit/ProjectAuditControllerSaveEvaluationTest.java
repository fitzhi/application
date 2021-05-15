package com.fitzhi.controller.projectAudit;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.controller.ProjectAuditController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.exception.ApplicationException;
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
 * Test of the method {@link ProjectAuditController#saveEvaluation(int, int, int)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAuditControllerSaveEvaluationTest {


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
	public void saveEvaluation() throws Exception {
		
		doNothing().when(projectAuditHandler).saveEvaluation(1805, 1815, 8);
		doNothing().when(projectAuditHandler).processAndSaveGlobalAuditEvaluation(1805);

		this.mvc.perform(put("/api/project/1805/audit/1815/evaluation/8")
				.contentType(MediaType.APPLICATION_JSON_UTF8))

				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"));

		Mockito.verify(projectAuditHandler, times(1)).saveEvaluation(1805, 1815, 8);
		Mockito.verify(projectAuditHandler, times(1)).processAndSaveGlobalAuditEvaluation(1805);
		
	}
	
	@Test
	@WithMockUser
	public void saveEvaluationKO() throws Exception {
	
		doThrow(new ApplicationException(CODE_PROJECT_NOFOUND, ""))
			.when(projectAuditHandler)
			.saveEvaluation(anyInt(), anyInt(), anyInt());
		
		this.mvc.perform(put("/api/project/1805/audit/1815/evaluation/5")
				.contentType(MediaType.APPLICATION_JSON_UTF8))

				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)));

	}

}
