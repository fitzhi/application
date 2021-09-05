package com.fitzhi.data.internal;

import java.time.LocalDate;
import java.util.List;

import com.fitzhi.source.crawler.git.SourceChange;
import static com.fitzhi.Error.CODE_UNDEFINED;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of the method {@link RepositoryAnalysis#getStaffChanges(int)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class RepositoryAnalysisGetStaffChangesTest {
 
	Project p;
	
	@Before
	public void before() {
		p = new Project(1789, "The revolutionary project");
	}

	@Test
	public void nominalGetStaffChangesById() {
		RepositoryAnalysis ra = new RepositoryAnalysis(p);
		ra.getChanges().addChange("one", new SourceChange(LocalDate.of(2021, 1, 1), 1));
		ra.getChanges().addChange("two", new SourceChange(LocalDate.of(2021, 1, 2), 1));
		ra.getChanges().addChange("three", new SourceChange(LocalDate.of(2021, 1, 2), 2));
		ra.getChanges().addChange("four", new SourceChange(LocalDate.of(2021, 1, 2), 3));
		List<SourceChange> changes = ra.getStaffChanges(1);
		Assert.assertEquals(2, changes.size());
	}

	@Test
	public void nominalGetStaffChangesByUnknownId() {
		RepositoryAnalysis ra = new RepositoryAnalysis(p);
		ra.getChanges().addChange("one", new SourceChange(LocalDate.of(2021, 1, 1), 1));
		ra.getChanges().addChange("two", new SourceChange(LocalDate.of(2021, 1, 2), 1));
		List<SourceChange> changes = ra.getStaffChanges(CODE_UNDEFINED);
		Assert.assertTrue(changes.isEmpty());
	}

	@Test
	public void nominalGetStaffChangesByAuthor() {
		RepositoryAnalysis ra = new RepositoryAnalysis(p);
		ra.getChanges().addChange("one", new SourceChange("id1", LocalDate.of(2021, 1, 1), "author-name", "author@email.com"));
		ra.getChanges().addChange("two", new SourceChange("id2", LocalDate.of(2021, 1, 2), "author-name", "author@email.com"));
		ra.getChanges().addChange("two", new SourceChange("id3", LocalDate.of(2021, 1, 2), "another-author-name", "author@email.com"));
		List<SourceChange> changes = ra.getStaffChanges(new Author("author-name", "author@email.com"));
		Assert.assertEquals(2, changes.size());
	}

	@Test
	public void nominalGetStaffChangesByUnknownAuthor() {
		RepositoryAnalysis ra = new RepositoryAnalysis(p);
		ra.getChanges().addChange("one", new SourceChange("id1", LocalDate.of(2021, 1, 1), "author-name", "author@email.com"));
		ra.getChanges().addChange("two", new SourceChange("id2", LocalDate.of(2021, 1, 2), "author-name", "author@email.com"));
		List<SourceChange> changes = ra.getStaffChanges(new Author("fr-author-name", "author@email.com"));
		Assert.assertTrue(changes.isEmpty());
	}
}
