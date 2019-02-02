/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.external.Action;
import fr.skiller.data.external.PseudoListDTO;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

import org.junit.Assert;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the method {@link fr.skiller.bean.impl.ProjectHandlerImpl#saveGhosts(int, java.util.List)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandler_saveGhosts_Test {

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	Staff first = null, second = null, third = null;
	
	@Before
	public void before() throws Exception {
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		
		Project p = new Project(8121964, "testingGhost");
		projectHandler.addNewProject(p);
		p.ghosts.add( new Ghost("best_Dev_change", first.idStaff, false) );
		p.ghosts.add(new Ghost("Sonar_change", true));
		p.ghosts.add(new Ghost("must disappear", second.idStaff, false));
		p.ghosts.add(new Ghost("remove me", true));
		p.ghosts.add( new Ghost("donotneedmore", third.idStaff, false) );
		projectHandler.addNewProject(p);
	}

	@Test
	public void testOne() throws SkillerException {
		List<Pseudo> pseudos = new ArrayList<Pseudo>();
		// The pseudo best_Dev_change change his login, and therefore his staff member
		pseudos.add(new Pseudo("best_Dev_change", second.login));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		
		List<Pseudo> result = projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);

		List<Pseudo> expectedPseudos = new ArrayList<Pseudo>();
		expectedPseudos.add(new Pseudo("best_Dev_change"
				, second.idStaff
				, staffHandler.getFullname(second.idStaff)
				, second.login 
				, false
				, Action.U));

		Assert.assertArrayEquals("pseudos", expectedPseudos.toArray(), result.toArray());
	}

	@Test
	public void test() throws SkillerException {
		List<Pseudo> pseudos = new ArrayList<Pseudo>();
		// The pseudo best_Dev_change change his login, and therefore his staff member
		pseudos.add(new Pseudo("best_Dev_change", second.login));
		// The automatic pseudo "Sonar" finally is a human being.
		pseudos.add(new Pseudo("Sonar_change", first.login));
		// The pseudo "must disappear" does not correspond anymore to the second staff member.
		pseudos.add(new Pseudo("must disappear", ""));
		// The pseudo "remove me" is no more a technical committer
		pseudos.add(new Pseudo("remove me", false));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		
		List<Pseudo> result = projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);

		List<Pseudo> expectedPseudos = new ArrayList<Pseudo>();
		expectedPseudos.add(new Pseudo("best_Dev_change"
				, second.idStaff
				, staffHandler.getFullname(second.idStaff)
				, second.login 
				, false
				, Action.U));
		expectedPseudos.add(new Pseudo("Sonar_change"
				, first.idStaff
				, staffHandler.getFullname(first.idStaff)
				, first.login 
				, false
				, Action.U));
		expectedPseudos.add(new Pseudo("must disappear"
				, Ghost.NULL
				, ""
				, ""
				, false
				, Action.D));
		expectedPseudos.add(new Pseudo("remove me"
				, Ghost.NULL
				, ""
				, ""
				, false
				, Action.D));
		
		Assert.assertArrayEquals("pseudos", expectedPseudos.toArray(), result.toArray());

		Assert.assertEquals("number of ghosts", 2, projectHandler.get(8121964).ghosts.size());
		
		Ghost g = projectHandler.get(8121964).ghosts.get(0);
		Assert.assertEquals("1st ghost in list", new Ghost("best_Dev_change", second.idStaff, false), g);
		
		g = projectHandler.get(8121964).ghosts.get(1);
		Assert.assertEquals("2nd ghost in list", new Ghost("Sonar_change", first.idStaff, false), g);
		
	
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(8121964);
	}
	
}
