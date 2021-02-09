package com.fitzhi.source.crawler.git;

import org.junit.Assert;
import org.junit.Test;

public class SourceChangeTest {

	/**
	 * Testing the creation of the Author object
	 */
	@Test
	public void testAuthor() {
		SourceChange sc = new SourceChange("commitId", null, "Frédéric", "fv@email.com");
		Assert.assertEquals("Frédéric", sc.getAuthor().getName());
		Assert.assertEquals("fv@email.com", sc.getAuthor().getEmail());
	}
	
}
