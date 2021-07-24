package com.fitzhi.bean.impl.StaffHandler;


import java.text.Normalizer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.StringTransform;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;


/**
 * <p>
 * Testing the methods
 * <ul>
 * <li>
 * {@link StaffHandler#isEligible(com.fitzhi.data.internal.Staff, String, StringTransform)},
 * </li><li>
 * {@link StaffHandler#isEligible(com.fitzhi.data.internal.Staff, String) }
 * </li>
 * </ul>
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerIsEligibleTest {

	@Autowired
	StaffHandler staffHandler;
	
	@Test
	public void testisEligibleLowerCase() throws ApplicationException {
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "frvidal", "frvidal","frvidal@nope.com", "Gaulo-roman");
		StringTransform st = s -> s.toLowerCase();
		Assert.assertTrue(staffHandler.isEligible(staff, "frvidal", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "FrVidal", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "Frédéric VIDAL", st));
		Assert.assertFalse(staffHandler.isEligible(staff, "VIDAL Frederic", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "VIDAL Frédéric", st));
		Assert.assertFalse(staffHandler.isEligible(staff, "frederic vidal", st));
		Assert.assertFalse(staffHandler.isEligible(staff, "fvidal", st));
		Assert.assertFalse(staffHandler.isEligible(staff, "frederic nope", st));
	}

	@Test
	public void testisEligibleNormalizedOne() throws ApplicationException {
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "frvidal", "frvidal","frvidal@nope.com", "Gaulo-roman");
		StringTransform st = s -> Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "").toLowerCase();
		Assert.assertTrue(staffHandler.isEligible(staff, "frvidal", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "FrVidal", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "Frédéric VIDAL", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "VIDAL Frederic", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "frederic vidal", st));
		Assert.assertFalse(staffHandler.isEligible(staff, "frederic nope", st));
	}

	@Test
	public void testisEligibleNormalizedTwo() throws ApplicationException {
		Staff staff = new Staff(1, "Frederic", "VIDAL", "frvidal", "frvidal","frvidal@nope.com", "Gaulo-roman");
		StringTransform st = s -> Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "").toLowerCase();
		Assert.assertTrue(staffHandler.isEligible(staff, "frvidal", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "FrVidal", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "Frédéric VIDAL", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "VIDAL Frederic", st));
		Assert.assertTrue(staffHandler.isEligible(staff, "frederic vidal", st));
		Assert.assertFalse(staffHandler.isEligible(staff, "frederic nope", st));
	}

	@Test
	public void testisEligible() throws ApplicationException {
		Staff staff = new Staff(1, "Frederic", "VIDAL", "frvidal", "frvidal","frvidal@nope.com", "Gaulo-roman");
		Assert.assertTrue(staffHandler.isEligible(staff, "frvidal"));
		Assert.assertTrue(staffHandler.isEligible(staff, "FrVidal"));
		Assert.assertTrue(staffHandler.isEligible(staff, "  Frédéric VIDAL  "));
		Assert.assertTrue(staffHandler.isEligible(staff, "Frédéric VIDAL"));
		Assert.assertTrue(staffHandler.isEligible(staff, "VIDAL Frederic"));
		Assert.assertTrue(staffHandler.isEligible(staff, "VIDAL Frédéric"));
		Assert.assertTrue(staffHandler.isEligible(staff, "VIDAL"));
		Assert.assertTrue(staffHandler.isEligible(staff, "frederic vidal"));
		Assert.assertFalse(staffHandler.isEligible(staff, "frederic nope"));
	}

	@Test
	public void testIsEligibleForALoginWithTwoWords() throws ApplicationException {
		Staff staff = new Staff(1, "Frederic", "VIDAL", "frvidal", "Diogènes VLAID","frvidal@nope.com", "Gaulo-roman");
		Assert.assertTrue(staffHandler.isEligible(staff, "Diogenes VLAID"));
		Assert.assertTrue(staffHandler.isEligible(staff, "diogènes vlaid"));
		Assert.assertTrue(staffHandler.isEligible(staff, "  Diogènes Vlaid  "));
		Assert.assertFalse(staffHandler.isEligible(staff, "VLAID Diogènes"));
	}
	
	@Test
	public void testIsEligibleComposedFirstname() throws ApplicationException {
		Staff staff = new Staff(1, "Jean-Paul", "TWO", "jptwo", "jptwo","jptwo@nope.com", "Gaulo-roman");
		staff.setActive(false);
		Assert.assertTrue(staffHandler.isEligible(staff, "jptwo"));
		Assert.assertTrue(staffHandler.isEligible(staff, "Two Jean Paul"));
		Assert.assertTrue(staffHandler.isEligible(staff, "Two Jean-Paul"));
		Assert.assertTrue(staffHandler.isEligible(staff, "Jean-Paul TWO"));		
		Assert.assertFalse(staffHandler.isEligible(staff, "Jean-Paul THREE"));		
	}	
}
