package com.fitzhi.controller.staff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.StaffController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>
 * Testing the URL /staff/processActiveStatus
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 * @see StaffController#processActiveStatus(int)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "staffHandler.inactivity.delay=10" })
public class StaffControllerProcessActiveStatusTest {

	private final String STAFF_PROCESS_ACTIVE_STATUS = "/api/staff/processActiveStatus/%d";

	private final int ID_STAFF = 777;
	
	private final int ID_PROJECT = 666;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	@Before 
	public void before()  {
		Staff staff = new Staff(777,"Captain", "Haddock", "capt" , "capt", "capt@void.com", "level");
		staff.setForceActiveState(true);
		staff.setActive(false);
		staff.setDateInactive(null);
		staffHandler.getStaff().put(777, staff);
		staff.addMission(
				new Mission(ID_STAFF, ID_PROJECT, "Void", LocalDate.of(2017, 01, 01), LocalDate.now().minusDays(5), 100, 100));
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(777));
		
	}
	
	@Test
	@WithMockUser
	public void processActiveStatus() throws Exception {
		MvcResult result = this.mvc
			.perform(get(String.format(STAFF_PROCESS_ACTIVE_STATUS, ID_STAFF)))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(status().isOk())
			.andReturn();
		
		Staff staff = gson.fromJson(result.getResponse().getContentAsString(), Staff.class);
		Assert.assertTrue("Staff has to be active.", staff.isActive());
		Assert.assertNull(staff.getDateInactive());		
	}
	
	@After
	public void after() {
		staffHandler.getStaff().remove(777);
	}
}