package com.fitzhi.controller.skill;

import static com.fitzhi.Error.CODE_SKILL_NOFOUND;
import static com.fitzhi.Error.MESSAGE_SKILL_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.MessageFormat;
import java.util.Optional;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.controller.SkillController;
import com.fitzhi.data.internal.Skill;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing the method
 * {@link SkillController#search(String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SkillControllerSearchTest {

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
	public void successfullSearch() throws Exception {

		when(skillHandler.lookup("Java")).thenReturn(Optional.of(new Skill(1789, "Java")));

		this.mvc.perform(get("/api/skill/name/Java")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1789)))
			.andExpect(jsonPath("$.title", is("Java")))
			.andDo(print())
			.andExpect(status().isOk());
	
	}

	@Test
	@WithMockUser
	public void failedSearch() throws Exception {

		when(skillHandler.lookup("unknown")).thenReturn(Optional.empty());

		this.mvc.perform(get("/api/skill/name/unknown")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_SKILL_NOFOUND)))
			.andExpect(jsonPath("$.message", is(MessageFormat.format(MESSAGE_SKILL_NOFOUND, "unknown"))))
			.andExpect(status().isNotFound())
			.andDo(print());
	
	}

}
