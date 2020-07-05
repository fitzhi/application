package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Testing the URL /staff/save
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerRemoveStaffMemberTest {

	private final String API_STAFF = "/api/staff/";

	private final int ID_STAFF = 1964;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
	
	@Before 
	public void before()  {
		Staff staff = new Staff(ID_STAFF,"Frédéric", "VIDAL", "frvidal" , "frvidal", "frvidal@void.com", "level");
		staff.getExperiences().add(new Experience(1, 0));
		staff.getMissions().add(new Mission(1000, 1789, "The big revolution"));
		staffHandler.getStaff().put(ID_STAFF, staff);
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(ID_STAFF));
	}
	
	@Test
	@WithMockUser
	public void testRemoveUnknownStaff() throws Exception {
		this.mvc.perform(delete(API_STAFF + "666")).andExpect(status().isNotFound());
	}
		
	@Test
	@WithMockUser
	public void testRemoveStaffOk() throws Exception {
		this.mvc.perform(delete(API_STAFF + 1964)).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	public void testRemoveAllStaff() throws Exception {	
		this.mvc.perform(delete(API_STAFF)).andExpect(status().isMethodNotAllowed());		
	}

	
	@After
	public void after() {
		staffHandler.getStaff().remove(1964);
	}
}