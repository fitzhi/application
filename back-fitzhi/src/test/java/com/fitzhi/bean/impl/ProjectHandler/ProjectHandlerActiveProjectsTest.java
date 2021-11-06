package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * This class tests the method {@link ProjectHandler#activeProjects()}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ProjectHandlerActiveProjectsTest {

	@Autowired
	ProjectHandler projectHandler;
		
	@Before
	public void before() throws Exception {
		// We force to remove the project Marignan that might have been added by a previous test.
		// There is currently no clear answer to the use case
		projectHandler.removeProject(1515);
	}

	@Test
	public void activeProjects() throws ApplicationException {

		if (log.isInfoEnabled()) {
			if (projectHandler.activeProjects().size() != 3) {
				projectHandler.activeProjects().stream().forEach(p -> log.info(p.getId() + " " + p.getName()));
			}
		}

		// There are 3 active projects declared in the file Projects.json.
		Assert.assertEquals(3, projectHandler.activeProjects().size());

		// We inactivate the project 2.
		projectHandler.inactivateProject(projectHandler.getProject(2));
		Assert.assertEquals(2, projectHandler.activeProjects().size());
	}
	
}
