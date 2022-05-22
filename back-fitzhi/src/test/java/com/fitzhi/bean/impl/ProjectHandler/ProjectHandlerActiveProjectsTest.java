package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class ProjectHandlerActiveProjectsTest {

	@Autowired
	ProjectHandler projectHandler;
		
	@Test
	public void activeProjects() throws ApplicationException {

		// WTF the project 1515 might have been added by anoter test and kept for an unknown reason.
		// Shame on me.
		projectHandler.removeProject(1515);

		if (log.isInfoEnabled()) {
			if (projectHandler.activeProjects().size() != 3) {
				projectHandler.activeProjects().stream().forEach(p -> log.info(p.getId() + " " + p.getName()));
			}
		}

		String message = projectHandler.activeProjects().stream()
			.map(p -> p.getId() + " " + p.getName() + ", ")
			.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
			.toString();
		// There are 3 active projects declared in the file Projects.json.
		Assert.assertEquals("Invalid number of active projects / Projects (" +message+")", 3, projectHandler.activeProjects().size());

		// We inactivate the project 2.
		projectHandler.inactivateProject(projectHandler.getProject(2));
		Assert.assertEquals(2, projectHandler.activeProjects().size());
	}
	
}
