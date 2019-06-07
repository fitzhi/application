/**
 * 
 */
package fr.skiller.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest {

	@Autowired
	private MockMvc mvc;

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder().create();

	@Test
	@WithMockUser
	public void testVerySimplePostString() throws Exception {
		String ret = this.mvc.perform(post("/test/post_a_String").content("test")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();		
		assert ("test OK".equals(ret));
	}
	
	@Test
	@WithMockUser
	public void testVerySimplePostTest() throws Exception {
		
		String ret = this.mvc.perform(post("/test/post_a_Test")
				.content(gson.toJson( new fr.skiller.data.internal.ForTest("test@")))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();	
		Assert.assertEquals("{\"test\":\"test@ OK\"}", ret);
	}

}