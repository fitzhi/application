package com.fitzhi.bean.impl.StaffHandler;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.Normalizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Staff;

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
		staffHandler.getStaff().put(1005, 
				new Staff(1005, "信鑫", "king", "信鑫" , "信鑫", "nope@nope.com", ""));
	}
	
	@Test
	public void testLookupChristian()  {
		assertThat(staffHandler.lookup(new Author("Christian Aligato Chavez Tugo"))).isNotNull();
	}

	@Test
	public void testLookupGuorinDeTourvilleGuillaume()  {
		assertThat(staffHandler.lookup(new Author("Guorin De Tourville Guillaume"))).isNotNull();
	}

	@Test
	public void testLookupYassine() {
		assertThat(staffHandler.lookup(new Author("Ouaamou Mohammed"))).isNotNull();
	}

	@Test
	public void testLookupJeromeWithAccent()  {
		assertThat(Normalizer.normalize(JEROME_WITH_ACCENT, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""))
		.isEqualTo(JEROME_SANS_ACCENT);
		assertThat(staffHandler.lookup(new Author(JEROME_WITH_ACCENT))).isNotNull();
	}
	
	@Test
	public void testLookupJeromeWithoutAccent()  {
		assertThat(Normalizer.normalize(JEROME_WITH_ACCENT, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", ""))
		.isEqualTo(JEROME_SANS_ACCENT);
		assertThat(staffHandler.lookup(new Author(JEROME_SANS_ACCENT))).isNotNull();
	}
	
	@Test
	public void testFredericNabillauSansAccent()  {
		assertThat(staffHandler.lookup(new Author(FREDERIC_SANS_ACCENT))).isNotNull();
	}
	
	@Test
	public void testFredericNabillauWithSpaces()  {
		assertThat(staffHandler.lookup(new Author("Frédéric  NABILLAU"))).isNotNull();
		assertThat(staffHandler.lookup(new Author("   Frédéric    NABILLAU   "))).isNotNull();
	}
		
	@Test
	public void testComposedName()  {
		assertThat(staffHandler.lookup(new Author("信鑫-king"))).isNotNull();
	}
	
	@Test
	public void testComposedName2()  {
		assertThat(staffHandler.lookup(new Author("信鑫-King"))).isNotNull();
	}

	@After
	public void after() {
		staffHandler.removeStaff(1000);
		staffHandler.removeStaff(1001);
		staffHandler.removeStaff(1002);
		staffHandler.removeStaff(1003);
		staffHandler.removeStaff(1004);
		staffHandler.removeStaff(1005);
	}
}
