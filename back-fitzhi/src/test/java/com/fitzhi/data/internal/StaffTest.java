package com.fitzhi.data.internal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the class {@link Staff}
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
public class StaffTest {
 
	@Test
	public void constuctor1() {
		Staff staff = new Staff(1789, "frvidal", "nope");
		Assert.assertEquals(1789, staff.getIdStaff());
		Assert.assertEquals("frvidal", staff.getLogin());
		Assert.assertEquals("nope", staff.getPassword());
		Assert.assertNotNull(staff.getMissions());
		Assert.assertNotNull(staff.getAuthorities());
		Assert.assertEquals(1, staff.getAuthorities().size());
		Assert.assertNotNull(staff.getExperiences());
		Assert.assertNotNull(staff.getOpenIds());
	}

	@Test
	public void constuctor2() {
		Staff staff = new Staff(1789, "Frédéric", "VIDAL", "frvidal", "frvidal", "frvidal@email.com", "level");
		Assert.assertEquals(1789, staff.getIdStaff());
		Assert.assertEquals("Frédéric", staff.getFirstName());
		Assert.assertEquals("VIDAL", staff.getLastName());
		Assert.assertEquals("frvidal", staff.getNickName());
		Assert.assertEquals("frvidal", staff.getLogin());
		Assert.assertEquals("frvidal@email.com", staff.getEmail());
		Assert.assertEquals("level", staff.getLevel());
		Assert.assertTrue(staff.isActive());
		Assert.assertFalse(staff.isExternal());
		Assert.assertNotNull(staff.getMissions());
		Assert.assertNotNull(staff.getAuthorities());
		Assert.assertEquals(1, staff.getAuthorities().size());
		Assert.assertNotNull(staff.getExperiences());
		Assert.assertNotNull(staff.getOpenIds());
	}

	@Test
	public void constuctor3() {
		Staff staff = new Staff(1789, "Frédéric", "VIDAL", "frvidal", "frvidal", "frvidal@email.com", "level", false, true);
		Assert.assertEquals(1789, staff.getIdStaff());
		Assert.assertEquals("Frédéric", staff.getFirstName());
		Assert.assertEquals("VIDAL", staff.getLastName());
		Assert.assertEquals("frvidal", staff.getNickName());
		Assert.assertEquals("frvidal", staff.getLogin());
		Assert.assertEquals("frvidal@email.com", staff.getEmail());
		Assert.assertEquals("level", staff.getLevel());
		Assert.assertFalse(staff.isActive());
		Assert.assertTrue(staff.isExternal());
		Assert.assertNotNull(staff.getMissions());
		Assert.assertNotNull(staff.getAuthorities());
		Assert.assertEquals(1, staff.getAuthorities().size());
		Assert.assertNotNull(staff.getExperiences());
		Assert.assertNotNull(staff.getOpenIds());
	}

}
