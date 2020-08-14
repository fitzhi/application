/**
 * 
 */
package com.fitzhi.source.crawler.git;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the cleanup process of pseudos.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitScannerCleanupUnknownPseudosTest {

	
	Logger logger = LoggerFactory.getLogger(GitScannerCleanupUnknownPseudosTest.class.getCanonicalName());
	
	@MockBean
	private CacheDataHandler cacheDataHandler;
	
	@Autowired
	@Qualifier("GIT")
	private RepoScanner gitScanner;

	@Autowired
	private StaffHandler staffHandler;

	private CommitRepository repo;
	
	private Project project;
	
	@Before
	public void before() {
		repo = new BasicCommitRepository();
		repo.unknownContributors().add("no name");	
		repo.unknownContributors().add("Frédéric VIDAL");
		repo.unknownContributors().add("desaparecido");
		
		project = new Project(1, "TEST");
		
		staffHandler.getStaff().put(1000, new Staff(1000, "Frédéric", "VIDAL", "frvidal", "Frédéric", "f@test.com", ""));
	}
	
	@Test
	public void test() {
		staffHandler.getStaff().values().stream().forEach(staff -> System.out.println (staff.getLastName()));
		Staff staff = staffHandler.lookup("Frédéric VIDAL");
		logger.debug(staff.toString());
		Assert.assertNotNull(staffHandler.lookup("Frédéric VIDAL"));	
	}
	
	/**
	 * We moved from 3 unknown pseudos to 2. 
	 * @throws Exception
	 */
	@Test
	public void testCleanup() throws Exception {

		Mockito.when(cacheDataHandler.hasCommitRepositoryAvailable(project)).thenReturn(true);
		Mockito.when(cacheDataHandler.getRepository(project)).thenReturn(repo);

		CommitRepository repoCleaned = gitScanner.parseRepository(project);
		Assert.assertEquals(repoCleaned.unknownContributors().size(), 2);
	}
	
}
