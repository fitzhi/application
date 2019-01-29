package fr.skiller.bean.impl;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.Normalizer;

import javax.validation.constraints.AssertTrue;

import org.junit.Before;
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

	@Before
	public void before() {
		staffHandler.getStaff().put(1000, 
				new Staff(1000,"Christian Aligato", "Chavez Tugo", "cact" , "cact", "cact@void.com", ""));
		staffHandler.getStaff().put(1001, 
				new Staff(1001, "Ouaamou", "Mohammed", "mouaamou" , "mouaamou", "mouaamou@void.com", ""));
		staffHandler.getStaff().put(1002, 
				new Staff(1002, "Jérôme", "WithAccent", "jwithaccent" , "jwithaccent", "jwithaccent@void.com", ""));
		
	}
	
	@Test
	public void testLookupChristian() throws Exception {
		assertThat(staffHandler.lookup("Christian Aligato Chavez Tugo")).isNotNull();
	}

	@Test
	public void testLookupYassine() throws Exception {
		assertThat(staffHandler.lookup("Ouaamou Mohammed")).isNotNull();
	}

	@Test
	public void testLookupJeromeWithAccent() throws Exception {
		assertThat(Normalizer.normalize("Jérôme WithAccent", Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""))
		.isEqualTo("Jerome WithAccent");
		assertThat(staffHandler.lookup("Jérôme WithAccent")).isNotNull();
	}
	
	@Test
	public void testLookupJeromeWithoutAccent() throws Exception {
		assertThat(Normalizer.normalize("Jérôme WithAccent", Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""))
		.isEqualTo("Jerome WithAccent");
		assertThat(staffHandler.lookup("Jerome WithAccent")).isNotNull();
	}
}
