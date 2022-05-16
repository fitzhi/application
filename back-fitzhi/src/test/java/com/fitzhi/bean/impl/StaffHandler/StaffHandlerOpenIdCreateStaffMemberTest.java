package com.fitzhi.bean.impl.StaffHandler;

import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.OpenIdToken;
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
 * Testing the method {@link StaffHandler#createStaffMember(com.fitzhi.data.internal.OpenIdToken)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerOpenIdCreateStaffMemberTest {

	@Autowired
	private StaffHandler staffHandler;

	@Before
	public void before() {
		//
		// We cannot mock the method nextIdStaff() because this method is located on the same bean as createEmptyStaff
		//
		// We cleanup all staff entries with an id lower than 1788 
		// it looks like some tests keep data in the staff collection when execeting the tests on some IC
		staffHandler.getStaff().entrySet().removeIf(e -> e.getKey() > 1788);
		staffHandler.getStaff().put(1788, new Staff(1788, "login", "password"));
	}

	@Test
	public void testSimpleCreation() throws ApplicationException {
				
		OpenIdToken oit = OpenIdToken.of();
		oit.setServerId(GOOGLE_OPENID_SERVER);
		oit.setUserId("userId...");
		oit.setFamilyName("VIDAL");
		oit.setGivenName("Frederic");
		oit.setName("Frédéric VIDAL");
		oit.setEmail("frederic.vidal@nope.com");
		oit.setLogin("myLogin");

		Staff st = staffHandler.createStaffMember(oit);
		
		Assert.assertEquals(1789, st.getIdStaff());
		Assert.assertEquals("VIDAL", st.getLastName());
		Assert.assertEquals("Frederic", st.getFirstName());
		Assert.assertNull(st.getNickName());
		Assert.assertEquals("myLogin", st.getLogin());
		Assert.assertEquals("frederic.vidal@nope.com", st.getEmail());

		Assert.assertEquals(1, st.getOpenIds().size());
		Assert.assertEquals(GOOGLE_OPENID_SERVER, st.getOpenIds().get(0).getServerId());
		Assert.assertEquals("userId...", st.getOpenIds().get(0).getUserId());

		Assert.assertEquals(1789, st.getIdStaff());
		staffHandler.removeStaff(1789);
	}


	@After
	public void after() {
		staffHandler.removeStaff(1788);
		staffHandler.removeStaff(1789);
	}
}
