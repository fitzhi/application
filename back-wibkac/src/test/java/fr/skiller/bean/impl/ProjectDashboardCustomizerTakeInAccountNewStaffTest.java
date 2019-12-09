/**
 * 
 */
package fr.skiller.bean.impl;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.ProjectDashboardCustomizer;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Mission;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.exception.SkillerException;
/**
 * Test the method {@link ProjectDashboardCustomizer#takeInAccountNewStaff(fr.skiller.data.internal.Project, Staff)}.<br/>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cacheDirRepository=./src/test/resources/cacheDirRepository/", "cache_duration=100000" }) 
public class ProjectDashboardCustomizerTakeInAccountNewStaffTest {

	@Autowired
	ProjectDashboardCustomizer customizer;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	CacheDataHandler cacheDataHandler;
	
	CommitRepository savedRepository;
	
	@Before
	public void before() throws IOException {
		Project project = new Project(1917, "The Red Rev project");
		savedRepository = cacheDataHandler.getRepository(project);
	}
	
	@Test
	public void testOnBoardingNominal() throws SkillerException, IOException {
		Project project = new Project(1917, "The Red Rev project");
		
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "altF4", "fvidal", "frvidal@void.com", "OIM");
		staffHandler.getStaff().put(1, staff);
		
		
		CommitRepository repository = cacheDataHandler.getRepository(project);
		repository.dump();
		
		// we start with 4 operations
		Assert.assertEquals(4,
				repository.getRepository().get("fr/test/test.java").operations.size());
		
		customizer.takeInAccountNewStaff(project, staff);
		
		repository = cacheDataHandler.getRepository(project);
		repository.dump();
		
		// Testing the result.
		Assert.assertEquals(2,
				repository.getRepository().get("fr/test/test.java").operations.size());
		
		Assert.assertEquals(2,
				repository.getRepository().get("com/test.java").operations.size());
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(0).getIdStaff());
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(1).getIdStaff());
		
		staff = staffHandler.getStaff(1);
		Optional<Mission> oMission = 
				staff.getMissions().stream().filter(mission -> mission.getIdProject() == 1917).findFirst();
		
		Mission mission = oMission.get();
		
		Assert.assertEquals(2, mission.getNumberOfFiles());
		Assert.assertEquals(4, mission.getNumberOfCommits());
		Assert.assertEquals(LocalDate.of(2019, 12, 6), mission.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2019, 12, 7), mission.getLastCommit());
		
	}
	
	@After
	public void after() throws IOException {
		Project project = new Project(1917, "The Red Rev project");
		cacheDataHandler.saveRepository(project, savedRepository);
	}
}
