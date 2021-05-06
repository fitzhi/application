/**
 * 
 */
package com.fitzhi.controller.administrationController;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fitzhi.bean.Administration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testing the complete installation cinematic
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdministrationControllerKeepVeryFirstConnectionTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	Administration administration;
	
	@Test
	public void saveVeryFirstConnection() throws Exception {

		doNothing().when(administration).saveVeryFirstConnection();

		// We save the very first connection
		this.mvc.perform(post("/api/admin/saveVeryFirstConnection"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(print());

		Mockito.verify(administration, times(1)).saveVeryFirstConnection();

	}
	
}