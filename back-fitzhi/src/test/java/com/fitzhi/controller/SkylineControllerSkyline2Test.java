package com.fitzhi.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
public class SkylineControllerSkyline2Test {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@MockBean
	private SkylineProcessor skylineProcessor;

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
	 * We verify that the generation of the skyline is delegated to to {@link SkylineProcessor#generateSkyline()}
	 * @throws Exception Oops !
	 */
	@Test
	@WithMockUser	
	public void testCallSkyline() throws Exception {

		Skyline skyline = new Skyline();
		skyline.getFloors().add(new ProjectFloor(1805, 2020, 11, 0, 0));

		when(skylineProcessor.generateSkyline()).thenReturn(skyline);

		MvcResult result = mvc.perform(get("/api/skyline")
			.accept(MediaType.APPLICATION_JSON_UTF8))
		    .andExpect(status().isOk())
			.andExpect(content()
			.contentType("application/json;charset=UTF-8"))
			.andReturn();
		skyline = gson.fromJson(result.getResponse().getContentAsString(), Skyline.class);
		Assert.assertEquals(1, skyline.getFloors().size());
		
		Mockito.verify(skylineProcessor, times(1)).generateSkyline();
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1214);
		projectHandler.removeProject(1515);
	}
	
}
