package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.MapDetectedExperiences;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectionTemplate;
import com.fitzhi.data.internal.SkillDetectorType;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java_cup.runtime.lr_parser;

/**
 * This class tests the method {@link ProjectHandler#updateStaffExperiences()}
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerUpdateStaffExperiencesTest {
	
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

	@Test
	public void updateNominal() throws ApplicationException {
		
		when(dataHandler.loadChanges(any(Project.class))).thenReturn(new SourceControlChanges());

		Map<Integer, Project> allProjects = new HashMap<>();
		final Project pOne = new Project(1, "one");
		pOne.setActive(true);
		ProjectSkill psOne = new ProjectSkill(ID_SKILL_FILE_DETECTION);
		ProjectSkill psTwo = new ProjectSkill(ID_SKILL_NOT_FILE_DETECTION);
		Map<Integer, ProjectSkill> pOneSkills = new HashMap<>();
		pOneSkills.put(ID_SKILL_FILE_DETECTION, psOne);
		pOneSkills.put(ID_SKILL_NOT_FILE_DETECTION, psTwo);
		pOne.setSkills(pOneSkills);		
		allProjects.put(1, pOne);
		final Project pTwo = new Project(1, "one");
		pTwo.setActive(false);
		allProjects.put(2, pTwo);
		when(dataHandler.loadProjects()).thenReturn(allProjects);

		Map<Integer, Skill> allSkills = new HashMap<>();
		Skill skOne = new Skill(ID_SKILL_FILE_DETECTION, "Skill one", 
			new SkillDetectionTemplate(SkillDetectorType.FILENAME_DETECTOR_TYPE, "whocares$"));
		allSkills.put(ID_SKILL_FILE_DETECTION, skOne);
		Skill skTwo = new Skill(ID_SKILL_NOT_FILE_DETECTION, "Skill two", 
			new SkillDetectionTemplate(SkillDetectorType.PACKAGE_JSON_DETECTOR_TYPE, "whocares$"));
		allSkills.put(ID_SKILL_NOT_FILE_DETECTION, skTwo);
		when(dataHandler.loadSkills()).thenReturn(allSkills);

		Skill[] skills = { skOne };

		doNothing().when(ecosystemAnalyzer).calculateExperiences(
			pOne, 
			Arrays.asList(skills), 
			new SourceControlChanges(),
			new MapDetectedExperiences());	

		projectHandler.updateStaffExperiences();

		Mockito.verify(ecosystemAnalyzer, times(1)).calculateExperiences(
			pOne, 
			Arrays.asList(skills), 
			new SourceControlChanges(),
			new MapDetectedExperiences());	
	}
}
