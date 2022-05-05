package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#initLocationRepository(Project)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerInitLocationRepositoryProjectTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "my testing project 6");
		project.setLocationRepository("theLocationRepository");
		projectHandler.addNewProject(project);
	}
	
	@Test
	public void found() throws ApplicationException {
		projectHandler.initLocationRepository(project);
		// initLocationReposioty(Project) has been called
		Assert.assertTrue(projectHandler.isDataUpdated());
		Project prj = projectHandler.getProject(1789); 
		Assert.assertNull(prj.getLocationRepository());
	}
	

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
}
