package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class is testing the method
 * {@link GitCrawler#loadChanges(com.fitzhi.data.internal.Project, org.eclipse.jgit.lib.Repository)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=true" }) 
public class GitCrawlerLoadChangesTest {
 
    private final String TESTING_PROJECT = "repo-test-number-of-lines";

	private final String REPO_DIR = ".." + File.separator + "git_repo_for_test" + File.separator + "%s";

    @Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
  
    /**
     * Testing the loadChanges(...) method with the GitHub Project repo-test-number-of-lines
     */
    @Test
    public void testLoadChangesWithProjectRepoTestNumberOfLines() throws IOException, SkillerException {
        Project project = new Project (1809, "Wagram");
        project.setLocationRepository(new File(String.format(REPO_DIR, TESTING_PROJECT)).getAbsolutePath());

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repo = builder.setGitDir(new File(project.getLocationRepository() + "/.git")).readEnvironment().findGitDir()
				.build();

        final RepositoryAnalysis analysis = scanner.loadChanges(project, repo);
        Assert.assertEquals(3, analysis.numberOfFiles());
    }
}