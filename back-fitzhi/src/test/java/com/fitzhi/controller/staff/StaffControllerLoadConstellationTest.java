package com.fitzhi.controller.staff;

import static com.fitzhi.Error.CODE_MONTH_SKILLS_CONSTELLATION_NOFOUND;
import static com.fitzhi.Error.CODE_YEAR_MONTH_INVALID;
import static com.fitzhi.Error.MESSAGE_MONTH_SKILLS_CONSTELLATION_NOFOUND;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.SkillController;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.exception.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
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
 * Testing the method {@link SkillController#loadConstellation(int, int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerLoadConstellationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private StaffHandler staffHandler;

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
		when(staffHandler.loadConstellations(month)).thenThrow(
			new NotFoundException(
				CODE_MONTH_SKILLS_CONSTELLATION_NOFOUND, 
				MessageFormat.format(MESSAGE_MONTH_SKILLS_CONSTELLATION_NOFOUND, 12, 2020)));

		this.mvc.perform(get("/api/staff/constellation/2020/12")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_MONTH_SKILLS_CONSTELLATION_NOFOUND)))
			.andExpect(jsonPath("$.message", is("There is no skills data available for the month 12/2020.")))
			.andDo(print());

		Mockito.verify(staffHandler, times(1)).loadConstellations(month);
	}

	/**
	 * We test an invalid month 13!
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void invalidDate() throws Exception {
		when(staffHandler.loadConstellations(any())).thenReturn(null);
		this.mvc.perform(get("/api/staff/constellation/2020/13")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isInternalServerError())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.code", is(CODE_YEAR_MONTH_INVALID)))
			.andDo(print());
		verify(staffHandler, times(0)).loadConstellations(any());
	}

	/**
	 * We test the nominal mode.
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void nominal() throws Exception {

		List<Constellation> constellations = new ArrayList<>();
		constellations.add( Constellation.of(1, 10, 10));
		constellations.add( Constellation.of(2, 21, 21));
		when(staffHandler.loadConstellations(LocalDate.of(2021,10,1))).thenReturn(constellations);

		MvcResult result = this.mvc.perform(get("/api/staff/constellation/2021/10")
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andDo(print())
			.andReturn();

		Type listConstellationsType = new TypeToken<List<Constellation>>() {
		}.getType();

		Collection<Constellation> res = gson.fromJson(result.getResponse().getContentAsString(), listConstellationsType);
		Assert.assertEquals(2, res.size());

		verify(staffHandler, times(1)).loadConstellations(LocalDate.of(2021,10,1));
	}

}
