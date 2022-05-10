package com.fitzhi.controller.skill;

import static com.fitzhi.Error.CODE_SKILL_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.controller.SkillController;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.NotFoundException;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing the method {@link SkillController#read(int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class SkillControllerReadTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private SkillHandler skillHandler;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Test
	@WithMockUser
	public void successfullRead() throws Exception {

		when(skillHandler.getSkill(1789)).thenReturn(new Skill(1789, "Java"));

		this.mvc.perform(get("/api/skill/1789")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1789)))
			.andExpect(jsonPath("$.title", is("Java")))
			.andDo(print())
			.andExpect(status().isOk());

		Mockito.verify(skillHandler, times(1)).getSkill(1789);
	}

	@Test
	@WithMockUser
	public void failedRead() throws Exception {

		when(skillHandler.getSkill(1914)).thenThrow(
			new NotFoundException(CODE_SKILL_NOFOUND, "Skill not found"));

		this.mvc.perform(get("/api/skill/1914")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_SKILL_NOFOUND)))
			.andExpect(jsonPath("$.message", is("Skill not found")))
			.andExpect(status().isNotFound())
			.andDo(print());
	
	}

}
