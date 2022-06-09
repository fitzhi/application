/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.io.File;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
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
 * 
 * Testing the method {@link GitCrawler#initialCommit(Git)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "patternsInclusion=.*." , "prefilterEligibility=true" }) 
public class GitCrawlerInitialCommitTest {

	private static final String FIRST_TEST = "first-test/";

	private static final String DIR_GIT = "../git_repo_for_test/%s";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	AsyncTask asyncTask;

	private Repository repository;

	@Before
	public void before() throws Exception {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment().findGitDir()
				.build();
	}
	
	@Test
	public void test() throws Exception {
		
		try (Git git = new Git(repository)) {
			RevCommit commit = scanner.initialCommit(git);
			Assert.assertEquals("66bd3dadc9ddc42472229dc870c65a40977c1a4e", commit.getName());
		}

	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}
