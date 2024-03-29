package com.fitzhi.controller.project;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Contributor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerContributorsTest {

	private final Logger logger = LoggerFactory.getLogger(ProjectControllerContributorsTest.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectHandler projectHandler;

	@Autowired
	private StaffHandler staffHandler;

	private static final int ID_STAFF = 1;

	@Before
	public void before() {
		given(this.projectHandler.getLocker()).willReturn("Test");
	}

	@Test
	@WithMockUser
	public void testloadContributors() throws Exception {

		Staff staff = staffHandler.getStaff().get(ID_STAFF);

		List<Contributor> contributors = new ArrayList<>();
		contributors.add(new Contributor(ID_STAFF, LocalDate.now(), LocalDate.now(), 100, 200));
		given(this.projectHandler.contributors(666)).willReturn(contributors);

		MvcResult result = this.mvc.perform(get("/api/project/666/contributors")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.idProject", is(666)))
				.andExpect(jsonPath("$.contributors[0].idStaff", is(ID_STAFF)))
				.andExpect(jsonPath("$.contributors[0].fullname", is(staff.fullName())))
				.andReturn();

		if (logger.isDebugEnabled()) {
			logger.debug(result.getResponse().getContentAsString());
		}

		Assert.isTrue((result.getResponse().getContentAsString().indexOf(staff.fullName()) != -1),
				"The fullname has to be in clear in the response if the shuffle mode is OFF");

	}
}
