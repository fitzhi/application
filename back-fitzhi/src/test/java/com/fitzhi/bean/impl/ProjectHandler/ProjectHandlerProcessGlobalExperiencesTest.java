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
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectionTemplate;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.StaffExperienceTemplate;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#processGlobalExperiences()}
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerProcessGlobalExperiencesTest {
	
	final int ID_SKILL_FILE_DETECTION = 1;
	final int ID_SKILL_NOT_FILE_DETECTION = 2;

	@Autowired
	ProjectHandler projectHandler;

	@MockBean
	EcosystemAnalyzer ecosystemAnalyzer;

	@MockBean
	StaffHandler staffHandler;

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

	@Test
	public void processGlobalExperiencesOK() throws ApplicationException {
		ProjectDetectedExperiences pde = new ProjectDetectedExperiences();
		pde.add(DetectedExperience.of(1, 2, new Author("frvidal", "frederic.vidal@fitzhi.com"), 10, -1));
		when(dataHandler.loadDetectedExperiences(any(Project.class))).thenReturn(pde);

		Staff staff = new Staff(1, "frvidal", "thepass");
		when(staffHandler.lookup(new Author("frvidal", "frederic.vidal@fitzhi.com"))).thenReturn(staff);
		when(dataHandler.loadProjects()).thenReturn(allProjects());

		Map<StaffExperienceTemplate, Integer> result = projectHandler.processGlobalExperiences();
		Assert.assertEquals(1, result.size());

		verify(dataHandler, times(1)).loadDetectedExperiences(any(Project.class));
	}

}
