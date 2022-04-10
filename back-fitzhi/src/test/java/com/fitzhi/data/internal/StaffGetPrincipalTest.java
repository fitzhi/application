package com.fitzhi.data.internal;

import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;

import org.junit.Assert;
import org.junit.Test;
/**
 * Test of the method {@link Staff#getPrincipal(String)}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class StaffGetPrincipalTest {
	
	@Test
	public void found() {
		Staff staff = new Staff ();
		staff.getOpenIds().add(OpenId.of(GOOGLE_OPENID_SERVER, "myGooglePrincipal"));
		String result = staff.getPrincipal(GOOGLE_OPENID_SERVER);
		Assert.assertEquals("myGooglePrincipal", result);
	}

	@Test
	public void notFound() {
		Staff staff = new Staff ();
		staff.getOpenIds().add(OpenId.of(GOOGLE_OPENID_SERVER, "myGooglePrincipal"));
		Assert.assertNull(staff.getPrincipal("???"));
	}

	@Test
	public void empty() {
		Staff staff = new Staff ();
		Assert.assertNull(staff.getPrincipal(GOOGLE_OPENID_SERVER));
	}
}
