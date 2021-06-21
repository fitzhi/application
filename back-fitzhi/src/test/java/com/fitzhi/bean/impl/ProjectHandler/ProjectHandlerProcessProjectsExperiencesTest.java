package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.ArgumentMatchers.any;
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#processProjectsExperiences()}
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
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
		projectActive = new Project(1, "one");
		projectActive.setActive(true);
		ProjectSkill psOne = new ProjectSkill(ID_SKILL_FILE_DETECTION);
		ProjectSkill psTwo = new ProjectSkill(ID_SKILL_NOT_FILE_DETECTION);
		Map<Integer, ProjectSkill> pOneSkills = new HashMap<>();
		pOneSkills.put(ID_SKILL_FILE_DETECTION, psOne);
		pOneSkills.put(ID_SKILL_NOT_FILE_DETECTION, psTwo);
		projectActive.setSkills(pOneSkills);		
		allProjects.put(1, projectActive);
		final Project pTwo = new Project(1, "one");
		pTwo.setActive(false);
		allProjects.put(2, pTwo);
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
	public void calculateExperiencesOK() throws ApplicationException {
		
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

		verify(dataHandler, times(1)).saveDetectedExperiences(any(Project.class), any(ProjectDetectedExperiences.class));
	}

	@Test
	public void calculateExperiencesNoChangesFile() throws ApplicationException {
		
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

		verify(dataHandler, never()).saveDetectedExperiences(any(Project.class), any(ProjectDetectedExperiences.class));
	}

	@Test(expected = ApplicationException.class)
	public void calculateExperiencesKO() throws ApplicationException {
		
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
	}

}
