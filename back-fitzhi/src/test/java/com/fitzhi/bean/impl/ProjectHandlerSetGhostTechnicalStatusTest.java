/**
 * 
 */
package com.fitzhi.bean.impl;

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

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;

/**
 * <p>
 * Test the method {@link ProjectHandler#associateStaffToGhost(com.fitzhi.data.internal.Project, String, int) ProjectHandler.associateStaffToGhost}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectHandlerSetGhostTechnicalStatusTest {
	
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
		
		projectHandler.setGhostTechnicalStatus(project, "pseudoUnlinked", true);	

		Project p = projectHandler.get(1);
		Optional<Ghost> oGhost = p.getGhosts().stream()
				.filter(g -> g.getPseudo().equals("pseudoUnlinked"))
				.findFirst();
		if (!oGhost.isPresent()) {
			throw new SkillerException(-1, "Skiller has disappeared");
		}
		Assert.assertTrue(oGhost.get().isTechnical());
		Assert.assertEquals(oGhost.get().getIdStaff(), Ghost.NULL);		
	}

	@Test
	public void testPseudoTwo() throws SkillerException {
		
		projectHandler.setGhostTechnicalStatus(project, "pseudoLinked", true);	

		Project p = projectHandler.get(1);
		Optional<Ghost> oGhost = p.getGhosts().stream()
				.filter(g -> g.getPseudo().equals("pseudoLinked"))
				.findFirst();
		if (!oGhost.isPresent()) {
			throw new SkillerException(-1, "Skiller has disappeared");
		}
		Assert.assertTrue(oGhost.get().isTechnical());
		Assert.assertEquals(oGhost.get().getIdStaff(), Ghost.NULL);
	}
	
	@After
	public void after() throws SkillerException {
		project = projectHandler.get(1);
		project.getGhosts().clear();
				
	}
	
}
