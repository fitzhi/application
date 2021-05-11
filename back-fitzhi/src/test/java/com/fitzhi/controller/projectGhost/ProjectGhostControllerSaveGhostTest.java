package com.fitzhi.controller.projectGhost;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectGhostController;
import com.fitzhi.controller.in.GhostAssociation;
import com.fitzhi.controller.util.LocalDateAdapter;
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
 * <p>
 * Test of the method {@link ProjectGhostController#saveGhost(int, GhostAssociation)}
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

	@MockBean
	private ProjectHandler projectHandler;

	private GhostAssociation ga() {
		GhostAssociation ga = new GhostAssociation();
		ga.setPseudo("myLogin");
		ga.setIdStaff(-1);
		ga.setTechnical(false);
		return ga;
	}

	@Test
	@WithMockUser
	public void savePseudoAssicatedToAStaff() throws Exception {

		final GhostAssociation ga = ga();
		ga.setIdStaff(1802);

		when(projectHandler.get(1789)).thenReturn(new Project(1789, "The revolution"));
		doNothing().when(projectHandler).associateStaffToGhost(
			new Project(1789, "The revolution"), "myLogin", 1802);

		this.mvc.perform(post("/api/project/1789/ghost")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(ga)))

			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).get(1789);
		Mockito.verify(projectHandler, times(1)).associateStaffToGhost(
			new Project(1789, "The revolution"), "myLogin", 1802);
	}

	@Test
	@WithMockUser
	public void savePseudoIsTechnical() throws Exception {

		final GhostAssociation ga = ga();
		ga.setTechnical(true);

		when(projectHandler.get(1789)).thenReturn(new Project(1789, "The revolution"));
		doNothing().when(projectHandler).setGhostTechnicalStatus(
			new Project(1789, "The revolution"), "myLogin", true);

		this.mvc.perform(post("/api/project/1789/ghost")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(ga)))

			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).get(1789);
		Mockito.verify(projectHandler, times(1)).setGhostTechnicalStatus(
			new Project(1789, "The revolution"), "myLogin", true);
	}

	@Test
	@WithMockUser
	public void resetPseudo() throws Exception {

		final GhostAssociation ga = ga();

		when(projectHandler.get(1789)).thenReturn(new Project(1789, "The revolution"));
		doNothing().when(projectHandler).resetGhost(new Project(1789, "The revolution"), "myLogin");

		this.mvc.perform(post("/api/project/1789/ghost")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(ga)))

			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).get(1789);
		Mockito.verify(projectHandler, times(1)).resetGhost(new Project(1789, "The revolution"), "myLogin");
	}

	@Test
	@WithMockUser
	public void saveKo() throws Exception {

		GhostAssociation ga = new GhostAssociation();
		ga.setPseudo("myLogin");
		ga.setIdStaff(1789);
		ga.setTechnical(false);

		when(projectHandler.get(1789)).thenThrow(new ApplicationException(7777, "error 7777"));

		this.mvc.perform(delete("/api/project/1789/ghost/myLogin")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(ga)))

			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(7777)))
			.andExpect(jsonPath("$.message", is("error 7777")))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).get(1789);
	}
	
}
