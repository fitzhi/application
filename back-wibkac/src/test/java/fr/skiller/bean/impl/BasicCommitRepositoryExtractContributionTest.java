package fr.skiller.bean.impl;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.Contributor;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cacheDirRepository=./src/test/resources/cacheDirRepository/", "cache_duration=100000" }) 
public class BasicCommitRepositoryExtractContributionTest {

	@Autowired
	CacheDataHandler cacheDataHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	@Test
	public void testNominal() throws IOException {
		
		Project project = new Project(1418, "la der des der");
		CommitRepository repository = cacheDataHandler.getRepository(project);
		
		Staff staff = new Staff(10001, "MockFirst", "MOCKLAST", "the 1 o o o one", "the 1 o o o one", "mock@void.com", "N/A");
		
				
		Contributor contributor = repository.extractContribution(staff);
		
		Assert.assertNotNull(contributor);
		Assert.assertEquals(10001, contributor.getIdStaff());
		Assert.assertEquals(2, contributor.getNumberOfFiles());
		Assert.assertEquals(6, contributor.getNumberOfCommitsSubmitted());
		Assert.assertEquals( LocalDate.of(2019, 11, 3), contributor.getFirstCommit());
		Assert.assertEquals( LocalDate.of(2019, 12, 6), contributor.getLastCommit());
		
	}
	
}
