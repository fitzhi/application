package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fitzhi.Global;
import com.fitzhi.bean.StaffHandler;
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
public class StaffControllerSaveStaffMemberTest {

	private static final String STAFF_SAVE = "/api/staff/save";

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
		staffHandler.getStaff().put(1000, 
				new Staff(1000,"Christian Aligato", "Chavez Tugo", "my login" , "cact", "cact@void.com", ""));
		staffHandler.getStaff().put(1000, 
				new Staff(1001,"el oto", "Chavez Tugo", "cact" , "another login", "cact@void.com", ""));
	}
	
	@Test
	@WithMockUser
	public void saveStaffMemberOk() throws Exception {
		Staff st = new Staff(1000,"one", "two", "cact" , "cact", "cact@void.com", "");
		this.mvc.perform(post(STAFF_SAVE).header(HttpHeaders.CONTENT_TYPE, "application/json").content(gson.toJson(st))).andExpect(status().isOk());		
		Staff s = staffHandler.getStaff().get(1000);
		Assert.assertEquals ("one", s.getFirstName());
		Assert.assertEquals ("two", s.getLastName());
	}
	
	@Test
	@WithMockUser (username = "user", password = "password", roles = "USER")
	public void saveStaffMemberKoUnregisteredStaff() throws Exception {
		Staff st = new Staff(0,"one", "two", "cact" , "cact", "cact@void.com", "");
		this.mvc.perform(post(STAFF_SAVE)
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(gson.toJson(st)))
				.andExpect(status().isNotFound())
				.andExpect(header().string(Global.BACKEND_RETURN_CODE, "1"));
	}

	@Test
	@WithMockUser
	public void saveStaffMemberKoDuplicateLogin() throws Exception {
		Staff st = new Staff(1000,"one", "two", "cact" , "another login", "cact@void.com", "");
		this.mvc.perform(post(STAFF_SAVE)
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(gson.toJson(st)))
				.andExpect(status().isInternalServerError())
				.andExpect(header().string(Global.BACKEND_RETURN_CODE, 
						String.valueOf(com.fitzhi.Error.CODE_LOGIN_ALREADY_EXIST)));
	}
	
	@After
	public void after() {
		staffHandler.getStaff().remove(1000);
		staffHandler.getStaff().remove(1001);
	}
}