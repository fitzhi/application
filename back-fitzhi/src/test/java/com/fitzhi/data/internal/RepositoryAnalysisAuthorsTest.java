package com.fitzhi.data.internal;

import java.time.LocalDate;
import java.util.List;

import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of the method {@link RepositoryAnalysis#authors()}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class RepositoryAnalysisAuthorsTest {
 
	Project p;
	
	@Before
	public void before() {
		p = new Project(1789, "The revolutionary project");
	}

	@Test
	public void nominalAuthors() {
		RepositoryAnalysis ra = new RepositoryAnalysis(p);
		ra.getChanges().addChange("one", new SourceChange("id1", LocalDate.of(2021, 1, 1), "author-name", "author@email.com"));
		ra.getChanges().addChange("two", new SourceChange("id2", LocalDate.of(2021, 1, 2), "author-name", "author@email.com"));
		List<Author> authors = ra.authors();
		Assert.assertEquals(1, authors.size());
		Assert.assertEquals("author-name", authors.get(0).getName());
		Assert.assertEquals("author@email.com", authors.get(0).getEmail());
	}
}
