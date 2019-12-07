package fr.skiller.bean.impl;


import java.text.Normalizer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;


/**
 * <p>
 * Testing the methods
 * <ul>
 * <li>
 * {@link StaffHandler#isEligible(fr.skiller.data.internal.Staff, String, StringTransform)},
 * </li><li>
 * {@link StaffHandler#isEligible(fr.skiller.data.internal.Staff, String) }
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
	public void testisEligibleLowerCase() throws SkillerException {
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
	public void testisEligibleNormalizedOne() throws SkillerException {
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
	public void testisEligibleNormalizedTwo() throws SkillerException {
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
	public void testisEligible() throws SkillerException {
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
	
}
