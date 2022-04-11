package com.fitzhi.bean.impl.StaffHandler;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the method {@link StaffHandler#lookup(com.fitzhi.data.internal.OpenId)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerOpenIdLookupTest {
	
	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() throws ApplicationException {
		Staff staff1 = new Staff(1789, "firstName", "lastName", "nickName", "login", "email", "level");
		staff1.getOpenIds().add(OpenId.of("GOOGLE", "openIdGoogleOne"));
		staff1.getOpenIds().add(OpenId.of("GITHUB", "openIdGithubOne"));
		Staff staff2 = new Staff(1790, "firstName", "lastName", "nickName", "login", "email", "level");
		staff2.getOpenIds().add(OpenId.of("GOOGLE", "openIdGithuOne"));

		staffHandler.getStaff().put(1789, staff1);
		staffHandler.getStaff().put(1790, staff2);
	}

	@Test
	public void found() throws ApplicationException {
		final Staff staff = staffHandler.lookup(OpenId.of ("GITHUB", "openIdGithubOne"));
		Assert.assertNotNull(staff);
		Assert.assertEquals(1789, staff.getIdStaff());
	}

	@Test
	public void notfound() throws ApplicationException {
		final Staff staff = staffHandler.lookup(OpenId.of ("GITHUB", "n/a"));
		Assert.assertNull(staff);
	}

	@Test(expected = ApplicationException.class)
	public void exception() throws ApplicationException {
		// We'll have the same openId on 2 differents staff
		staffHandler.getStaff(1790).getOpenIds().add(OpenId.of("GITHUB", "openIdGithubOne"));

		staffHandler.lookup(OpenId.of ("GITHUB", "openIdGithubOne"));
	}

	@After
	public void after() {
		staffHandler.removeStaff(1789);
		staffHandler.removeStaff(1790);
	}
}
