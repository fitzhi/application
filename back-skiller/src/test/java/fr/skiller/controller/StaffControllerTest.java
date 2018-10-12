/**
 * 
 */
package fr.skiller.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;
		
	@Test
	public void updateStaff() throws Exception {
		
		Staff staffInput = new Staff (2, "John", "Doe", "unknown","fvidal","jdoe@gmail.com", "ICD 1", true);
		String response = this.mvc.perform(post("/staff/save").
				content(gson.toJson(staffInput)).contentType(MediaType.APPLICATION_JSON)).
					andExpect(status().isOk()).andReturn().getResponse().getContentAsString();		
		Staff staff = gson.fromJson(response, Staff.class);
		Assert.assertEquals(staff.lastName, "Doe");
		Assert.assertNull(staff.dateInactive);
		
		staffInput.isActive = false;
		response = this.mvc.perform(post("/staff/save").
				content(gson.toJson(staffInput)).contentType(MediaType.APPLICATION_JSON)).
					andExpect(status().isOk()).andReturn().getResponse().getContentAsString();	
		staff = gson.fromJson(response, Staff.class);
		Assert.assertNotNull(staff.dateInactive);

		staffHandler.init();
		
	}	
	
}