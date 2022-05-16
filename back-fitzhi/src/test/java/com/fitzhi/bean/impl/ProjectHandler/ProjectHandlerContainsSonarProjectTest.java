package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#containsSonarProject(Project, String)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerContainsSonarProjectTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "my testing project 2");
		project.getSonarProjects().add(new SonarProject("key-sonar", "value-sonar"));
	}
	
	@Test
	public void found() throws ApplicationException {
		Assert.assertTrue(projectHandler.containsSonarProject(project, "key-sonar"));
	}
	
	@Test
	public void notFound() throws ApplicationException {
		Assert.assertFalse(projectHandler.containsSonarProject(project, "key-unknown"));
	}
}
