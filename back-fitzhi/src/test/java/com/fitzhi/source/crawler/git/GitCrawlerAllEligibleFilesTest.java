package com.fitzhi.source.crawler.git;

import java.io.File;
import java.util.Set;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is testing the method
 * {@link GitCrawler#allEligibleFiles(Project)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=true" }) 
@Slf4j
public class GitCrawlerAllEligibleFilesTest {
 
    private final String TESTING_PROJECT = "repo-test-number-of-lines";

	private final String REPO_DIR = ".." + File.separator + "git_repo_for_test" + File.separator + "%s";

    @Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
  
    /**
     * Testing the GitCrawler.allFiles(...) method with the GitHub Project repo-test-number-of-lines
     */
    @Test
    public void testAllEligibleFilesWithProjectRepoTestNumberOfLines() throws SkillerException {
        Project project = new Project (1809, "Wagram");
        project.setLocationRepository(new File(String.format(REPO_DIR, TESTING_PROJECT)).getAbsolutePath());
 
        Set<String> allFiles = scanner.allEligibleFiles(project);
        if (log.isDebugEnabled()) {
            allFiles.stream().forEach(log::debug);
        }
        Assert.assertEquals(4, allFiles.size());
        Assert.assertTrue(allFiles.contains("src\\main\\java\\two\\FileTwo.java"));
        Assert.assertTrue(allFiles.contains("src\\main\\java\\two\\lib\\Externalib.java"));
        Assert.assertTrue(allFiles.contains("src\\main\\java\\two\\AnotherFileTwo.java"));
        Assert.assertTrue(allFiles.contains("README.md"));

    }

    /**
     * Testing the GitCrawler.allFiles(...) method with an invalid path.
     */
    @Test(expected=SkillerException.class)
    public void testInvalidPath() throws SkillerException {
        Project project = new Project (1809, "Wagram");
        project.setLocationRepository("invalid path");
        scanner.allEligibleFiles(project);
   }
   
}