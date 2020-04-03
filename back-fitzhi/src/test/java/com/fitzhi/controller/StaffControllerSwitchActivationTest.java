package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerSwitchActivationTest {

	private static final String STAFF_SWITCH_ACTIVATION = "/api/staff/switchActiveState/";


	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	@Before 
	public void before() throws SkillerException {
		staffHandler.getStaff().put(1789, 
				new Staff(1789,"Prenom", "Nom", "Surnom" , "UNIQUE_LOGIN", "adresse@mail.com", "DIEU"));
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(1789));
	}
	

	@Test
	@WithMockUser
	public void activateUnknownDeveloper() throws Exception {

		this.mvc.perform(
				get(STAFF_SWITCH_ACTIVATION + 1805)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().json("{code: -1001}"));
	}
	
	@Test
	@WithMockUser
	public void activateDeveloper() throws Exception {
		Staff staff = staffHandler.getStaff().get(1789);
		staff.setActive(true);
		staff.setDateInactive(null);
		this.mvc.perform(
				get(STAFF_SWITCH_ACTIVATION + 1789))
				.andExpect(status().isOk());
		
		staff = staffHandler.getStaff().get(1789);
		Assert.assertFalse(staff.isActive());
		Assert.assertNotNull(staff.getDateInactive());
				
	}	

	@Test
	@WithMockUser
	public void deactivateDeveloper() throws Exception {
		Staff staff = staffHandler.getStaff().get(1789);
		staff.setActive(false);
		staff.setDateInactive(LocalDate.now());
		this.mvc.perform(
				get(STAFF_SWITCH_ACTIVATION + 1789))
				.andExpect(status().isOk());
		
		staff = staffHandler.getStaff().get(1789);
		Assert.assertTrue(staff.isActive());
		Assert.assertNull(staff.getDateInactive());
				
	}	
	
	@Before 
	public void after() {
		staffHandler.getStaff().remove(1789);
	}

}