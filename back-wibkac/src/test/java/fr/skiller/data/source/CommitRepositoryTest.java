package fr.skiller.data.source;

import java.time.LocalDate;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
		commitRepository.addCommit("/test.java", 1, "one", LocalDate.now(), 1);
		Assert.assertTrue(commitRepository.containsSourceCode("/test.java"));
	}

}
