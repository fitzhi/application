package com.fitzhi.util;

import org.junit.Assert;
import org.junit.Test;

import com.fitzhi.ApplicationRuntimeException;

public class CommonUtilTest {
	
	@Test
	public void extractProjectFromUrlDotGit() {
		Assert.assertEquals("spring-framework", CommonUtil.extractProjectNameFromUrl("https://github.com/spring-projects/spring-framework.git"));
	}

	@Test
	public void extractProjectFromUrl() {
		Assert.assertEquals("spring-framework", CommonUtil.extractProjectNameFromUrl("https://github.com/spring-projects/spring-framework"));
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void extractInvalidUrl() {
		CommonUtil.extractProjectNameFromUrl("https:||github.com|spring-projects|spring-framework");
	}
}
