package com.fitzhi.controller.projectAnalysis;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
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
public class ProjectAnalysisControllerLookupLibDirTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ProjectHandler projectHandler;

	@MockBean
	ProjectDashboardCustomizer dashboardCustomizer;

	@Test
	@WithMockUser	
	public void simpleLookup() throws Exception {
	
		when(projectHandler.getProject(1789)).thenReturn(new Project(1789, "P 1789"));
		when(dashboardCustomizer.lookupPathRepository(new Project(1789, "P 1789"), "myCrit"))
			.thenReturn(new ArrayList<String>());

		this.mvc.perform(get("/api/project/1789/analysis/lib-dir?path=myCrit")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).getProject(1789);
		Mockito.verify(dashboardCustomizer, times(1))
			.lookupPathRepository(new Project(1789, "P 1789"), "myCrit");
			
	}

	@Test
	@WithMockUser	
	public void lookupWithPath() throws Exception {
	
		when(projectHandler.getProject(1789)).thenReturn(new Project(1789, "P 1789"));
		when(dashboardCustomizer.lookupPathRepository(new Project(1789, "P 1789"), "myRep/myCrit"))
			.thenReturn(new ArrayList<String>());

		this.mvc.perform(get("/api/project/1789/analysis/lib-dir?path=myRep/myCrit")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).getProject(1789);
		Mockito.verify(dashboardCustomizer, times(1))
			.lookupPathRepository(new Project(1789, "P 1789"), "myRep/myCrit");
			
	}
	
}
