package com.fitzhi.source.crawler.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Committer;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link AbstractScannerDataGenerator#loadCommitters(StaffHandler, Project)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class AbstractScannerDataGeneratorLoadCommittersTest {
	
	@MockBean
	StaffHandler staffHandler;

	Project project;

	@Before
	public void before() {
		project = new Project (1789, "The French revolution");
		List<Ghost> ghosts = new ArrayList<>();
		project.setGhosts(ghosts);

		Ghost g = new Ghost("one", 1, false);
		g.setNumberOfCommits(1);
		g.setNumberOfFiles(2);
		ghosts.add(g);

		g = new Ghost("two", false);
		g.setNumberOfCommits(1);
		g.setFirstCommit(LocalDate.of(2021, 9, 7));
		g.setLastCommit(LocalDate.of(2021, 9, 8));
		ghosts.add(g);

		g = new Ghost("three", true);
		g.setNumberOfCommits(10);
		g.setFirstCommit(LocalDate.of(2021, 1, 1));
		g.setLastCommit(LocalDate.of(2021, 1, 2));
		ghosts.add(g);

	}
	
	@Test
	public void loadNominal() {
		Staff staff = new Staff(1, "frvidal", "thepass");
		when(staffHandler.lookup(1)).thenReturn(staff);

		List<Committer> committers = AbstractScannerDataGenerator.loadCommitters(staffHandler, project);
		Assert.assertEquals(3, committers.size());
		Committer committer = committers.get(0);
		Assert.assertEquals(1, committer.getIdStaff());
		Assert.assertEquals("one", committer.getPseudo());
		Assert.assertEquals("frvidal", committer.getLogin());
		Assert.assertEquals(1, committer.getNumberOfCommits());
		Assert.assertEquals(2, committer.getNumberOfFiles());

		committer = committers.get(1);
		Assert.assertEquals(-1, committer.getIdStaff());
		Assert.assertEquals("two", committer.getPseudo());
		Assert.assertEquals(LocalDate.of(2021, 9, 7), committer.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2021, 9, 8), committer.getLastCommit());

		committer = committers.get(2);
		Assert.assertEquals(-1, committer.getIdStaff());
		Assert.assertEquals("three", committer.getPseudo());
		Assert.assertTrue(committer.isTechnical());
		Assert.assertEquals(LocalDate.of(2021, 1, 1), committer.getFirstCommit());
		Assert.assertEquals(LocalDate.of(2021, 1, 2), committer.getLastCommit());

		verify(staffHandler, times(1)).lookup(1);

	}
}
