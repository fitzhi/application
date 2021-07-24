package com.fitzhi.controller.projectAnalysis;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
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
 * <p>Class in charge of the test of {@link com.fitzhi.controller.ProjectAnalysisController#lookupDir(int, String)}</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectAnalysisControllerSaveLibDirTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ProjectHandler projectHandler;

	
	@Test
	@WithMockUser	
	public void saveOk() throws Exception {
	
		when(projectHandler.lookup(1789)).thenReturn(new Project(1789, "The revolution"));
		when(projectHandler.saveLibraries(1789, new ArrayList<>()))
			.thenReturn(new ArrayList<Library>());

		List<Library> continents  = new ArrayList<>();	
		String jsonInput = gson.toJson(continents);
		
		this.mvc.perform(post("/api/project/1789/analysis/lib-dir")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(jsonInput))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))	
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).lookup(1789);
		Mockito.verify(projectHandler, times(1)).saveLibraries(1789, new ArrayList<>());
			
	}


	@Test
	@WithMockUser	
	public void saveKO() throws Exception {
	
		when(projectHandler.lookup(1789)).thenThrow(new ApplicationException(7777, "error 7777"));
		
		List<Library> continents  = new ArrayList<>();	
		String jsonInput = gson.toJson(continents);
		
		this.mvc.perform(post("/api/project/1789/analysis/lib-dir")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(jsonInput))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))	
			.andExpect(jsonPath("$.code", is(7777)))
			.andExpect(jsonPath("$.message", is("error 7777")))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).lookup(1789);
			
	}
	
}
