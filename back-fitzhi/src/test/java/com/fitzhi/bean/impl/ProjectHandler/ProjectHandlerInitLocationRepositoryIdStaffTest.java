package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#initLocationRepository(int)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectHandlerInitLocationRepositoryIdStaffTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "my testing project");
		projectHandler.addNewProject(project);
		project.setLocationRepository("theLocationRepository");
	}
	
	@Test
	public void found() throws ApplicationException {
		projectHandler.initLocationRepository(1789);
		// initLocationReposioty(Project) has been called
		Assert.assertTrue(projectHandler.isDataUpdated());
		Project prj = projectHandler.getProject(1789); 
		Assert.assertNull(prj.getLocationRepository());
	}
	

	@Test
	public void notFound() throws ApplicationException {
		
		Assert.assertThrows(NotFoundException.class, () -> {
			projectHandler.initLocationRepository(1790);
		});
		// initLocationReposioty(Project) has NOT been called
		Project prj = projectHandler.getProject(1789); 
		Assert.assertNotNull(prj.getLocationRepository());
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
}
