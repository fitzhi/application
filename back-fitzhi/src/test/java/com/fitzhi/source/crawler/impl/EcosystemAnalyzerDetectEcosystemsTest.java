package com.fitzhi.source.crawler.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#detectEcosystems()}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerDetectEcosystemsTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	@Test
	public void test() throws ApplicationException {
		List<String> pathnames = new ArrayList<>();
		pathnames.add("One.ts");
		pathnames.add("Two.ts");
		pathnames.add("One.java");
		pathnames.add("one/one.ts");		
		pathnames.add("unused.json");
		List<Ecosystem> ecosystems = ecosystemAnalyzer.detectEcosystems(pathnames);
		Assert.assertEquals(2, ecosystems.size());
		Assert.assertEquals("TypeScript detected", 2, ecosystems.get(0).getId());
		Assert.assertEquals("Java detected",0, ecosystems.get(1).getId());		
	}

	@Test
	public void testPHP() throws ApplicationException {
		List<String> pathnames = new ArrayList<>();
		pathnames.add("/application/Classes/AbstractException.php");
		pathnames.add("/application/Classes/GroupGile.php");
		pathnames.add("unused.json");
		List<Ecosystem> ecosystems = ecosystemAnalyzer.detectEcosystems(pathnames);
		Assert.assertEquals(1, ecosystems.size());
		Assert.assertEquals("PHP detected", 5, ecosystems.get(0).getId());
	}
}
