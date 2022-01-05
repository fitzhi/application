package com.fitzhi.data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GhostsListFactoryGetInstanceTest {
 
	CommitRepository repo;

	@Before
	public void before() {
		repo = new BasicCommitRepository();

		Set<String> unknowns = new HashSet<>();
		unknowns.add("one");
		unknowns.add("two");
		unknowns.add("three");
		unknowns.add("WTF should not be here");
		repo.setUnknownContributors(unknowns);

		repo.addCommit("A", 0, "one", "email@nope.com", LocalDate.of(2021, 6, 1), 1);
		repo.addCommit("A", 0, "one", "email@nope.com", LocalDate.of(2021, 6, 1), 1);
		repo.addCommit("A", 0, "one", "email@nope.com", LocalDate.of(2021, 6, 1), 1);
		repo.addCommit("A", 0, "two", "email@nope.com", LocalDate.of(2021, 6, 1), 1);
		repo.addCommit("A", 0, "two", "email@nope.com", LocalDate.of(2021, 6, 1), 1);

		repo.addCommit("B", 0, "two", "email@nope.com", LocalDate.of(2021, 9, 1), 1);
		repo.addCommit("B", 0, "two", "email@nope.com", LocalDate.of(2021, 9, 2), 1);
		repo.addCommit("B", 0, "two", "email@nope.com", LocalDate.of(2021, 9, 2), 1);
		repo.addCommit("B", 0, "three", "email@nope.com", LocalDate.of(2021, 10, 1), 1);
		repo.addCommit("B", 0, "three", "email@nope.com", LocalDate.of(2021, 10, 2), 1);
		repo.addCommit("B", 0, "one", "email@nope.com", LocalDate.of(2020, 11, 1), 1);
	}

	@Test
	public void testNominal() {

		List<Ghost> ghosts = GhostsListFactory.getInstance(repo);
		Assert.assertEquals(3, ghosts.size());

		Ghost one = ghosts.get(0);
		Assert.assertEquals("one", one.getPseudo());
		Assert.assertEquals(4, one.getNumberOfCommits());
		Assert.assertEquals(2, one.getNumberOfFiles());
		Assert.assertEquals(LocalDate.of(2020, 11, 1), one.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2021, 6, 1), one.getLastCommit());

		Ghost two = ghosts.get(1);
		Assert.assertEquals("two", two.getPseudo());
		Assert.assertEquals(5, two.getNumberOfCommits());
		Assert.assertEquals(2, two.getNumberOfFiles());
		Assert.assertEquals(LocalDate.of(2021, 6, 1), two.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2021, 9, 2), two.getLastCommit());

		Ghost three = ghosts.get(2);
		Assert.assertEquals("three", three.getPseudo());
		Assert.assertEquals(2, three.getNumberOfCommits());
		Assert.assertEquals(1, three.getNumberOfFiles());
		Assert.assertEquals(LocalDate.of(2021, 10, 1), three.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2021, 10, 2), three.getLastCommit());

	}
}
