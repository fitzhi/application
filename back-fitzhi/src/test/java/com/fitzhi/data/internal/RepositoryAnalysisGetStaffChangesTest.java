package com.fitzhi.data.internal;

import java.time.LocalDate;
import java.util.List;

import com.fitzhi.source.crawler.git.SourceChange;

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
	public void nominalGetStaffChanges() {
		RepositoryAnalysis ra = new RepositoryAnalysis(p);
		ra.getChanges().addChange("one", new SourceChange(LocalDate.of(2021, 1, 1), 1));
		ra.getChanges().addChange("two", new SourceChange(LocalDate.of(2021, 1, 2), 1));
		List<SourceChange> changes = ra.getStaffChanges(1);
		Assert.assertEquals(2, changes.size());
	}
}
