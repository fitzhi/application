package fr.skiller.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.skiller.bean.DataHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * The goal of this test is to ensure that the password stay hidden inside the back-end, 
 * and is not sent to the browser.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerSpecialPasswordTest {
	
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
	
	
	@Before
	public void before() {
		staffs = staffHandler.getStaff();
		Staff staff = new Staff(staffs.size()+1, "user", "password");
		staffs.put(staff.getIdStaff(), staff);
	}

	@Test
	@WithMockUser
	public void doNotTransportThePassword() throws Exception  {
		this.mvc.perform(get(
				String.format("/api/staff/%d", staffs.size())
				))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.login", is("user")))	
		.andExpect(content().string(not(containsString("password:"))));	
	}

	@After
	public void after()  {
		staffs.remove(staffs.size());
	}	
	
}
