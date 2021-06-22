package com.fitzhi.source.crawler.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceCodeDiffChange;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.fitzhi.data.internal.ProjectDetectedExperiences.key;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#calculateExperiences(com.fitzhi.data.internal.Project, java.util.List, com.fitzhi.data.internal.SourceControlChanges, com.fitzhi.data.internal.ProjectDetectedExperiences) calculateExperiences(...)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerCalculateExperiencesTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	SkillHandler skillHandler;

	/**
	 * Nominal behavior
	 */
	@Test
	public void calculateExperiencesOneFileOk() throws ApplicationException {
		Project project = new Project(570, "The prophet birth");
		project.setActive(true);
		ProjectDetectedExperiences experiences = new ProjectDetectedExperiences();
		Skill[] aSkills =  { skillHandler.getSkill(1) };
		List<Skill> skills = Arrays.asList( aSkills ); 
		SourceControlChanges scc = new SourceControlChanges();
		// ONE LINE
		SourceChange sc = new SourceChange("cmt 1", LocalDate.of(2021, 2, 8), "frvidal", "frederic.vidal@fitzhi.com");
		sc.setDiff(new SourceCodeDiffChange("fr/test/MyTest.java", 0, 5));
		scc.addChange("fr/test/MyTest.java", sc);
		ecosystemAnalyzer.calculateExperiences(project, skills, scc, experiences);
		Assert.assertEquals(1, experiences.content().size());
		DetectedExperience de = experiences.content().iterator().next();
		Assert.assertEquals(2, de.getIdExperienceDetectionTemplate());
		Assert.assertEquals(570, de.getIdProject());
		Assert.assertEquals(5, de.getCount());
	}

	/**
	 * Nominal behavior
	 */
	@Test
	public void calculateExperiencesMultipleFilesOk() throws ApplicationException {
		Project project = new Project(570, "The prophet birth");
		project.setActive(true);
		ProjectDetectedExperiences experiences = new ProjectDetectedExperiences();
		Skill[] aSkills =  { skillHandler.getSkill(1) };
		List<Skill> skills = Arrays.asList( aSkills ); 
		SourceControlChanges scc = new SourceControlChanges();

		// Multiple source files
		SourceChange sc = new SourceChange("cmt 1", LocalDate.of(2021, 2, 8), "frvidal", "frederic.vidal@fitzhi.com");		
		sc.setDiff(new SourceCodeDiffChange("fr/test/MyTest.java", 0, 5));
		scc.addChange("fr/test/MyTest.java", sc);

		sc = new SourceChange("cmt 2", LocalDate.of(2021, 2, 13), "frvidal", "frederic.vidal@fitzhi.com");		
		sc.setDiff(new SourceCodeDiffChange("fr/test/MyTest.java", 0, 10));
		scc.addChange("fr/test/MyTest.java", sc);

		sc = new SourceChange("cmt 3", LocalDate.of(2021, 3, 20), "myUser", "myUser@gmail.com");		
		sc.setDiff(new SourceCodeDiffChange("fr/test/MyNopeTest.java", 3, 12));
		scc.addChange("fr/test/MyNopeTest.java", sc);

		sc = new SourceChange("cmt 4", LocalDate.of(2021, 3, 20), "myUser", "myUser@gmail.com");		
		sc.setDiff(new SourceCodeDiffChange("fr/test/evicted.js", 3, 12));
		scc.addChange("fr/test/evicted.js", sc);

		sc = new SourceChange("cmt 5", LocalDate.of(2021, 3, 20), "myUser", "myUser@gmail.com");		
		sc.setDiff(new SourceCodeDiffChange("fr/test.java/evicted.js", 3, 12));
		scc.addChange("fr/test.java/evicted.js", sc);

		ecosystemAnalyzer.calculateExperiences(project, skills, scc, experiences);
		Assert.assertEquals(2, experiences.content().size());

		DetectedExperience de = experiences.get(key(2, new Author("frvidal", "frederic.vidal@fitzhi.com")));
		Assert.assertEquals(2, de.getIdExperienceDetectionTemplate());
		Assert.assertEquals(570, de.getIdProject());
		Assert.assertEquals(15, de.getCount());
		Assert.assertEquals("frvidal", de.getAuthor().getName());
		Assert.assertEquals("frederic.vidal@fitzhi.com", de.getAuthor().getEmail());

		de = experiences.get(key(2, new Author("myUser", "myUser@gmail.com")));
		Assert.assertEquals(2, de.getIdExperienceDetectionTemplate());
		Assert.assertEquals(570, de.getIdProject());
		Assert.assertEquals(9, de.getCount());
		Assert.assertEquals("myUser", de.getAuthor().getName());
		Assert.assertEquals("myuser@gmail.com", de.getAuthor().getEmail());


	}


}