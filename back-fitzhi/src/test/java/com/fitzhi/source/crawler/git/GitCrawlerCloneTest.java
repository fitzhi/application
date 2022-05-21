package com.fitzhi.source.crawler.git;

import java.nio.file.Paths;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
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
 * Testing the nominal behavior or the failed behavior
 * for the method {@link GitCrawler#clone(com.fitzhi.data.internal.Project, com.fitzhi.data.source.ConnectionSettings) Git.clone()}
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "gitcrawler.repositories.location=../repos_test" }) 
@Slf4j
public class GitCrawlerCloneTest {

	@Autowired
	@Qualifier("GIT")
	RepoScanner repoScanner;

	@Autowired
	ProjectHandler projectHandler;

	@Before
	public void before() throws Exception {
		
		projectHandler.addNewProject(new Project(1515, "Marignan"));
		projectHandler.dataAreSaved();
		projectHandler.getProject(1515).setBranch("master");

		projectHandler.addNewProject(new Project(1214, "Bouvines"));	
		projectHandler.dataAreSaved();
		projectHandler.getProject(1214).setBranch("master");
	}

	/**
	 * The application stores the local repositories into {@code gitcrawler.repositories.location}
	 * @throws Exception
	 */
	@Test
	public void testCloneSimpleProject() throws Exception {
		Project project = projectHandler.lookup(1515);

		ConnectionSettings settings = new ConnectionSettings();
		settings.setPublicRepository(true);
		settings.setUrl("https://github.com/frvidal/first-test.git");
		repoScanner.clone(project, settings);
		log.debug(project.getLocationRepository());

		// // The removal of the directory has to be successful, because the clone operation succeeds
		GitCrawler.removeCloneDir(Paths.get("../repos_test/1515"));
	}

	/**
	 * Handling the exception when the branch does not exist (anymore?)
	 * @throws Exception
	 */
	@Test(expected = GitAPIException.class)
	public void invalidBranchName() throws Exception {
		Project project = projectHandler.lookup(1214);

		project.setBranch("undefined");
		ConnectionSettings settings = new ConnectionSettings();
		settings.setPublicRepository(true);
		settings.setUrl("https://github.com/frvidal/first-test.git");
		repoScanner.clone(project, settings);
	} 

	@After
	public void after() throws Exception {
		projectHandler.removeProject(1515);
		projectHandler.removeProject(1214);
		try {
			GitCrawler.removeCloneDir(Paths.get("../repos_test/1214"));
		} catch (final Exception e) {}
	}
}
