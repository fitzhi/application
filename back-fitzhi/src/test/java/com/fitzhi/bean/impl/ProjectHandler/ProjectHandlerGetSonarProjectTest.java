package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.impl.ProjectHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the method {@link ProjectHandler#getSonarProject}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
public class ProjectHandlerGetSonarProjectTest {
   
	private Project project;

	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "my testing project 4");
		project.getSonarProjects().add(new SonarProject("key-sonar", "value-sonar"));
	}

	@Test
	public void found() throws ApplicationException { 
		Assert.assertNotNull(ProjectHandlerImpl.getSonarProject(project, "key-sonar"));
	}

	@Test (expected = ApplicationException.class)
	public void notFound() throws ApplicationException { 
		ProjectHandlerImpl.getSonarProject(project, "key-unknown");
	}
}
