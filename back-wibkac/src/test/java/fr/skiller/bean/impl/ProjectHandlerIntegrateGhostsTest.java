package fr.skiller.bean.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

import org.junit.After;
import org.junit.Assert;

/**
 * <p>
 * Test the method {@link ProjectHandler#setGhostTechnicalStatus(Project, String, boolean) ProjectHandler.setGhostTechnicalStatus}
 * </p>setGhostTechnicalStatus
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectHandlerIntegrateGhostsTest {
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	Project project;

	@Before
	public void before() throws SkillerException {
		project = projectHandler.addNewProject(new Project(314116, "PI"));
		project.getGhosts().add(new Ghost("pseudoUnlinked", false));
		project.getGhosts().add(new Ghost("pseudoLinked", 2, false));
	}
	
	@Test
	public void test() throws SkillerException {
		Set<String> pseudos = new HashSet<String>();
		pseudos.add("newpseudo");
		projectHandler.integrateGhosts(314116, pseudos);
		
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
	
}
