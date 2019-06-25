/**
 * 
 */
package fr.skiller.bean.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
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
import fr.skiller.data.internal.Committer;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the method {@link fr.skiller.bean.impl.ProjectHandlerImpl#saveGhosts(int, java.util.List)}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerSaveGhostsTest {

	private static final String REMOVE_ME = "remove me";

	private static final String MUST_DISAPPEAR = "must disappear";

	private static final String SONAR_CHANGE = "Sonar_change";

	private static final String BEST_DEV_CHANGE = "best_Dev_change";

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	Staff first = null;
	Staff second = null;
	Staff third = null;
	
	@Before
	public void before() throws SkillerException {
		
		first = (Staff) staffHandler.getStaff().values().toArray()[0];
		second = (Staff) staffHandler.getStaff().values().toArray()[1];
		third = (Staff) staffHandler.getStaff().values().toArray()[2];
		
		Project p = new Project(8121964, "testingGhost");
		projectHandler.addNewProject(p);
		p.getGhosts().add( new Ghost(BEST_DEV_CHANGE, first.getIdStaff(), false) );
		p.getGhosts().add(new Ghost(SONAR_CHANGE, true));
		p.getGhosts().add(new Ghost(MUST_DISAPPEAR, second.getIdStaff(), false));
		p.getGhosts().add(new Ghost(REMOVE_ME, true));
		p.getGhosts().add( new Ghost("donotneedmore", third.getIdStaff(), false) );
		p.getGhosts().add( new Ghost("toto", false) );		
		projectHandler.addNewProject(p);
	}

	@Test
	public void testOne() throws SkillerException {
		List<Committer> pseudos = new ArrayList<>();
		// The pseudo best_Dev_change change his login, and therefore his staff member
		pseudos.add(new Committer(BEST_DEV_CHANGE, second.getLogin()));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		
		List<Committer> result = projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);

		List<Committer> expectedPseudos = new ArrayList<>();
		expectedPseudos.add(new Committer(BEST_DEV_CHANGE
				, second.getIdStaff()
				, staffHandler.getFullname(second.getIdStaff())
				, second.getLogin() 
				, false
				, Action.U));

		Assert.assertArrayEquals("pseudos", expectedPseudos.toArray(), result.toArray());
	}

	@Test
	public void test() throws SkillerException {
		List<Committer> pseudos = new ArrayList<>();
		// The pseudo best_Dev_change change his login, and therefore his staff member
		pseudos.add(new Committer(BEST_DEV_CHANGE, second.getLogin()));
		// The automatic pseudo "Sonar" finally is a human being.
		pseudos.add(new Committer(SONAR_CHANGE, first.getLogin()));
		// The pseudo "must disappear" does not correspond anymore to the second staff member.
		pseudos.add(new Committer(MUST_DISAPPEAR, ""));
		// The pseudo "remove me" is no more a technical committer
		pseudos.add(new Committer(REMOVE_ME, false));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		
		List<Committer> result = projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);

		List<Committer> expectedPseudos = new ArrayList<>();
		expectedPseudos.add(new Committer(BEST_DEV_CHANGE
				, second.getIdStaff()
				, staffHandler.getFullname(second.getIdStaff())
				, second.getLogin() 
				, false
				, Action.U));
		expectedPseudos.add(new Committer(SONAR_CHANGE
				, first.getIdStaff()
				, staffHandler.getFullname(first.getIdStaff())
				, first.getLogin() 
				, false
				, Action.U));
		expectedPseudos.add(new Committer(MUST_DISAPPEAR
				, Ghost.NULL
				, ""
				, ""
				, false
				, Action.D));
		expectedPseudos.add(new Committer(REMOVE_ME
				, Ghost.NULL
				, ""
				, ""
				, false
				, Action.D));
		
		Assert.assertArrayEquals("pseudos", expectedPseudos.toArray(), result.toArray());

		Assert.assertEquals("number of ghosts", 2, projectHandler.get(8121964).getGhosts().size());
		
		Ghost g = projectHandler.get(8121964).getGhosts().get(0);
		Assert.assertEquals("1st ghost in list", new Ghost(BEST_DEV_CHANGE, second.getIdStaff(), false), g);
		
		g = projectHandler.get(8121964).getGhosts().get(1);
		Assert.assertEquals("2nd ghost in list", new Ghost(SONAR_CHANGE, first.getIdStaff(), false), g);
		
	
	}
	
	@Test
	public void testPseudoTotoTaggedAsTechnical() throws SkillerException {
		List<Committer> pseudos = new ArrayList<>();
		// The pseudo "Toto" is tagged as a technical one
		pseudos.add(new Committer("toto", true));
		
		PseudoListDTO pseudosDTO = new PseudoListDTO(8121964, pseudos);
		projectHandler.saveGhosts(8121964, pseudosDTO.unknowns);
		
		Assert.assertEquals("Ghost size", projectHandler.getProjects().get(8121964).getGhosts().size(), 1);
		Ghost expected = new Ghost("toto", true);
		Assert.assertEquals("Unique entry in ghosts ", expected, projectHandler.getProjects().get(8121964).getGhosts().get(0));
	}	
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(8121964);
	}
	
}
