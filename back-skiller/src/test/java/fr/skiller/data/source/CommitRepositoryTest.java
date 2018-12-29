package fr.skiller.data.source;

import java.util.Calendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.skiller.data.source.CommitRepository;

public class CommitRepositoryTest {

	CommitRepository commitRepository;
	Calendar calendar;
	
	@Before
	public void before() {
		commitRepository = new BasicCommitRepository();
		calendar = Calendar.getInstance();
		calendar.set(2018, 12, 6, 12, 0);
		calendar.getTime();
	}
	
	@Test
	public void testAdd() {
		commitRepository.addCommit("/test.java", "myUser", "myUser@toto.com", calendar.getTime());
		Assert.assertTrue(commitRepository.containsSourceCode("/test.java"));
	}

	@Test
	public void testNoUpdate() {
		commitRepository.addCommit("/test.java", "myUser", "myUser@toto.com", calendar.getTime());
		Assert.assertTrue(commitRepository.containsSourceCode("/test.java"));
		// One hour before
		Calendar calendar2 = ((Calendar) calendar.clone());
		calendar2.set(Calendar.HOUR_OF_DAY, 11);
		commitRepository.addCommit("/test.java", "myUser", "myUser@toto.com", calendar2.getTime());
		Assert.assertEquals(calendar.getTime(), commitRepository.getLastDateCommit("/test.java", "myUser"));
	}
	
	@Test
	public void testUpdate() {
		commitRepository.addCommit("/test.java", "myUser", "myUser@toto.com", calendar.getTime());
		Assert.assertTrue(commitRepository.containsSourceCode("/test.java"));
		// One hour After
		Calendar calendar2 = ((Calendar) calendar.clone());
		calendar2.set(Calendar.HOUR_OF_DAY, 13);
		commitRepository.addCommit("/test.java", "myUser", "myUser@toto.com", calendar2.getTime());
		Assert.assertTrue(calendar.getTime().before(commitRepository.getLastDateCommit("/test.java", "myUser")));
	}
	
	@After
	public void after() {
	}

}
