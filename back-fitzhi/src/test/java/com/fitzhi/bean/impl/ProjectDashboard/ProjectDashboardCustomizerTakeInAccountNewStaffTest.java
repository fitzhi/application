package com.fitzhi.bean.impl.ProjectDashboard;


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;
/**
 * Test the method {@link ProjectDashboardCustomizer#takeInAccountNewStaff(com.fitzhi.data.internal.Project, Staff)}.<br/>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = ProjectDashboardCustomizerTakeInAccountNewStaffTest.Initializer.class)
@SpringBootTest
@TestPropertySource(properties = { "cache_duration=100000" }) 
public class ProjectDashboardCustomizerTakeInAccountNewStaffTest {

	
	/**
	 * This initializer is there to setup the {@code cache.working.dir} with the appropriate path for the OS environment.
	 * @author Fr&eacute;d&eacute;ric VIDAL
	 */
	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"cache.working.dir=" +  MessageFormat.format(".{0}src{0}test{0}resources{0}cacheDirRepository{0}", 
					File.separator))
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	@Autowired
	ProjectDashboardCustomizer customizer;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	StaffHandler staffHandler;

	@Autowired
	CacheDataHandler cacheDataHandler;
	
	/**
	 * The Commit repository is SAVED when starting the test for a backup pupose.
	 * It will be RESTORED after the test in order to reset the system in the original state.
	 */
	CommitRepository savedRepository;
	
	@Before
	public void before() throws IOException {
		Project project = new Project(1917, "The Red Rev project");
		savedRepository = cacheDataHandler.getRepository(project);
	}
	

	/**
	 * This TEST verifies 2 specific cases
	 * The file has 4 commits detected.
	 * 2 commits on the 6/12/2019
	 * 2 commits on the 7/12/2019
	 * 
	 * On the 6/12, one commit is already registered with an identified staff 1 and the "authorName": "theAuthorOfOne"
	 * We will onboard the Staff fvidal with the identifier 1. So the 2 records has to be merged in to a single one.
	 * 
	 * On the 7/12, the 2 commits have been made by 2 ghosts which correspond to the same staff member with id 1.
	 * Therefore these 2 commits have to be merged.
	 * 
	 * @throws ApplicationException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void onBoardingNominal() throws ApplicationException, IOException, Exception {
		Project project = new Project(1917, "The Red Revolutionary project");
		
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "altF4", "fvidal", "frvidal@void.com", "OIM");
		staffHandler.getStaff().put(1, staff);
		Mission m1 = new Mission(1, 1919, "N/A");
		m1.setNumberOfCommits(2);
		m1.setNumberOfFiles(2);
		staff.addMission(m1);
		
		
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
		
		Assert.assertEquals("The host 'Frédéric VIDAL' is no more a ghost", 0,
				repository.unknownContributors().stream().filter(ghost -> ghost.equals("Frédéric VIDAL")).count());
		
		staff = staffHandler.lookup(1);
		Optional<Mission> oMission = 
				staff.getMissions().stream().filter(mission -> mission.getIdProject() == 1917).findFirst();
		
		if (!oMission.isPresent()) {
			throw new Exception("oMission should be not empty.");
		}
		Mission mission = oMission.get();
		
		Assert.assertEquals(2, mission.getNumberOfFiles());
		Assert.assertEquals(4, mission.getNumberOfCommits());
		Assert.assertEquals(LocalDate.of(2019, 12, 6), mission.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2019, 12, 7), mission.getLastCommit());
		Assert.assertEquals(1917, mission.getIdProject());
		Assert.assertEquals("The Red Revolutionary project", mission.getName());
		Assert.assertEquals(1, mission.getIdStaff());
		
	}
	
	@After
	public void after() throws IOException {
		Project project = new Project(1917, "The Red Rev project");
		cacheDataHandler.saveRepository(project, savedRepository);
	}
}
