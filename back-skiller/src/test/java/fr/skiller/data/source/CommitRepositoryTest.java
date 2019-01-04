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
	
	final int idStaff = 1;
	
	@Before
	public void before() {
		commitRepository = new BasicCommitRepository();
		calendar = Calendar.getInstance();
		calendar.set(2018, 12, 6, 12, 0);
		calendar.getTime();
	}
	
	@Test
	public void testAdd() {
		commitRepository.addCommit("/test.java", idStaff, calendar.getTime());
		Assert.assertTrue(commitRepository.containsSourceCode("/test.java"));
	}

	@Test
	public void testNoUpdate() {
		commitRepository.addCommit("/test.java", idStaff, calendar.getTime());
		Assert.assertTrue(commitRepository.containsSourceCode("/test.java"));
		// One hour before
		Calendar calendar2 = ((Calendar) calendar.clone());
		calendar2.set(Calendar.HOUR_OF_DAY, 11);
		commitRepository.addCommit("/test.java", idStaff, calendar2.getTime());
		Assert.assertEquals(calendar.getTime(), commitRepository.getLastDateCommit("/test.java", idStaff));
	}
	
	@After
	public void after() {
	}

}
