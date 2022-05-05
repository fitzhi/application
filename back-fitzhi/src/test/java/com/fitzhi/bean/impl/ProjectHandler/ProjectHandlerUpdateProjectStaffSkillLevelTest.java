package com.fitzhi.bean.impl.ProjectHandler;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.StaffExperienceTemplate;
import com.fitzhi.exception.ApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#addSkill(Project, ProjectSkill)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerUpdateProjectStaffSkillLevelTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "the French revolution");
	}
	
	/**
	 * No error when the experiences list is empty.
	 * @throws ApplicationException
	 */
	@Test
	public void empty() throws ApplicationException {
		Map<StaffExperienceTemplate, Integer> experiences = new HashMap<StaffExperienceTemplate, Integer>();
		projectHandler.updateProjectStaffSkillLevel(experiences);
	}
}
