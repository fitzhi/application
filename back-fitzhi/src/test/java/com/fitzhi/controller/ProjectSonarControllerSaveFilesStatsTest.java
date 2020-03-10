package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.in.BodyParamSonarFilesStats;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.FilesStats;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.SkillerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
public class ProjectSonarControllerSaveFilesStatsTest {

//	private final Logger logger = LoggerFactory.getLogger(ProjectGhostControllerTest.class.getCanonicalName());

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
	
	final int ID_PROJECT = 1;
	
	private final String KEY_SONAR_1 = "key-sonar-1";
	private final String KEY_SONAR_2 = "key-sonar-2";
	
	@Before
	public void before() throws SkillerException {
		project = projectHandler.get(ID_PROJECT);
		SonarProject sp = new SonarProject();
		sp.setKey(KEY_SONAR_1);
		project.getSonarProjects().add(sp);
		sp = new SonarProject();
		sp.setKey(KEY_SONAR_2);
		project.getSonarProjects().add(sp);
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {
		BodyParamSonarFilesStats bpsfs = new BodyParamSonarFilesStats();
		bpsfs.setIdProject(ID_PROJECT);
		bpsfs.setSonarProjectKey(KEY_SONAR_1);
		List<FilesStats> l = new ArrayList<>();
		l.add(new FilesStats("css", 7));
		l.add(new FilesStats("java", 67));
		l.add(new FilesStats("ts", 35));
		bpsfs.setFilesStats(l);
	
		MvcResult result = this.mvc.perform(post("/api/project/sonar/files-stats")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpsfs)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		
		Boolean b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);

		bpsfs = new BodyParamSonarFilesStats();
		bpsfs.setIdProject(ID_PROJECT);
		bpsfs.setSonarProjectKey(KEY_SONAR_2);
		l = new ArrayList<>();
		l.add(new FilesStats("java", 28));
		l.add(new FilesStats("xml", 12));
		bpsfs.setFilesStats(l);
		
		result = this.mvc.perform(post("/api/project/sonar/files-stats")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(bpsfs)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		
		b = gson.fromJson(result.getResponse().getContentAsString(), Boolean.class);
		Assert.assertTrue(b);

		result = this.mvc.perform(get("/api/project/id/" + ID_PROJECT))
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
	public void after() throws SkillerException {
		project = projectHandler.get(ID_PROJECT);
		project.getSonarProjects().clear();
				
	}
	
}
