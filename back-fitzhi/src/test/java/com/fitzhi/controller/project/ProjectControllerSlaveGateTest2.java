package com.fitzhi.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.ProjectController;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.Project;
import com.fitzhi.source.crawler.RepoScanner;
import static com.fitzhi.service.ConnectionSettingsType.PUBLIC_LOGIN;

/**
 * <p>
 * Test of the method {@link ProjectController#slaveGate}.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"autoProjectCreation=true" })
public class ProjectControllerSlaveGateTest2 {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProjectHandler projectHandler;

	@MockBean
	private StaffHandler staffHandler;

	@MockBean
	private DataHandler dataHandler;

	@MockBean
	RepoScanner scanner;

	/**
	 * Test project not found when autoProjectCreation is FALSE.
	 * @throws Exception
	 */
	@Test
	public void unknownProjectShouldBeCreated() throws Exception {

		when(dataHandler.isLocal()).thenReturn(false);
		when(projectHandler.lookup("myProject")).thenReturn(Optional.empty());
		doNothing().when(dataHandler).saveProject(any(Project.class));

		SettingsGeneration settings = new SettingsGeneration();
		settings.setUrlRepository("https://myUrlRepository/myProject");

		this.mvc.perform(put("/api/project/analysis")
			.content(objectMapper.writeValueAsString(settings))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isNoContent())

			.andDo(print())
			.andReturn();
		
		Project project = new Project(-1, "myProject");
		project.setUrlRepository("https://myUrlRepository/myProject");
		project.setAuditEvaluation(-1);
		// By default, "auto-creation" actually only works with public repositories.
		project.setConnectionSettings(PUBLIC_LOGIN);
		verify(dataHandler, times(1)).saveProject(project);
	
	}

	/**
	 * Test project not found when autoProjectCreation is FALSE.
	 * @throws Exception
	 */
	@Test
	public void cannotDuplicateProject() throws Exception {

		when(dataHandler.isLocal()).thenReturn(false);
		Optional<Project> oProject = Optional.of(new Project(1789, "myProject"));
		when(projectHandler.lookup("myProject")).thenReturn(oProject);
		doNothing().when(dataHandler).saveProject(any(Project.class));

		SettingsGeneration settings = new SettingsGeneration();
		settings.setUrlRepository("https://myUrlRepository/myProject");

		this.mvc.perform(put("/api/project/analysis")
			.content(objectMapper.writeValueAsString(settings))
			.contentType(MediaType.APPLICATION_JSON_UTF8))

			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is("The project myProject already exists!")))

			.andDo(print())
			.andReturn();		
	}

}
