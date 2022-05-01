package com.fitzhi.source.crawler.git;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import com.fitzhi.exception.ApplicationException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class is testing the method {@link GitCrawler#checkBranchNameExist(Git, String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
public class GitCrawlerCheckBranchNameExistTest {

	private static final String FIRST_TEST = "first-test";

	private static final String DIR_GIT = "../git_repo_for_test/%s";

	@Test
	public void branchFound() throws IOException, ApplicationException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment()
			.findGitDir().build();
		Git git = new Git(repository);
		GitCrawler.checkBranchNameExist(git, "master");
	}
	
	@Test (expected = ApplicationException.class)
	public void branchNotFound() throws IOException, ApplicationException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment()
			.findGitDir().build();
		Git git = new Git(repository);
		GitCrawler.checkBranchNameExist(git, "unknown");
	}

	@Test (expected = ApplicationException.class)
	public void empty() throws IOException, ApplicationException, GitAPIException {

		ListBranchCommand mockListBranchCommand = mock(ListBranchCommand.class);
		when(mockListBranchCommand.call()).thenReturn(Collections.emptyList());

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment()
			.findGitDir().build();
		Git git = new Git(repository);
		Git spyGit = spy(git);
		when(spyGit.branchList()).thenReturn(mockListBranchCommand);

		GitCrawler.checkBranchNameExist(spyGit, "master");
	}

	


}
