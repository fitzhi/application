package com.fitzhi.source.crawler.impl;

import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#calculateExperiences(com.fitzhi.data.internal.Project, java.util.List, com.fitzhi.data.internal.SourceControlChanges, com.fitzhi.data.internal.MapDetectedExperiences) calculateExperiences(...)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerCalculateExperiencesTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	/**
	 * Nominal behavior
	 */
	@Test
	public void calculateExperiencesOk() {

	}
}