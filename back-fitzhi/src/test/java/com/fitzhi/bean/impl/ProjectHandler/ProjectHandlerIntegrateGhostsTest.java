package com.fitzhi.bean.impl.ProjectHandler;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.GhostsListFactory;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

/**
 * Test the method {@link ProjectHandler#integrateGhosts(int, java.util.List)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerIntegrateGhostsTest {
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	Project project;

	private int ID_PROJECT = 314116;

	@Before
	public void before() throws ApplicationException {
		project = projectHandler.addNewProject(new Project(ID_PROJECT, "PI 4"));
		project.getGhosts().add(new Ghost("pseudoUnlinked", false));
		project.getGhosts().add(new Ghost("pseudoLinked", 2, false));
	}
	
	
	@Test
	public void test() throws ApplicationException {
		Set<String> pseudos = new HashSet<String>();
		pseudos.add("newpseudo");
		projectHandler.integrateGhosts(ID_PROJECT, GhostsListFactory.getInstance(pseudos));
		
		Assert.assertTrue("Ghosts list contains 2 entries", project.getGhosts().size()== 2);
		
		Assert.assertFalse(
				"pseudoUnlinked has to disappear from the ghosts list", 
				("pseudoUnlinked".equals(project.getGhosts().get(0).getPseudo())
		 ||		"pseudoUnlinked".equals(project.getGhosts().get(1).getPseudo())));

		Assert.assertTrue(
				"newpseudo has to be present the ghosts list", 
				("newpseudo".equals(project.getGhosts().get(0).getPseudo())
		 ||		"newpseudo".equals(project.getGhosts().get(1).getPseudo())));
		
		Assert.assertTrue(
				"pseudoLinked has to be present the ghosts list", 
				("pseudoLinked".equals(project.getGhosts().get(0).getPseudo())
		 ||		"pseudoLinked".equals(project.getGhosts().get(1).getPseudo())));

	}	

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(ID_PROJECT);
	}
}
