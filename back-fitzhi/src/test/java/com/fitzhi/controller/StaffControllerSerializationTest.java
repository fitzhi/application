/**
 * 
 */
package com.fitzhi.controller;

import static org.junit.Assert.assertEquals;
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

import com.fitzhi.bean.ProjectHandler;
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

	String STAFF = "{\"idStaff\":56,\"firstName\":\"Eric\",\"lastName\":\"CHANAL\",\"nickName\":null,\"login\":\"AdminCarto\",\"email\":\"echanal@void.fr\",\"level\":\"ET 2\",\"password\":null,\"active\":true,\"dateInactive\":null,\"application\":null,\"typeOfApplication\":0,\"external\":false,\"missions\":[{\"idStaff\":56,\"idProject\":2,\"name\":\"VEGEO\",\"firstCommit\":\"2016-05-31\",\"lastCommit\":\"2017-03-16\",\"numberOfCommits\":18,\"numberOfFiles\":81}],\"experiences\":[],\"authorities\":[{\"authority\":\"ROLE_USER\"}],\"empty\":false,\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true,\"username\":\"AdminCarto\"}";

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
		Assert.assertEquals(staff.getMissions().size(), 1);
		Assert.assertEquals(staff.getMissions().get(0).getIdProject(), 2);
		Assert.assertEquals(staff.getMissions().get(0).getIdStaff(), 56);
		
	}
	
	public void after() {
		staffHandler.getStaff().remove(56);
	}
}
