package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.io.File;
import java.io.IOException;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
 
//    private final String TESTING_PROJECT = "application";
    private final String TESTING_PROJECT = "repo-test-number-of-lines";

	private final String REPO_DIR = ".." + File.separator + "git_repo_for_test" + File.separator + "%s";

    @Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
 
    @Autowired
    AsyncTask asyncTask;
 
    @Before
    public void before() throws SkillerException {
    	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1809);
    }

    /**
     * Testing the loadChanges(...) method with the GitHub Project repo-test-number-of-lines
     */
    @Test
    public void testLoadChangesWithProjectRepoTestNumberOfLines() throws IOException, SkillerException {
        Project project = new Project (1809, "Wagram");
        project.setLocationRepository(new File(String.format(REPO_DIR, TESTING_PROJECT)).getAbsolutePath());
        project.add(new Library("two/lib", 1));
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try (Repository repo = builder.setGitDir(new File(project.getLocationRepository() + "/.git")).readEnvironment().findGitDir().build()) {
            final RepositoryAnalysis analysis = scanner.loadChanges(project, repo);
            Assert.assertEquals(4, analysis.numberOfFiles());
        }
    }

	@After
	public void after() {
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1809);
	}

}
