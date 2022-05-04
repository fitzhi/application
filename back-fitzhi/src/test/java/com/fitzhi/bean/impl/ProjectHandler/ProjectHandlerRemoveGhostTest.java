package com.fitzhi.bean.impl.ProjectHandler;

import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#removeGhost(Project, String)}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerRemoveGhostTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() {
		project = new Project (1789, "French revolution");
		List<Ghost> ghosts = new ArrayList<>();
		ghosts.add(new Ghost("thePseudo", 1789, false));
		ghosts.add(new Ghost("tech", -1, true));
		project.setGhosts(ghosts);
	}

	@Test
	public void removePseudo() {
		projectHandler.dataAreSaved();
		projectHandler.removeGhost(project, "thePseudo");
		Assert.assertEquals(1, project.getGhosts().size());
		Assert.assertEquals("tech", project.getGhosts().get(0).getPseudo());
		Assert.assertTrue(projectHandler.isDataUpdated());
	}

	@Test
	public void notFound() {
		projectHandler.dataAreSaved();
		projectHandler.removeGhost(project, "unknownPseudo");
		Assert.assertEquals(2, project.getGhosts().size());
		Assert.assertFalse(projectHandler.isDataUpdated());
	}
}
