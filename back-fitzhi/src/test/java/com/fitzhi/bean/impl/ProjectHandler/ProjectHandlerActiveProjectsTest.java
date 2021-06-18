package com.fitzhi.bean.impl.ProjectHandler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.exception.ApplicationException;

/**
 * This class tests the method {@link ProjectHandler#activeProjects()}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerActiveProjectsTest {

	@Autowired
	ProjectHandler projectHandler;
		
	@Test
	public void activeProjects() throws ApplicationException {

		projectHandler.getProjects().values().stream().forEach(p -> System.out.println(p.getId() +  "  " +p.getName())));

		// There are 3 active projects declared in the file Projects.json.
		Assert.assertEquals(3, projectHandler.activeProjects().size());

		// We inactivate the project 2.
		projectHandler.inactivateProject(projectHandler.getProject(2));
		Assert.assertEquals(2, projectHandler.activeProjects().size());
	}
	
}
