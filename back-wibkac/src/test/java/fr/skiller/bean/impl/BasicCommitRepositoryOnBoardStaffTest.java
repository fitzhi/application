package fr.skiller.bean.impl;

import java.io.IOException;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cacheDirRepository=./src/test/resources/cacheDirRepository/", "cache_duration=100000" }) 
public class BasicCommitRepositoryOnBoardStaffTest {

	@Autowired
	CacheDataHandler cacheDataHandler;
	
	@Autowired
	StaffHandler staffHandler;
	
	@Test
	public void testNominal() throws IOException {
		
		Project project = new Project(1917, "The Red Rev project");
		CommitRepository repository = cacheDataHandler.getRepository(project);
		
		Staff staff = new Staff(1, "Frédéric", "VIDAL", "altF4", "fvidal", "frvidal@void.com", "OIM");
		
		
		Assert.assertEquals(4,
				repository.getRepository().get("fr/test/test.java").operations.size());
		Assert.assertEquals(3,
				repository.getRepository().get("com/test.java").operations.size());
		
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(0).getIdStaff());
		Assert.assertEquals(-1,
				repository.getRepository().get("com/test.java").operations.get(1).getIdStaff());
		Assert.assertEquals(-1,
				repository.getRepository().get("com/test.java").operations.get(2).getIdStaff());
		
		repository.onBoardStaff(staffHandler, staff);
		
		Assert.assertEquals(2,
				repository.getRepository().get("fr/test/test.java").operations.size());
		
		Assert.assertEquals(2,
				repository.getRepository().get("com/test.java").operations.size());
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(0).getIdStaff());
		Assert.assertEquals(1,
				repository.getRepository().get("com/test.java").operations.get(1).getIdStaff());
		
	}
	
}
