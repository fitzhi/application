package com.fitzhi.controller.staff;

import static com.fitzhi.Error.CODE_LOGIN_ALREADY_EXIST;
import static com.fitzhi.Error.CODE_STAFF_NOFOUND;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.controller.StaffController;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

/**
 * Testing the URL {@code /staff/save} and the controller {@link StaffController}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerSaveStaffMemberTest {

	private final String STAFF_SAVE = "/api/staff";

	private final int ID_JAVA = 1;
	
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
		// We create one staff member with ID = 1000 
		Staff staff = new Staff(1000,"Christian Aligato", "Chavez Tugo", "my login" , "cact", "cact@void.com", "level");
		staff.getExperiences().add(new Experience(ID_JAVA, 0));
		staff.getMissions().add(new Mission(1000, 1789, "The big revolution"));
		staffHandler.getStaff().put(1000, staff);
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(1000));
		
		// We create a second staff member with ID = 1001 
		staffHandler.getStaff().put(1001, 
				new Staff(1001,"Prenom", "Nom", "Surnom" , "UNIQUE_LOGIN", "adresse@mail.com", "DIEU"));
		Assert.assertTrue ("staff is registered", staffHandler.hasStaff(1001));
	}
	
	/**
	 * Testing the modification of the staff member with ID = 1000.
	 */
	@Test
	@WithMockUser
	public void shouldCorrectlyUpdateTheUpdatedStaff() throws Exception {
		
		Staff st = new Staff(1000,"firstName", "lastName", "nickName" , "login", "email@void.com", "level");
		
		this.mvc.perform(put(STAFF_SAVE + "/1000")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(st)))
			.andExpect(status().isNoContent());
			
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
	
	/**
	 * Trying to update an unregistered staff member (ID unknown).
	 * The application should return a 404 code error.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void shouldSend404ErrorforUnregisteredStaff() throws Exception {
		this.mvc.perform(put("/api/staff/404")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.content(gson.toJson(new Staff(404, "", ""))))
			.andExpect(status().isNotFound());
	}

	/**
	 * Cannot record 2 different staff members with the same login
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void shouldNotSave2DifferentStaffMembersWithTheSameLogin() throws Exception {
		// This staff have the same login "UNIQUE_LOGIN" than the staff member with 1001, already registered during the before step
		Staff staff = new Staff(1000,"firstName", "lastName", "nickName" , "UNIQUE_LOGIN", "email@void.com", "level");
		this.mvc.perform(put(STAFF_SAVE + "/1000")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(staff)))
				.andExpect(status().isInternalServerError())
				.andExpect(content().json(String.format("{code: %d}", CODE_LOGIN_ALREADY_EXIST)));		
	}
	
	/**
	 * Cannot UPDATE an unknown staff member
	 * @throws Exception
	 */
	@Test
	@WithMockUser
	public void cannotUpdateAnUnregisteredWorkforceMember() throws Exception {
		// This staff have the same login "UNIQUE_LOGIN" than the staff member with 1001, already registered during the before step
		Staff staff = new Staff(1002,"firstName", "lastName", "nickName" , "UNIQUE_LOGIN", "email@void.com", "level");
		this.mvc.perform(put(STAFF_SAVE + "/1002")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8)
				.content(gson.toJson(staff)))
				.andExpect(status().isNotFound())
				.andExpect(content().json(String.format("{code: %d}", CODE_STAFF_NOFOUND)));
	}

	@After
	public void after() {
		staffHandler.getStaff().remove(1000);
		staffHandler.getStaff().remove(1001);
	}
}