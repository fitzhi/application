package fr.skiller.source.crawler.impl;

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
 * Test of the method {@link EcosystemAnalyzer#loadEcosystems()}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerLoadEcosystemsTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	@Test
	public void test() throws SkillerException {
		Map<Integer, Ecosystem> ecosystems = ecosystemAnalyzer.loadEcosystems();
		Assert.assertEquals(4, ecosystems.size());
	}
}
