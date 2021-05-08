package com.fitzhi.controller.staff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import lombok.extern.slf4j.Slf4j;

/**
 * The goal of this test is to test the {@link StaffController#revokeProject(int, int)}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class StaffControllerRevokeProjectTest {
	
	/**
	 * Class in charge of the staff collection.
	 */
	@Autowired
	StaffHandler staffHandler;
	
	/**
	 * Class in charge of the staff collection.
	 */
	@Autowired
	DataHandler dataSaver;

	@Autowired
	private MockMvc mvc;
	
	Map<Integer, Staff> staffs;
	
	private int idStaff = -1;
	
	@Before
	public void before() {
		staffs = staffHandler.getStaff();
		idStaff = staffHandler.nextIdStaff();
		log.debug(String.format("id of new Staff member : %d", idStaff));
		Staff staff = new Staff(idStaff, "user", "password");
		staffs.put(staff.getIdStaff(), staff);

		
	}

	@Test
	@WithMockUser
	public void doCannotRevokeUnknownStaffMember() throws Exception {
		this.mvc.perform(delete("/api/staff/1794/project/1")).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	public void doCannotRevokeUnknownProject() throws Exception {
		this.mvc.perform(delete("/api/staff/1/project/1791")).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	public void doCannotRevokeActiveProject() throws Exception {
		Mission m = new Mission(idStaff, 1, "test");
		m.setNumberOfCommits(1);
		staffHandler.getStaff(idStaff).addMission(m);
		this.mvc.perform(delete(String.format("/api/staff/%d/project/1", idStaff))).andExpect(status().isInternalServerError());
	}

	@Test
	@WithMockUser
	public void doCannotRevokeInactiveProject() throws Exception {
		
		Mission m = new Mission(idStaff, 1, "test");
		m.setNumberOfCommits(0);
		staffHandler.getStaff(idStaff).addMission(m);

		this.mvc
			.perform(delete(String.format("/api/staff/%d/project/1", idStaff)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));

		Assert.assertTrue("The mission 1 has been deleted", staffHandler.getStaff(idStaff).getMissions().isEmpty());
	}

	@After
	public void after()  {
		staffs.remove(idStaff);
	}	
	
}
