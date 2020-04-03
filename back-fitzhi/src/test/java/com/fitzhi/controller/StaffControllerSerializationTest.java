/**
 * 
 */
package com.fitzhi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;

/**
 * @author frvidal
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerSerializationTest {

	String STAFF = "{\"idStaff\":56,\"firstName\":\"Eric\",\"lastName\":\"CHANAL\",\"nickName\":null,\"login\":\"echanal\",\"email\":\"echanal@void.fr\",\"level\":\"ET 2\",\"password\":null,\"forceActiveState\":false,\"active\":true,\"dateInactive\":null,\"application\":null,\"typeOfApplication\":0,\"external\":false,\"missions\":[],\"experiences\":[],\"authorities\":[{\"authority\":\"ROLE_USER\"}],\"empty\":false,\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true,\"username\":\"AdminCarto\"}";

	@Autowired
	private MockMvc mvc;

	@Autowired
	StaffHandler staffHandler;
	
	@Before
	public void before() {
		staffHandler.getStaff().put(56, new Staff(56, "login", "password")); 
		
	}
	
	@Test
	@WithMockUser
	public void test() throws Exception {
		this.mvc.perform(post("/api/staff/save")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(STAFF))
				.andExpect(status().isOk());
		
		Staff staff = staffHandler.getStaff(56); 
		Assert.assertEquals(staff.getLastName(), "CHANAL");
		Assert.assertEquals(staff.getFirstName(), "Eric");
		Assert.assertEquals(staff.getLogin(), "echanal");
		
	}
	
	public void after() {
		staffHandler.getStaff().remove(56);
	}
}
