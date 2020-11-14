package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.util.Map;

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

import lombok.extern.slf4j.Slf4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Testing the method {@link SkillController#save(com.fitzhi.data.internal.Skill)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class SkillControllerDetectionTemplatesTest {

	private static final String SKILL_DETECTION_TEMPLATES = "/api/skill/detection-templates";

	@Autowired
	private MockMvc mvc;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Test
	@WithMockUser
	public void skillDetectionTemplates() throws Exception {

		MvcResult result = this.mvc.perform(get(SKILL_DETECTION_TEMPLATES)
			.contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andReturn();
		Type templatesType = new TypeToken<Map<Integer, String>>(){}.getType();
		Map<Integer, String> templates = gson.fromJson(result.getResponse().getContentAsString(), templatesType);
		if (log.isDebugEnabled()) {
			templates.keySet().stream().forEach(key -> {
				log.debug(key + ' ' + templates.get(key));
			});
		}
	}
	
}
