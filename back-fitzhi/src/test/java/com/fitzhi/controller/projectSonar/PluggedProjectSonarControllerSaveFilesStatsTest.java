package com.fitzhi.controller.projectSonar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectGhostController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.FilesStats;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * <p>
 * Test of the class {@link ProjectGhostController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedProjectSonarControllerSaveFilesStatsTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;

	Project project;
	
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.lookup(1);
		SonarProject sp = new SonarProject();
		sp.setKey("key-sonar-1");
		project.getSonarProjects().add(sp);
		sp = new SonarProject();
		sp.setKey("key-sonar-2");
		project.getSonarProjects().add(sp);
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {

		List<FilesStats> l = new ArrayList<>();
		l.add(new FilesStats("css", 7));
		l.add(new FilesStats("java", 67));
		l.add(new FilesStats("ts", 35));
	
		MvcResult result = this.mvc.perform(put("/api/project/1/sonar/key-sonar-1/filesStats")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(l)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		
		Boolean b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);

		l = new ArrayList<>();
		l.add(new FilesStats("java", 28));
		l.add(new FilesStats("xml", 12));
		
		result = this.mvc.perform(put("/api/project/1/sonar/key-sonar-2/filesStats")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(l)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		
		b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);

		result = this.mvc.perform(get("/api/project/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		project = gson.fromJson(result.getResponse().getContentAsString(), Project.class);
		
		SonarProject sp = project.getSonarProjects().get(0);
		
		//
		// THIS PROJECT HAS BEEN ADDED TO THIS STAFF MEMBER.
		//
		Assert.assertTrue(sp.getProjectFilesStats().size() == 3);
		Assert.assertTrue("css".equals(sp.getProjectFilesStats().get(0).getLanguage()));
		Assert.assertTrue("java".equals(sp.getProjectFilesStats().get(1).getLanguage()));
		Assert.assertTrue("ts".equals(sp.getProjectFilesStats().get(2).getLanguage()));

		Assert.assertTrue(sp.getProjectFilesStats().get(0).getNumberOfFiles() == 7);
		Assert.assertTrue(sp.getProjectFilesStats().get(1).getNumberOfFiles() == 67);
		Assert.assertTrue(sp.getProjectFilesStats().get(2).getNumberOfFiles() == 35);

	}
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.lookup(1);
		project.getSonarProjects().clear();
	}
	
}
