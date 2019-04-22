/**
 * 
 */
package fr.skiller.bean.impl;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.Administration;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
/**
 * Test the administration bean for the user creation.<br/>
 * <span style="color:red;font-size:16">IF IT IS NOT THE VERY FIRST CONNECTION !</span>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdministrationCreateUserIfIsVeryFirstConnectionTest {

	private static final String MY_LOGIN = "myLogin";

	@Autowired
	Administration administration;

	@Autowired
	StaffHandler staffHandler;
		

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

	@After
	public void after() {
		staffHandler.getStaff().remove(idStaff);
 	}
}
