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
		p.getGhosts().add( new Ghost("best_Dev_change", first.getIdStaff(), false) );
		p.getGhosts().add(new Ghost("Sonar_change", true));
		p.getGhosts().add(new Ghost("must disappear", second.getIdStaff(), false));
		p.getGhosts().add(new Ghost("remove me", true));
		p.getGhosts().add( new Ghost("donotneedmore", third.getIdStaff(), false) );
		p.getGhosts().add( new Ghost("toto", false) );		
		projectHandler.addNewProject(p);
	}

	@Test
	public void testOne() throws SkillerException {
		List<Pseudo> pseudos = new ArrayList<>();
		// The pseudo best_Dev_change change his login, and therefore his staff member
		pseudos.add(new Pseudo("best_Dev_change", second.getLogin()));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		
		List<Pseudo> result = projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);

		List<Pseudo> expectedPseudos = new ArrayList<>();
		expectedPseudos.add(new Pseudo("best_Dev_change"
				, second.getIdStaff()
				, staffHandler.getFullname(second.getIdStaff())
				, second.getLogin() 
				, false
				, Action.U));

		Assert.assertArrayEquals("pseudos", expectedPseudos.toArray(), result.toArray());
	}

	@Test
	public void test() throws SkillerException {
		List<Pseudo> pseudos = new ArrayList<>();
		// The pseudo best_Dev_change change his login, and therefore his staff member
		pseudos.add(new Pseudo("best_Dev_change", second.getLogin()));
		// The automatic pseudo "Sonar" finally is a human being.
		pseudos.add(new Pseudo("Sonar_change", first.getLogin()));
		// The pseudo "must disappear" does not correspond anymore to the second staff member.
		pseudos.add(new Pseudo("must disappear", ""));
		// The pseudo "remove me" is no more a technical committer
		pseudos.add(new Pseudo("remove me", false));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		
		List<Pseudo> result = projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);

		List<Pseudo> expectedPseudos = new ArrayList<>();
		expectedPseudos.add(new Pseudo("best_Dev_change"
				, second.getIdStaff()
				, staffHandler.getFullname(second.getIdStaff())
				, second.getLogin() 
				, false
				, Action.U));
		expectedPseudos.add(new Pseudo("Sonar_change"
				, first.getIdStaff()
				, staffHandler.getFullname(first.getIdStaff())
				, first.getLogin() 
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

		Assert.assertEquals("number of ghosts", 2, projectHandler.get(8121964).getGhosts().size());
		
		Ghost g = projectHandler.get(8121964).getGhosts().get(0);
		Assert.assertEquals("1st ghost in list", new Ghost("best_Dev_change", second.getIdStaff(), false), g);
		
		g = projectHandler.get(8121964).getGhosts().get(1);
		Assert.assertEquals("2nd ghost in list", new Ghost("Sonar_change", first.getIdStaff(), false), g);
		
	
	}
	
	@Test
	public void testPseudoToto_TaggedAs_Technical() throws SkillerException {
		List<Pseudo> pseudos = new ArrayList<>();
		// The pseudo "Toto" is tagged as a technical one
		pseudos.add(new Pseudo("toto", true));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);
		
		Assert.assertEquals("Ghost size", projectHandler.getProjects().get(8121964).getGhosts().size(), 1);
		Ghost expected = new Ghost("toto", true);
		Assert.assertEquals("Unique entry in ghosts ", expected, projectHandler.getProjects().get(8121964).getGhosts().get(0));
	}	
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(8121964);
	}
	
}
