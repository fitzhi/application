/**
 * 
 */
package com.fitzhi.bean.impl.Administration;


import com.fitzhi.bean.Administration;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * Test the administration bean for the user creation.<br/>
 * <span style="color:red;font-size:16">IF IT IS NOT THE VERY FIRST CONNECTION !</span>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdministrationConnectTest {

	private static final String MY_PSSWORD = "myPassword";

	private static final String MY_LOGIN = "myLogin";

	@Autowired
	Administration administration;

	@Autowired
	StaffHandler staffHandler;
		

	int idStaff;

	@Before
	public void before() throws ApplicationException {
		final Staff staff = administration.createNewUser(MY_LOGIN, MY_PSSWORD);
		this.idStaff = staff.getIdStaff();
	}

	@Test 
	public void testConnectOk() throws ApplicationException {
		Staff staff = administration.connect(MY_LOGIN, MY_PSSWORD);
		Assert.assertEquals (idStaff, staff.getIdStaff());
	}

	@Test (expected=ApplicationException.class)
	public void testConnectKo() throws ApplicationException {
		administration.connect(MY_LOGIN, "badPass");
	}
	
	@After
	public void after() {
		staffHandler.getStaff().remove(idStaff);
 	}
}
