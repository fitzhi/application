package com.fitzhi.controller.skyline;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.stream.Collectors;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.SkylineController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectFloor;
import com.fitzhi.data.internal.Skyline;
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
 * Class in charge of the test of {@link com.fitzhi.controller.SkylineController#skyline()}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SkylineControllerSkylineTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProjectHandler projectHandler;

	@Before
	public void before() throws ApplicationException {
		Project project = new Project(1515, "Marignan");
		project.setActive(true);
		projectHandler.addNewProject(project);

		project = new Project(1214, "Bouvines");
		project.setActive(false);
		projectHandler.addNewProject(project);

	}

	/**
	 * Testing the call in the controller for the generation of the skyline
	 * @throws Exception Oops !
	 */
	@Test
	@WithMockUser	
	public void testCallSkyline() throws Exception {

		MvcResult result = mvc.perform(get("/api/skyline")
			.accept(MediaType.APPLICATION_JSON_UTF8))
		    .andExpect(status().isOk())
			.andExpect(content()
			.contentType("application/json;charset=UTF-8"))
			.andReturn();
		Skyline skyline = gson.fromJson(result.getResponse().getContentAsString(), Skyline.class);
		Assert.assertFalse(skyline.isEmpty());
		Set<Integer> projectIdentifiers = skyline.getFloors()
			.stream()
			.map(ProjectFloor::getIdProject)
			.distinct()
			.collect(Collectors.toSet());
		Assert.assertFalse(projectIdentifiers.isEmpty());
		Assert.assertTrue(projectIdentifiers.contains(1515));
		// The project Bouvines is inactive, so it cannot be present in the skyline 
		Assert.assertFalse(projectIdentifiers.contains(1214));
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1214);
		projectHandler.removeProject(1515);
	}
	
}
