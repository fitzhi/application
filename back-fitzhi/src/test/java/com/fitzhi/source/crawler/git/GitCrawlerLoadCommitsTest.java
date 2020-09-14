/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Task;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

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

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=false" })
@Slf4j
public class GitCrawlerLoadCommitsTest {

	private static final String TEST_PROJECT = "mock-repo-with-branches-for-dev-and-testing-purposes";

	private static final String DIR_GIT = ".." + File.separator + "git_repo_for_test" + File.separator + "%s"
			+ File.separator;

	private static final String FILE_GIT = DIR_GIT + ".git";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	DataHandler dataSaver;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	DataChartHandler dataChartHandler;

	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;

	@Autowired
	AsyncTask asyncTask;

	private Repository repository;

	private Project project;

	private AsyncTask tasks = new AsyncTask () {

		@Override
		public void addTask(String operation, String title, int id) throws SkillerException {
		}

		@Override
		public void removeTask(String operation, String title, int id) {
		}

		@Override
		public boolean containsTask(String operation, String title, int id) {
			return false;
		}

		@Override
		public boolean hasActiveTask(String operation, String title, int id) {
			return false;
		}

		@Override
		public Task getTask(String operation, String title, int id) {
			return null;
		}

		@Override
		public boolean logMessage(String operation, String title, int id, String message) {
			return false;
		}

		@Override
		public boolean logMessage(String operation, String title, int id, int errorCode, String message) {
			return false;
		}

		@Override
		public void completeTask(String operation, String title, int id) throws SkillerException {
		}

		@Override
		public void completeTaskOnError(String operation, String title, int id) throws SkillerException {
		}

		@Override
		public String trace() {
			return null;
		}

	};

	@Before
	public void before() throws SkillerException {
		project = new Project(1000, TEST_PROJECT);

	}

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadCommits() throws SkillerException, IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Repository location %s", new File(String.format(FILE_GIT, TEST_PROJECT)).getAbsolutePath()));
		}
		
		repository = builder.setGitDir(new File(String.format(FILE_GIT, TEST_PROJECT))).readEnvironment().findGitDir()
				.build();


		Collection<RevCommit> commits = GitCrawler.loadCommits(project, repository, tasks);	
		Assert.assertEquals("no doublon in the resulting list", 2, commits.size());;
	}
	

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
 	}
}