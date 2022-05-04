package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#hasValidRepository(com.fitzhi.data.internal.Project)}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerHasValidRepositoryTest {

	private Project project;

	@Autowired
	ProjectHandler projectHandler;
		
	@Before
	public void before() throws Exception {
		project = new Project(1789, "The French revolution");
	}

	@Test
	public void noRepository() {
		Assert.assertFalse(projectHandler.hasValidRepository(project));
	}

	@Test
	public void notFound()  {
		project.setLocationRepository(".");
		Assert.assertFalse(projectHandler.hasValidRepository(project));
	}

	@Test
	public void found() {
		// We use the Project .git located in the application folder, which is a parent of back-fitzhi
		project.setLocationRepository("..");
		Assert.assertTrue(projectHandler.hasValidRepository(project));
	}

}
