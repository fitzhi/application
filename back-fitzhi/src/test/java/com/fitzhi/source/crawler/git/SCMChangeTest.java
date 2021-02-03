package com.fitzhi.source.crawler.git;

import org.junit.Assert;
import org.junit.Test;

public class SCMChangeTest {
    
	/**
	 * Testing the creation of the Author object
	 */
	@Test
	public void testAuthor() {
		SCMChange sc = new SCMChange("commitId", "fullPath", null, "Frédéric", "fv@email.com");
		Assert.assertEquals("Frédéric", sc.getAuthorName());
		Assert.assertEquals("fv@email.com", sc.getAuthorEmail());
	}

}
