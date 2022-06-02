package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectionTemplate;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.javaparser.ExperienceParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#processProjectsExperiences()}
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ProjectHandlerProcessProjectsExperiencesTest {
	
	final int ID_SKILL_FILE_DETECTION = 1;
	final int ID_SKILL_NOT_FILE_DETECTION = 2;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	SkillHandler skillHandler;

	@MockBean
	EcosystemAnalyzer ecosystemAnalyzer;

	@MockBean
	DataHandler dataHandler;

	private Project projectActive;

	private Skill skillFileDetection;

	private Map<Integer, Project> allProjects() {
		Map<Integer, Project> allProjects = new HashMap<>();

		// An active project with a local repository should be included.
		projectActive = new Project(1, "one");
		projectActive.setActive(true);
		projectActive.setLocationRepository("locationRepository");
		ProjectSkill psOne = new ProjectSkill(ID_SKILL_FILE_DETECTION);
		ProjectSkill psTwo = new ProjectSkill(ID_SKILL_NOT_FILE_DETECTION);
		Map<Integer, ProjectSkill> pOneSkills = new HashMap<>();
		pOneSkills.put(ID_SKILL_FILE_DETECTION, psOne);
		pOneSkills.put(ID_SKILL_NOT_FILE_DETECTION, psTwo);
		projectActive.setSkills(pOneSkills);		
		allProjects.put(1, projectActive);
		
		// An INACTIVE project should be repository should be ignored.
		final Project pTwo = new Project(2, "Second project");
		pTwo.setActive(false);
		allProjects.put(2, pTwo);

		// An active project WITHOUT LOCAL REPOSITORY should be ignored.
		final Project pThree = new Project(3, "Third project");
		pThree.setActive(true);
		allProjects.put(3, pThree);

		return allProjects;
	}

	private Map<Integer, Skill> allSkills() {
		Map<Integer, Skill> allSkills = new HashMap<>();
		skillFileDetection = new Skill(ID_SKILL_FILE_DETECTION, "Skill one", 
			new SkillDetectionTemplate(SkillDetectorType.FILENAME_DETECTOR_TYPE, "whocares$"));
		allSkills.put(ID_SKILL_FILE_DETECTION, skillFileDetection);
		Skill skTwo = new Skill(ID_SKILL_NOT_FILE_DETECTION, "Skill two", 
			new SkillDetectionTemplate(SkillDetectorType.PACKAGE_JSON_DETECTOR_TYPE, "whocares$"));
		allSkills.put(ID_SKILL_NOT_FILE_DETECTION, skTwo);
		return allSkills;
	}


	@Test
	public void calculateExperiences() throws ApplicationException {
		
		when(ecosystemAnalyzer.loadExperienceParsers(any(Project.class), anyString())).thenReturn(new ExperienceParser[0]);
		doNothing().when(ecosystemAnalyzer).loadDetectedExperiences(projectActive, ProjectDetectedExperiences.of(), new ExperienceParser[0]);

		when(dataHandler.loadChanges(any(Project.class))).thenReturn(new SourceControlChanges());
		when(dataHandler.loadProjects()).thenReturn(allProjects());
		when(dataHandler.loadSkills()).thenReturn(allSkills());
		doNothing().when(dataHandler).saveDetectedExperiences(any(Project.class), any(ProjectDetectedExperiences.class));
		
		Skill[] skills = { skillFileDetection };

		doNothing().when(ecosystemAnalyzer).calculateExperiences(
			projectActive, 
			Arrays.asList(skills), 
			new SourceControlChanges(),
			new ProjectDetectedExperiences());	

		projectHandler.processProjectsExperiences();

		verify(ecosystemAnalyzer, times(1)).calculateExperiences(
			projectActive, 
			Arrays.asList(skills), 
			new SourceControlChanges(),
			new ProjectDetectedExperiences());	

		verify(dataHandler, atLeastOnce()).saveDetectedExperiences(any(Project.class), any(ProjectDetectedExperiences.class));
		verify(ecosystemAnalyzer, times(1)).loadExperienceParsers(any(Project.class), anyString());
		verify(ecosystemAnalyzer, times(1)).loadDetectedExperiences(projectActive, ProjectDetectedExperiences.of(), new ExperienceParser[0]);
	}

	@Test
	public void calculateExperiencesNoChangesFile() throws ApplicationException {
		
		when(ecosystemAnalyzer.loadExperienceParsers(any(Project.class), anyString())).thenReturn(new ExperienceParser[0]);
		doNothing().when(ecosystemAnalyzer).loadDetectedExperiences(projectActive, ProjectDetectedExperiences.of(), new ExperienceParser[0]);

		when(dataHandler.loadChanges(any(Project.class))).thenReturn(null);
		when(dataHandler.loadProjects()).thenReturn(allProjects());
		when(dataHandler.loadSkills()).thenReturn(allSkills());
		
		projectHandler.processProjectsExperiences();
		
		Skill[] skills = { skillFileDetection };
		verify(ecosystemAnalyzer, never()).calculateExperiences(
			projectActive, 
			Arrays.asList(skills), 
			new SourceControlChanges(),
			new ProjectDetectedExperiences());

		verify(ecosystemAnalyzer, times(1)).loadExperienceParsers(any(Project.class), anyString());
		verify(ecosystemAnalyzer, times(1)).loadDetectedExperiences(projectActive, ProjectDetectedExperiences.of(), new ExperienceParser[0]);
	}

	@Test(expected = ApplicationException.class)
	public void calculateExperiencesKO() throws ApplicationException {
		
		when(ecosystemAnalyzer.loadExperienceParsers(any(Project.class), anyString())).thenReturn(new ExperienceParser[0]);
		doNothing().when(ecosystemAnalyzer).loadDetectedExperiences(projectActive, ProjectDetectedExperiences.of(), new ExperienceParser[0]);

		when(dataHandler.loadChanges(any(Project.class))).thenThrow(ApplicationException.class);
		when(dataHandler.loadProjects()).thenReturn(allProjects());
		when(dataHandler.loadSkills()).thenReturn(allSkills());
		
		projectHandler.processProjectsExperiences();
		
		Skill[] skills = { skillFileDetection };
		verify(ecosystemAnalyzer, never()).calculateExperiences(
			projectActive, 
			Arrays.asList(skills), 
			new SourceControlChanges(),
			new ProjectDetectedExperiences());

		verify(dataHandler, never()).saveDetectedExperiences(any(Project.class), any(ProjectDetectedExperiences.class));
		verify(ecosystemAnalyzer, times(1)).loadExperienceParsers(any(Project.class), anyString());
		verify(ecosystemAnalyzer, times(1)).loadDetectedExperiences(projectActive, ProjectDetectedExperiences.of(), new ExperienceParser[0]);
	}

}
