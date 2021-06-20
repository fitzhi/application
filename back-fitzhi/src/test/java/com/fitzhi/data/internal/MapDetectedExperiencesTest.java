package com.fitzhi.data.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the class {@link MapDetectedExperiences}.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class MapDetectedExperiencesTest {

	@Test
	public void key() {
		String key = MapDetectedExperiences.key(DetectedExperience.of(1, 1789, new Author("name", "email@nope.com")));
		Assert.assertEquals("name-email@nope.com-1", key);
	}

	@Test
	public void addNewRecord() {
		MapDetectedExperiences map = MapDetectedExperiences.of();
		DetectedExperience de = map.inc(DetectedExperience.of(1, 1789, new Author("name", "email@nope.com")));
		Assert.assertEquals(1, de.getCount());
	}

	@Test
	public void add2TimesSameRecord() {
		MapDetectedExperiences map = MapDetectedExperiences.of();
		DetectedExperience de = map.inc(DetectedExperience.of(1, 1789, new Author("name", "email@nope.com")));
		Assert.assertEquals(1, de.getCount());
		de = map.inc(DetectedExperience.of(1, 1789, new Author("name", "email@nope.com")));
		Assert.assertEquals(2, de.getCount());
	}

	@Test
	public void getNominal() {
		MapDetectedExperiences map = MapDetectedExperiences.of();
		map.inc(DetectedExperience.of(1, 1789, new Author("name", "email@nope.com")));
		DetectedExperience de = map.get(DetectedExperience.of(1, 1914, new Author("name", "email@nope.com")));
		Assert.assertNotNull(de);
		Assert.assertEquals(1, de.getCount());
		// The project is not part of the key, at this level.
		Assert.assertEquals(1789, de.getIdProject());
	}
}
