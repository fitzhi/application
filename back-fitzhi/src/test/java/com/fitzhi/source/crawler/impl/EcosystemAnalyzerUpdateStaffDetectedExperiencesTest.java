package com.fitzhi.source.crawler.impl;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.javaparser.ExperienceParser;

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
public class EcosystemAnalyzerUpdateStaffDetectedExperiencesTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	private Project project;
	
	@Before
	public void before() throws IOException {
		this.project = new Project(1789, "My Fitzhi project");
		this.project.setLocationRepository(Paths.get("../git_repo_for_test/application").toRealPath(LinkOption.NOFOLLOW_LINKS).toString());
	}
	
	@Test
	public void load() throws ApplicationException {
		final ExperienceParser[] parsers = this.ecosystemAnalyzer.loadExperienceParsers(this.project, ".java$");
		ecosystemAnalyzer.updateStaffDetectedExperiences(this.project, parsers);
	}
}