package fr.skiller.source.crawler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.data.internal.Ecosystem;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.EcosystemAnalyzer;

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
	public void test() throws SkillerException {
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
}
