/**
 * 
 */
package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "patternsInclusion=.*." }) 
public class CrawlerFirstTest {

	private static final String FIRST_TEST = "first-test/";

	private static final String DIR_GIT = "../git_repo_for_test/%s";

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
	AsyncTask asyncTask;

	private Repository repository;

	private Project project;
	
	@Before
	public void before() throws SkillerException {
		project = new Project(1000, FIRST_TEST);
    	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1000);
	}
	
	@Test
	public void loadChangesForFirstTest() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);

		assertFalse(analysis.getPathsAll().contains("moduleA/test.txt"));
		assertTrue(analysis.getPathsAll().contains("moduleB/test.txt"));

		assertFalse(analysis.getPathsAll().contains("moduleA/creationInA.txt"));
		// This file has been updated and renamed, and therefore not detected as a
		// rename by the JGIT RenameDetector
		assertTrue(analysis.getPathsAll().contains("moduleAchanged/creationInA.txt"));

		// At this level if a java class move from one package to one another, system
		// does not detect it
		assertFalse(
				analysis.getPathsAll().contains("com/application/packageA/MyClass.java"));
		assertTrue(
				analysis.getPathsAll().contains("com/application/packageB/MyClass.java"));

	}

	@Test
	public void finalizeListChangesForFirstTest() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		
		assertTrue (new File(String.format(DIR_GIT, FIRST_TEST)).getAbsolutePath(), new File(String.format(DIR_GIT, FIRST_TEST)).exists());
		
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST + "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		
		scanner.finalizeListChanges(String.format(DIR_GIT, FIRST_TEST), analysis);
		
		assertFalse(analysis.getChanges().keySet().contains("moduleA/creationInA.txt"));
		//
		// This file has been updated and renamed, and therefore not detected as a
		// rename by the JGIT RenameDetector
		//
		assertTrue(analysis.getChanges().keySet().contains("moduleAchanged/creationInA.txt"));
	}


	public void testDebug() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis =  scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, FIRST_TEST), analysis);
		analysis.getPathsAll().stream().forEach(System.out::println);
	}

	@Test
	public void testParseRepository() throws IOException, SkillerException, GitAPIException {
		Project prj = new Project (777, "First test");
		projectHandler.addNewProject(prj);
		prj.setLocationRepository(String.format(DIR_GIT, FIRST_TEST));
		scanner.parseRepository(prj, new ConnectionSettings());
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1000);
	}
}