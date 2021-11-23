package com.fitzhi.controller.project;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "shuffleData=1" }) 
@Slf4j

public class ProjectControllerContributorsInShuffleModeTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	public MockMvc mvc;

	@MockBean
	public ProjectHandler projectHandler;
	
	@Autowired
	public StaffHandler staffHandler;

	private static final int ID_STAFF = 1;

	@Test
	@WithMockUser
	public void testloadContributors() throws Exception {

		Staff staff = staffHandler.getStaff(ID_STAFF);
		
		List<Contributor> contributors = new ArrayList<>();
		contributors.add(new Contributor(ID_STAFF, LocalDate.now(), LocalDate.now(), 100, 200));
		given(this.projectHandler.contributors(666)).willReturn(contributors);

		MvcResult result = this.mvc.perform(get("/api/project/666/contributors"))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.idProject").value(666))
			.andExpect(jsonPath("$.contributors[0].idStaff").value(ID_STAFF))
			.andReturn();
		
		if (log.isDebugEnabled()) {
			log.debug (result.getResponse().getContentAsString());
		}
		
		Assert.doesNotContain(result.getResponse().getContentAsString(), staff.fullName(), "The fullname has to be shuffled");

	}
}
