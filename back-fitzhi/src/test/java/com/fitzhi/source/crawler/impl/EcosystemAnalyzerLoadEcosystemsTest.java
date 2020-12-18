package com.fitzhi.source.crawler.impl;

import java.util.Map;

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
	public void test() throws ApplicationException {
		Map<Integer, Ecosystem> ecosystems = ecosystemAnalyzer.loadEcosystems();
		Assert.assertEquals(9, ecosystems.size());
	}
}
