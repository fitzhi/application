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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;

/**
 * <p>
 * Testing the method {@link ProjectHandler#detachStaffMemberFromGhostsOfAllProjects(int) }
 * </p>
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerDetachStaffMemberFromGhostsOfAllProjectsTest {

	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws Exception {
		projectHandler.addNewProject(new Project (1789, "French revolution"));			
		projectHandler.addNewProject(new Project (1805, "Austerlitz"));		
	}
	
	@Test
	public void test() throws SkillerException {
		Project project1789 = projectHandler.get(1789);
		project1789.getGhosts().add(new Ghost("pseudo-1789 666", 666, false));
		project1789.getGhosts().add(new Ghost("pseudo-1789 777", 777, false));
		project1789.getGhosts().add(new Ghost("pseudo-1789 -1", -1, false));
		
		Project project1805 = projectHandler.get(1805);
		project1805.getGhosts().add(new Ghost("pseudo-1805 666", 666, false));
		project1805.getGhosts().add(new Ghost("pseudo-1805 777", 888, false));
		project1805.getGhosts().add(new Ghost("pseudo-1805 -1", -1, false));
		
		projectHandler.detachStaffMemberFromGhostsOfAllProjects(666);
		
		Optional<Ghost> o1789 = project1789
				.getGhosts()
				.stream()
				.filter(ghost -> ("pseudo-1789 666".equals(ghost.getPseudo())))
				.findFirst();	
		
		Assert.assertEquals (-1, o1789.isPresent() ? o1789.get().getIdStaff() : 0);

		Optional<Ghost> o1805 = project1805
				.getGhosts()
				.stream()
				.filter(ghost -> ("pseudo-1805 666".equals(ghost.getPseudo())))
				.findFirst();	
		Assert.assertEquals (-1, o1805.isPresent() ? o1805.get().getIdStaff() : 0);
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(1789);
		projectHandler.getProjects().remove(1805);
	}
}
