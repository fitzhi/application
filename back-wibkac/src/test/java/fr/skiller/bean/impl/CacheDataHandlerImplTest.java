/**
 * 
 */
package fr.skiller.bean.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.skiller.bean.CacheDataHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;

/**
 * Testing the class CacheDataHandlerImpl
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "cache_duration=1" }) 
public class CacheDataHandlerImplTest {
	
	@Autowired
	private MockMvc mvc;

	@Autowired
	CacheDataHandler cacheDataHandler;
	
	@Before
	public void before() {
	}

	@Test
	public void creation() throws IOException {
		Project project = new Project((int) System.currentTimeMillis(), "TEST_SKILLER");
		CommitRepository repository = new BasicCommitRepository();
		repository.addCommit("com/test.java", 1, LocalDate.now(), 1);
		repository.addCommit("fr/test/test.java", 1, LocalDate.now(), 1);
		Assert.assertFalse(cacheDataHandler.hasCommitRepositoryAvailable(project));

		cacheDataHandler.saveRepository(project, repository);
		Assert.assertTrue(cacheDataHandler.hasCommitRepositoryAvailable(project));
		
		repository = cacheDataHandler.getRepository(project);
		Assert.assertNotNull(repository);
		Assert.assertEquals(2, repository.size());
	}
	
	
}
