package com.fitzhi.source.crawler.impl;

import java.util.List;

import com.fitzhi.data.internal.ExperienceAbacus;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#loadExperienceAbacus()}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerLoadExperienceAbacusTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	@Test
	public void load() throws ApplicationException {
		List<ExperienceAbacus> abacus = ecosystemAnalyzer.loadExperienceAbacus();
		Assert.assertEquals(2, abacus.size());
		Assert.assertEquals(0, abacus.get(0).getIdAbacus());
		Assert.assertEquals(1, abacus.get(0).getIdExperienceDetectionTemplate());
		Assert.assertEquals(2000, abacus.get(0).getValue());
		Assert.assertEquals(1, abacus.get(0).getLevel());
	}

}
