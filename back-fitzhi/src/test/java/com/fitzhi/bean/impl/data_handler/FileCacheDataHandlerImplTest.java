package com.fitzhi.bean.impl.data_handler;

import static com.fitzhi.bean.impl.RepositoryState.REPOSITORY_NOT_FOUND;
import static com.fitzhi.bean.impl.RepositoryState.REPOSITORY_READY;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.impl.FileCacheDataHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Testing the class {@link FileCacheDataHandlerImpl}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cache_duration=1" }) 
public class FileCacheDataHandlerImplTest {
	
	@Autowired
	CacheDataHandler cacheDataHandler;
	
	@Test
	public void creation() throws IOException {
		Project project = new Project((int) System.currentTimeMillis(), "TEST_SKILLER");
		CommitRepository repository = new BasicCommitRepository();
		repository.addCommit("com/test.java", 1, "theAuthorOfOne", "email@nope.com", LocalDate.now(), 1);
		repository.addCommit("fr/test/test.java", 1, "theAuthorOfOne", "email@nope.com", LocalDate.now(), 1);
		Assert.assertTrue(cacheDataHandler.retrieveRepositoryState(project) == REPOSITORY_NOT_FOUND);
		
		Set<String> unknowns = new HashSet<>();
		unknowns.add("tintin");
		repository.setUnknownContributors(unknowns);
		cacheDataHandler.saveRepository(project, repository);
		Assert.assertTrue(cacheDataHandler.retrieveRepositoryState(project) == REPOSITORY_READY);
		
		repository = cacheDataHandler.getRepository(project);
		Assert.assertNotNull(repository);
		Assert.assertEquals(2, repository.size());
		Assert.assertEquals(1, repository.unknownContributors().size());
		Assert.assertEquals("tintin", repository.unknownContributors().toArray()[0]);
	}
	
	@Test
	public void testNonExistingLocationRepository() throws Exception {
		Project project = new Project((int) System.currentTimeMillis(), "Project without local repository");
		project.setLocationRepository("/non-existing");
		Assert.assertTrue(cacheDataHandler.retrieveRepositoryState(project) == REPOSITORY_NOT_FOUND);
	}
		
}
