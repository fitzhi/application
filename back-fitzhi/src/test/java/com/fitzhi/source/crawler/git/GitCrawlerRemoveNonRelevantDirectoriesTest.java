package com.fitzhi.source.crawler.git;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.Global;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.source.crawler.RepoScanner;

/**
 * <p>
 * Test of the method {@link GitCrawler#removeNonRelevantDirectories(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.RepositoryAnalysis) }
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerRemoveNonRelevantDirectoriesTest {

	@Autowired
	@Qualifier("GIT")
	RepoScanner repoScanner;
	
	@Test
	public void test() {
		Project project = new Project(1789, "Revolutionnay test");
		project.add(new Library("jquery", Global.LIBRARY_DETECTED));
		project.add(new Library("docs", Global.LIBRARY_DECLARED));
		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		analysis.addChange("one", new SourceChange("commitId", null, "authorName", "authorEmail"));
		analysis.addChange("two", new SourceChange("commitId", null, "authorName", "authorEmail"));
		analysis.addChange("test/my/jquery/nope/arg.js", new SourceChange("commitId", null, "authorName", "authorEmail"));
		analysis.addChange("three", new SourceChange("commitId", null, "authorName", "authorEmail"));
		repoScanner.removeNonRelevantDirectories(project, analysis);
		Assert.assertTrue("jquery has be ne deleted", analysis.numberOfFiles()== 3);
	}
}
