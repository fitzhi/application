package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

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
import com.fitzhi.controller.in.BodyUpdateGhost;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
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
public class ProjectGhostControllerSaveGhostTest {

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
	
	@Before
	public void before() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		project.getGhosts().add(new Ghost("pseudoUnlinked", false));
		project.getGhosts().add(new Ghost("pseudoLinked", 2, false));
		
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {
		BodyUpdateGhost bug = new BodyUpdateGhost();
		bug.setIdProject(ID_PROJECT);
		bug.setPseudo("pseudoUnlinked");
		bug.setIdStaff(1);
	
		MvcResult result = this.mvc.perform(get("/api/staff/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();

		Staff staff = gson.fromJson(result.getResponse().getContentAsString(), Staff.class);
		//
		// THIS PROJECT IS NOT ALREADY DECLARED FOR THIS STAFF MEMBER
		//
		Assert.assertFalse(
				staff.getMissions().stream().anyMatch(mission -> mission.getIdProject() == ID_PROJECT));
		
		this.mvc.perform(post("/api/project/ghost/save")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(bug)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print());

		result = this.mvc.perform(get("/api/staff/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())
				.andReturn();
		staff = gson.fromJson(result.getResponse().getContentAsString(), Staff.class);
		//
		// THIS PROJECT HAS BEEN ADDED TO THIS STAFF MEMBER.
		//
		Assert.assertTrue(
				staff.getMissions().stream().anyMatch(mission -> mission.getIdProject() == ID_PROJECT));
	}
	
	@After
	public void after() throws ApplicationException {
		project = projectHandler.get(ID_PROJECT);
		project.getGhosts().clear();
				
	}
	
}
