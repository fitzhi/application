package com.fitzhi.source.crawler.impl;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.javaparser.ExperienceParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#loadExperienceParsers(String)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerLoadExperienceParsersTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	private Project project;
	
	@Before
	public void before() {
		this.project = new Project(1789, "The F R");
		this.project.setLocationRepository("locationRepository");
	}
	
	@Test
	public void loadNominal() throws ApplicationException {
		final ExperienceParser[] ep = ecosystemAnalyzer.loadExperienceParsers(this.project, ".java$");
		Assert.assertEquals(1, ep.length);
	}

	@Test
	public void loadUnknownFilePattern() throws ApplicationException {
		final ExperienceParser[] ep = ecosystemAnalyzer.loadExperienceParsers(this.project, ".unknown$");
		Assert.assertEquals(0, ep.length);
	}

}