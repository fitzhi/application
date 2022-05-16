package com.fitzhi.bean.impl.ProjectHandler;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Ghost;
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
 * This class tests the method {@link ProjectHandler#getGhost(Project, String)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerGetGhostTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "my testing project 3");
		List<Ghost> ghosts = new ArrayList<Ghost>();
		ghosts.add(new Ghost("myPseudo", 122, false));
		project.setGhosts(ghosts);
	}
	
	@Test
	public void found() throws ApplicationException {
		Ghost ghost = projectHandler.getGhost(project, "myPseudo");
		Assert.assertNotNull(ghost);
		Assert.assertEquals(122, ghost.getIdStaff());
	}
	
	@Test
	public void notFound() throws ApplicationException {
		Ghost ghost = projectHandler.getGhost(project, "unknownPseudo");
		Assert.assertNull(ghost);
	}
}
