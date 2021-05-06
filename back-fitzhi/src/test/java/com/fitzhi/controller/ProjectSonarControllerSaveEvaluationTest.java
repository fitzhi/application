package com.fitzhi.controller;

import static com.fitzhi.Error.CODE_PROJECT_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamProjectSonarEvaluation;
import com.fitzhi.controller.in.BodyParamSonarEntry;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarEvaluation;
import com.fitzhi.data.internal.SonarProject;
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
 * Test of the method {@link ProjectSonarController#saveEvaluation(com.fitzhi.controller.in.BodyParamProjectSonarEvaluation)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSonarControllerSaveEvaluationTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@Test
	@WithMockUser
	public void saveEvaluation() throws Exception {
		
		when(projectHandler.get(1805)).thenReturn(new Project(1805, "Testing project"));
	
		this.mvc.perform(post("/api/project/sonar/saveEvaluation")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpse())))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().string("true"));

		Mockito.verify(projectHandler, times(1)).saveSonarEvaluation(
				new Project(1805, "Testing project"), 
				"key-sonar", 
				new SonarEvaluation());
		Mockito.verify(projectHandler, times(1)).get(1805);
	}

	@Test
	@WithMockUser
	public void saveEvaluationKO() throws Exception {
		
		doThrow(new ApplicationException(CODE_PROJECT_NOFOUND, "Project 1805 not found"))
			.when(projectHandler)
			.get(anyInt());

		this.mvc.perform(post("/api/project/sonar/saveEntry")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpse())))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.message", is("Project 1805 not found")))
				.andExpect(jsonPath("$.code", is(CODE_PROJECT_NOFOUND)));

	}

	private BodyParamProjectSonarEvaluation bpse() {
		BodyParamProjectSonarEvaluation bpse = new BodyParamProjectSonarEvaluation();
		bpse.setIdProject(1805);
		bpse.setSonarKey("key-sonar");
		bpse.setSonarEvaluation(new SonarEvaluation());
		return bpse;
	}
}
