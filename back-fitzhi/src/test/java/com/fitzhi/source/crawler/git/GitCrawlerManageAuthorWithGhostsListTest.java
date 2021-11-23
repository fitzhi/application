package com.fitzhi.source.crawler.git;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * Testing the method {@link GitCrawler#manageAuthorWithGhostsList(Project, com.fitzhi.data.internal.RepositoryAnalysis, java.util.Set, String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerManageAuthorWithGhostsListTest {

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	Project project;
	RepositoryAnalysis analysis;

	@Before
	public void before() {
		project = new Project(1789, "The French revolution");
		List<Ghost> ghosts = new ArrayList<>();
		ghosts.add(new Ghost("pseudo", false));
		ghosts.add(new Ghost("editAuthorTest", 1, false));
		project.setGhosts(ghosts);

		analysis = new RepositoryAnalysis(project);
		analysis.takeChangeInAccount("one", new SourceChange("commitId", null, "editAuthorTest", "authorEmail"));
		analysis.takeChangeInAccount("two", new SourceChange("commitId", null, "authorName", "authorEmail"));
		analysis.takeChangeInAccount("test/my/jquery/nope/arg.js", new SourceChange("commitId", null, "authorName", "authorEmail"));
		analysis.takeChangeInAccount("three", new SourceChange("commitId", null, "editAuthorTest", "authorEmail"));
	}

	@Test
	public void addGhost() throws ApplicationException {
		Set<String> unknownContributors = new HashSet<>();
		scanner.manageAuthorWithGhostsList(project, analysis, unknownContributors, "newAuthorTest");
		Assert.assertEquals(1, unknownContributors.size());
		Assert.assertEquals("newAuthorTest", unknownContributors.toArray(new String[1])[0]);
	}

	@Test
	public void editGhost() throws ApplicationException {

		SourceChange sc = analysis.getChanges().getSourceFileHistory("one").getChanges().get(0);
		Assert.assertEquals(0, sc.getIdStaff());

		Set<String> unknownContributors = new HashSet<>();
		scanner.manageAuthorWithGhostsList(project, analysis, unknownContributors, "editAuthorTest");
		Assert.assertEquals(1, unknownContributors.size());
		Assert.assertEquals("editAuthorTest", unknownContributors.toArray(new String[1])[0]);

		sc = analysis.getChanges().getSourceFileHistory("one").getChanges().get(0);
		Assert.assertEquals(1, sc.getIdStaff());
	}
}
