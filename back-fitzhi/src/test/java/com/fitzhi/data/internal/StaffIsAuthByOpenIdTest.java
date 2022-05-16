package com.fitzhi.data.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the method {@link Staff#isAuthByOpenId(String, String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class StaffIsAuthByOpenIdTest {
	
	@Test
	public void found() {
		Staff st = new Staff();
		st.getOpenIds().add(OpenId.of("server2", "myOpenId2"));
		st.getOpenIds().add(OpenId.of("server", "myOpenId"));
		Assert.assertTrue(st.isAuthByOpenId("server", "myOpenId"));
	}

	@Test
	public void notFound1() {
		Staff st = new Staff();
		st.getOpenIds().add(OpenId.of("server", "myOpenIdAlternate"));
		Assert.assertFalse(st.isAuthByOpenId("server", "myOpenId"));
	}

	@Test
	public void notFound2() {
		Staff st = new Staff();
		st.getOpenIds().add(OpenId.of("serverAlternate", "myOpenId"));
		Assert.assertFalse(st.isAuthByOpenId("server", "myOpenId"));
	}

	@Test
	public void empty() {
		Staff st = new Staff();
		Assert.assertFalse(st.isAuthByOpenId("server", "myOpenId"));
	}
}
