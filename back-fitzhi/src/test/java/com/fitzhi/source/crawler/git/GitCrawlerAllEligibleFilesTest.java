package com.fitzhi.source.crawler.git;

import java.io.File;
import java.util.Set;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
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
 * This class is testing the method {@link GitCrawler#allEligibleFiles(Project)}
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
	 * Testing the GitCrawler.allFiles(...) method with the GitHub Project
	 * repo-test-number-of-lines
	 */
	@Test
	public void testAllEligibleFilesWithProjectRepoTestNumberOfLines() throws ApplicationException {
		Project project = new Project(1809, "Wagram");
		project.setLocationRepository(new File(String.format(REPO_DIR, TESTING_PROJECT)).getAbsolutePath());
		project.setBranch("master");

		Set<String> allFiles = scanner.allEligibleFiles(project);
		if (log.isDebugEnabled()) {
			allFiles.stream().forEach(log::debug);
		}
		Assert.assertEquals(4, allFiles.size());
		Assert.assertTrue(allFiles.contains("src" + File.separator + "main" + File.separator + "java" + File.separator
				+ "two" + File.separator + "FileTwo.java"));
		Assert.assertTrue(allFiles.contains("src" + File.separator + "main" + File.separator + "java" + File.separator
				+ "two" + File.separator + "lib" + File.separator + "Externalib.java"));
		Assert.assertTrue(allFiles.contains("src" + File.separator + "main" + File.separator + "java" + File.separator
				+ "two" + File.separator + "AnotherFileTwo.java"));
		Assert.assertTrue(allFiles.contains("README.md"));

	}

	/**
	 * Testing the GitCrawler.allFiles(...) method with an invalid path.
	 */
	@Test(expected = ApplicationException.class)
	public void testInvalidPath() throws ApplicationException {
		Project project = new Project(1809, "Wagram");
		project.setLocationRepository("invalid path");
		project.setBranch("master");
		scanner.allEligibleFiles(project);
	}

	/**
	 * <p>
	 * The directory {@code src/test/resources/fake-repo} contains only one file with a very little filename "KEYS".
	 * </p>
	 * <b>4 cars.</b>
	 * <p>
	 * The method {@link RepoScanner#allEligibleFiles(Project)} should not throw an {@link ApplicationException}
	 * </p>
	 */
	@Test
	public void testLittleFile() throws ApplicationException {
		Project project = new Project(1809, "Wagram");
		File f = new File("src/test/resources/fake-repo");
		project.setLocationRepository(f.getAbsolutePath());
		scanner.allEligibleFiles(project);
	}
}