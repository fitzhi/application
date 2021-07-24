package com.fitzhi.bean.impl.ProjectHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.exception.ApplicationException;

/**
 * This class tests the method {@link ProjectHandler#addSkill(Project, ProjectSkill)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerAddSkillTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "my testing project");
		projectHandler.addSkill(project, new ProjectSkill(1, 10, 0));
	}
	
	@Test
	public void addExistingProjectSkill() throws ApplicationException {
		projectHandler.addSkill(project, new ProjectSkill(1, 1, 0));
		ProjectSkill ps = project.getSkills().get(1);
		Assert.assertEquals(10, ps.getNumberOfFiles());
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
}
