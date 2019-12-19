package fr.skiller.bean.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.Normalizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerLookupTest {

	private static final String JEROME_SANS_ACCENT = "Jerome WithAccent";
	
	private static final String JEROME_WITH_ACCENT = "Jérôme WithAccent";

	private static final String FREDERIC_SANS_ACCENT = "Frederic NABILLAU";
	
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
		staffHandler.getStaff().put(1003, 
				new Staff(1003, "Guillaume", "Guorin De Tourville", "gguorin" , "gguorin", "gguorin@void.com", ""));
		staffHandler.getStaff().put(1004, 
				new Staff(1004, "Frédéric", "NABILLAU", "fnabillau" , "fnabillau", "fnabillau@void.com", ""));
		
	}
	
	@Test
	public void testLookupChristian()  {
		assertThat(staffHandler.lookup("Christian Aligato Chavez Tugo")).isNotNull();
	}

	@Test
	public void testLookupGuorinDeTourvilleGuillaume()  {
		assertThat(staffHandler.lookup("Guorin De Tourville Guillaume")).isNotNull();
	}
		

	@Test
	public void testLookupYassine() {
		assertThat(staffHandler.lookup("Ouaamou Mohammed")).isNotNull();
	}

	@Test
	public void testLookupJeromeWithAccent()  {
		assertThat(Normalizer.normalize(JEROME_WITH_ACCENT, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""))
		.isEqualTo(JEROME_SANS_ACCENT);
		assertThat(staffHandler.lookup(JEROME_WITH_ACCENT)).isNotNull();
	}
	
	@Test
	public void testLookupJeromeWithoutAccent()  {
		assertThat(Normalizer.normalize(JEROME_WITH_ACCENT, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""))
		.isEqualTo(JEROME_SANS_ACCENT);
		assertThat(staffHandler.lookup(JEROME_SANS_ACCENT)).isNotNull();
	}
	
	@Test
	public void testFredericNabillauSansAccent()  {
		assertThat(staffHandler.lookup(FREDERIC_SANS_ACCENT)).isNotNull();
	}
	
	@Test
	public void testFredericNabillauWithSpaces()  {
		assertThat(staffHandler.lookup("Frédéric  NABILLAU")).isNotNull();
		assertThat(staffHandler.lookup("   Frédéric    NABILLAU   ")).isNotNull();
	}
		
	
	@After
	public void after() {
		staffHandler.getStaff().remove(1000);
		staffHandler.getStaff().remove(1001);
		staffHandler.getStaff().remove(1002);
		staffHandler.getStaff().remove(1003);
		staffHandler.getStaff().remove(1004);
	}
}
