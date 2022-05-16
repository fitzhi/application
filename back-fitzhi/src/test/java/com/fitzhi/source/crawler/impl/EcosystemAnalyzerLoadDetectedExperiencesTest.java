package com.fitzhi.source.crawler.impl;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import com.fitzhi.OSType;

import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#loadExperienceParsers(String)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Slf4j
public class EcosystemAnalyzerLoadDetectedExperiencesTest {

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

		// We disable this test for Windows.
		// Javaparser does not worl correctly on Windows platform.
		if (OSType.DETECTED != OSType.Windows) {
			ProjectDetectedExperiences map = ProjectDetectedExperiences.of();
			final ExperienceParser[] parsers = this.ecosystemAnalyzer.loadExperienceParsers(this.project, ".java$");
			ecosystemAnalyzer.loadDetectedExperiences(this.project, map, parsers);
	
			DetectedExperience de = map.get(DetectedExperience.of(0, 0, new Author("frvidal", "frederic.vidal.perso@gmail.com")));
			Assert.assertEquals(10, de.getCount());
	
			de = map.get(DetectedExperience.of(1, 0, new Author("frvidal", "frederic.vidal.perso@gmail.com")));
			if (log.isDebugEnabled()) {
				map.content().forEach(item -> log.debug(item.toString()));
			}
			Assert.assertEquals(6, de.getCount());
	
			de = map.get(DetectedExperience.of(0, 0, new Author("fitzhi", "frederic.vidal@fitzhi.com")));
			Assert.assertEquals(3, de.getCount());
	
			de = map.get(DetectedExperience.of(0, 0, new Author("Frédéric", "frederic.vidal.perso@gmail.com")));
			Assert.assertEquals(5, de.getCount());
		}
	}
}