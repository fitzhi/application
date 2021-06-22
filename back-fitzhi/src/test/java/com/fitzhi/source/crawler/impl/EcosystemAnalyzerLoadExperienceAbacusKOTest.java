package com.fitzhi.source.crawler.impl;

import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the failure of method {@link EcosystemAnalyzer#loadExperienceAbacus()}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "referential.dir=nonExistingPath" }) 
public class EcosystemAnalyzerLoadExperienceAbacusKOTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	@Test (expected = ApplicationException.class)
	public void loadKO() throws ApplicationException {
		ecosystemAnalyzer.loadExperienceAbacus();
	}

}
