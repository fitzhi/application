package com.fitzhi.controller.projectGhost;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.controller.ProjectGhostController;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

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
 * Test of the class {@link ProjectGhostController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectGhostControllerRemoveGhostTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@Test
	@WithMockUser
	public void removeOk() throws Exception {

		when(projectHandler.lookup(1789)).thenReturn(new Project(1789, "The revolution"));
		doNothing().when(projectHandler).removeGhost(new Project(1789, "The revolution"), "myLogin");

		this.mvc.perform(delete("/api/project/1789/ghost/myLogin"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().string("true"))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).lookup(1789);
		Mockito.verify(projectHandler, times(1)).removeGhost(new Project(1789, "The revolution"), "myLogin");
	}

	@Test
	@WithMockUser
	public void removeKo() throws Exception {

		when(projectHandler.lookup(1789)).thenThrow(new ApplicationException(7777, "error 7777"));

		this.mvc.perform(delete("/api/project/1789/ghost/myLogin"))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(7777)))
			.andExpect(jsonPath("$.message", is("error 7777")))
			.andDo(print())
			.andReturn();
		
		Mockito.verify(projectHandler, times(1)).lookup(1789);
	}
	
}
