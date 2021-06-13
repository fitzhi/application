package com.fitzhi.data.internal;

import org.apache.commons.digester.plugins.Declaration;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the class {@link DetectedExperience}
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class DetectedExperienceTest {

	@Test
	public void testInstanciation() {
		final DetectedExperience de = DetectedExperience.of(1, new Author("name", "email"));
		Assert.assertEquals(-1, de.getIdStaff());
		Assert.assertEquals(0, de.getCount());
	}
	
	@Test
	public void testInc() {
		final DetectedExperience de = DetectedExperience.of(1, new Author("name", "email"));
		final int r = de.inc();
		Assert.assertEquals(1, r);
		Assert.assertEquals(1, de.getCount());

	}
	
}
