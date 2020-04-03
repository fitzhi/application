package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.http.MediaType;
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
public class StaffControllerSaveStaffMemberTest {

	private final String STAFF_SAVE = "/api/staff/save";

	private final int ID_JAVA = 1;;
	
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
		Staff staff = new Staff(1000,"Christian Aligato", "Chavez Tugo", "my login" , "cact", "cact@void.com", "level");
		staff.getExperiences().add(new Experience(ID_JAVA, 0));
		staff.getMissions().add(new Mission(1000, 1789, "The big revolution"));
		staffHandler.getStaff().put(1000, staff);
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(1000));
		
		staffHandler.getStaff().put(1001, 
				new Staff(1001,"Prenom", "Nom", "Surnom" , "UNIQUE_LOGIN", "adresse@mail.com", "DIEU"));
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(1001));
	}
	
	@Test
	@WithMockUser
	public void saveStaffMemberOk() throws Exception {
		Staff st = new Staff(1000,"firstName", "lastName", "nickName" , "login", "email@void.com", "level");
		this.mvc.perform(post(STAFF_SAVE)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.content(gson.toJson(st)))
			.andExpect(status().isOk());		
		Staff s = staffHandler.getStaff().get(1000);
		Assert.assertEquals ("firstName", s.getFirstName());
		Assert.assertEquals ("lastName", s.getLastName());
		Assert.assertEquals ("nickName", s.getNickName());
		Assert.assertEquals ("login", s.getLogin());
		Assert.assertEquals ("email@void.com", s.getEmail());
		Assert.assertEquals ("level", s.getLevel());
		// We keep the existing experience
		Assert.assertEquals (1, s.getExperiences().size());
		Assert.assertEquals (ID_JAVA, s.getExperiences().get(0).getId());
		Assert.assertEquals (0, s.getExperiences().get(0).getLevel());
		// We keep the existing missions
		Assert.assertEquals (1, s.getMissions().size());
		Assert.assertEquals (1789, s.getMissions().get(0).getIdProject());
	}
	
	@Test
	@WithMockUser
	public void saveStaffMemberKoUnregisteredStaff() throws Exception {
		Staff st = new Staff(404,"firstName", "lastName", "nickName" , "login", "email@void.com", "level");
		this.mvc.perform(post(STAFF_SAVE)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.content(gson.toJson(st)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json("{code: -1001}"))
				.andDo(print());
	}

	@Test
	@WithMockUser
	public void saveStaffMemberKoDuplicateLogin() throws Exception {
		Staff staff = new Staff(1000,"firstName", "lastName", "nickName" , "UNIQUE_LOGIN", "email@void.com", "level");
		this.mvc.perform(post(STAFF_SAVE)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(staff)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json("{code: -1009}"))
				.andDo(print());
		
	}
	
	@After
	public void after() {
		staffHandler.getStaff().remove(1000);
		staffHandler.getStaff().remove(1001);
	}
}