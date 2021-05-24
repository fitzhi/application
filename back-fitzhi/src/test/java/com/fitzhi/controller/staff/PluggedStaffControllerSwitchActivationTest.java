package com.fitzhi.controller.staff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

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

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PluggedStaffControllerSwitchActivationTest {

	private static final String STAFF_SWITCH_ACTIVATION = "/api/staff/%d/switchActiveStatus";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	@Before 
	public void before() throws ApplicationException {
		// We force the Staff identifier to 1789.
		staffHandler.getStaff().put(1789, 
				new Staff(1789,"Prenom", "Nom", "Surnom" , "UNIQUE_LOGIN", "adresse@mail.com", "DIEU"));
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(1789));
	}

	@Test
	@WithMockUser
	public void activateUnknownDeveloper() throws Exception {

		this.mvc.perform(post(String.format(STAFF_SWITCH_ACTIVATION,1805))
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(content().json("{code: -1001}"));
	}
	
	@Test
	@WithMockUser
	public void activateDeveloper() throws Exception {

		Staff staff = staffHandler.getStaff().get(1789);
		staff.setActive(true);
		staff.setDateInactive(null);

		this.mvc.perform(post(String.format(STAFF_SWITCH_ACTIVATION, 1789)))
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

		this.mvc.perform(post(String.format(STAFF_SWITCH_ACTIVATION, 1789)))
			.andExpect(status().isOk());
		
		staff = staffHandler.getStaff().get(1789);
		Assert.assertTrue(staff.isActive());
		Assert.assertNull(staff.getDateInactive());
				
	}	
	
	@Before 
	public void after() {
		staffHandler.removeStaff(1789);
	}

}