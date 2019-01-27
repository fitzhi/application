package fr.skiller.bean.impl;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.constraints.AssertTrue;

import org.junit.Test;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffHandler_lookup_Test {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private StaffHandler staffHandler;

	@Test
	public void testLookupChristian() throws Exception {
		assertThat(staffHandler.lookup("Christian Alonso Chavez Ley")).isNotNull();
	}

	@Test
	public void testLookupYassine() throws Exception {
		String fullname = staffHandler.getFullname(200);
		System.out.println(fullname);
		assertThat(staffHandler.lookup("Ouaamou Yassine")).isNotNull();
	}

}
