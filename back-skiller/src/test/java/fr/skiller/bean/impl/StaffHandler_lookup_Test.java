package fr.skiller.bean.impl;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Test;

import fr.skiller.bean.StaffHandler;

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
	public void testLookup() throws Exception {

		System.out.println(staffHandler.lookup("Christian Alonso Chavez Ley"));
	}
}
