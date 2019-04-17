/**
 * 
 */
package fr.skiller.bean.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

import static org.junit.Assume.assumeTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.util.Files;
import org.junit.After;
import org.junit.Assert;
/**
 * Test the administration bean for the user creation.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AdministrationCreateUserTest {

	private static final String MY_LOGIN = "myLogin";

	@Autowired
	Administration administration;

	@Autowired
	StaffHandler staffHandler;
		
	private final Logger logger = LoggerFactory.getLogger(AdministrationCreateUserTest.class.getCanonicalName());

	int idStaff;
	
	@Test
	public void testCreateUser() throws SkillerException {
		final Staff staff = administration.createNewUser(MY_LOGIN, "myPassword");
		idStaff = staff.getIdStaff();
		Assert.assertNotNull(
				"Staff entry has been created", 
				staff);
		Assert.assertTrue(
				"Staff entry has been created", 
				staff.getIdStaff() > 0);
		Assert.assertEquals(
				"Staff entry has been CORRECTLY created", 
				MY_LOGIN,
				staff.getLogin());
		Assert.assertNotNull(
				"Staff entry has been CORRECTLY created", 
				staff.getMissions());
	}

	@Test
	public void testCreateExistingUser()  {

		try {
			final Staff staff = administration.createNewUser("frvidal", "myPassword");
			idStaff = staff.getIdStaff();
			Assert.fail ("Exception is missing because the login 'frvidal' already exists.");
		} catch (final SkillerException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						String.format("exception has been correctly thrown with message %s", e.getMessage()));
			}
		}
	}
	
	@After
	public void after() {
		staffHandler.getStaff().remove(idStaff);
	}
}
