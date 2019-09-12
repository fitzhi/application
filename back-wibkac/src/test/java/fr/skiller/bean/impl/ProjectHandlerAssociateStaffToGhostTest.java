/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * <p>
 * Test the method {@link ProjectHandler#setGhostTechnicalStatus(Project, String, boolean) ProjectHandler.setGhostTechnicalStatus}
 * </p>setGhostTechnicalStatus
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectHandlerAssociateStaffToGhostTest {
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	Project project;

	@Before
	public void before() throws SkillerException {
		project = projectHandler.get(1);
		project.getGhosts().add(new Ghost("pseudoUnlinked", false));
		project.getGhosts().add(new Ghost("pseudoLinked", 2, false));
	}
	
	@Test
	public void testPseudoOne() throws SkillerException {
		
		projectHandler.associateStaffToGhost(project, "pseudoUnlinked", 1);	

		Project p = projectHandler.get(1);
		Optional<Ghost> oGhost = p.getGhosts().stream()
				.filter(g -> g.getPseudo().equals("pseudoUnlinked"))
				.findFirst();
		if (!oGhost.isPresent()) {
			throw new SkillerException(-1, "Skiller has disappeared");
		}
		Assert.assertEquals(oGhost.get().getIdStaff(), 1);
		
	}

	@Test
	public void testPseudoTwo() throws SkillerException {
		
		projectHandler.associateStaffToGhost(project, "pseudoLinked", 1);	

		Project p = projectHandler.get(1);
		Optional<Ghost> oGhost = p.getGhosts().stream()
				.filter(g -> g.getPseudo().equals("pseudoLinked"))
				.findFirst();
		if (!oGhost.isPresent()) {
			throw new SkillerException(-1, "Skiller has disappeared");
		}
		Assert.assertEquals(oGhost.get().getIdStaff(), 1);
	}
	
	@After
	public void after() throws SkillerException {
		project = projectHandler.get(1);
		project.getGhosts().clear();
				
	}
	
}
