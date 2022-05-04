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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the method {@link ProjectHandler#addSonarEntry(Project, com.fitzhi.data.internal.SonarProject)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectHandlerAddSonarEntryTest {
	
	@Autowired
	ProjectHandler projectHandler;
	
	Project project;

	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "the revolution");
	}
	
	@Test
	public void addSonar() throws ApplicationException {
		projectHandler.dataAreSaved();
		SonarProject sp = new SonarProject("key-sonar", "name-sonar");
		projectHandler.addSonarEntry(project, sp);
		Assert.assertEquals(1, project.getSonarProjects().size());
		Assert.assertEquals("key-sonar", project.getSonarProjects().get(0).getKey());
		Assert.assertEquals("name-sonar", project.getSonarProjects().get(0).getName());
		Assert.assertTrue(projectHandler.isDataUpdated());
	}

	@Test
	public void sonarAlreadyExist() throws ApplicationException {
		projectHandler.dataAreSaved();
		SonarProject sp = new SonarProject("key-sonar", "name-sonar");
		project.getSonarProjects().add(sp);

		projectHandler.addSonarEntry(project, sp);
		
		Assert.assertFalse(projectHandler.isDataUpdated());
	}

}
