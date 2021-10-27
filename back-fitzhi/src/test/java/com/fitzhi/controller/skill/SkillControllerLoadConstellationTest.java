package com.fitzhi.controller.skill;

import static com.fitzhi.Error.CODE_MONTH_SKILLS_CONSTELLATION_NOFOUND;
import static com.fitzhi.Error.CODE_YEAR_MONTH_INVALID;
import static com.fitzhi.Error.MESSAGE_MONTH_SKILLS_CONSTELLATION_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.MessageFormat;
import java.time.LocalDate;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.controller.SkillController;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing the method {@link SkillController#loadConstellation(int, int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SkillControllerLoadConstellationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private SkillHandler skillHandler;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	/**
	 * We test the fact that there is no constallation for the given month.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void notfound() throws Exception {

		LocalDate month = LocalDate.of(2020, 12, 1);
		when(skillHandler.loadConstellation(month)).thenThrow(
			new NotFoundException(
				CODE_MONTH_SKILLS_CONSTELLATION_NOFOUND, 
				MessageFormat.format(MESSAGE_MONTH_SKILLS_CONSTELLATION_NOFOUND, 12, 2020)));

		this.mvc.perform(get("/api/skill/constellation/2020/12")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_MONTH_SKILLS_CONSTELLATION_NOFOUND)))
			.andDo(print());

		Mockito.verify(skillHandler, times(1)).loadConstellation(month);
	}

	/**
	 * We test an invalid month 13!
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void invalidDate() throws Exception {
		when(skillHandler.loadConstellation(any())).thenReturn(null);
		this.mvc.perform(get("/api/skill/constellation/2020/13")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_YEAR_MONTH_INVALID)))
			.andDo(print());
		verify(skillHandler, times(0)).loadConstellation(any());
	}

}
