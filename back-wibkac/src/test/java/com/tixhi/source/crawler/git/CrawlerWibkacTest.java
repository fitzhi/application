/**
 * 
 */
package com.tixhi.source.crawler.git;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

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

import com.tixhi.bean.DataChartHandler;
import com.tixhi.bean.DataHandler;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.data.internal.Project;
import com.tixhi.data.internal.RepositoryAnalysis;
import com.tixhi.data.source.ConnectionSettings;
import com.tixhi.exception.SkillerException;
import com.tixhi.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=false" }) 
public class CrawlerWibkacTest {

	private static final String WIBKAC = "wibkac";

	private static final String DIR_GIT = "../git_repo_for_test/%s/";

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

	private Repository repository;

	private Project project;
	
	@Before
	public void before() {
		project = new Project(1000, WIBKAC);
	}
	
	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, WIBKAC), analysis);
		assertTrue(
				analysis.getPathsAll().contains("front-skiller/src/assets/img/zhi.png"));

		scanner.filterEligible(analysis);

		assertFalse(analysis.getPathsAll().contains("front-skiller/src/assets/img/zhi.png"));

	}

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCleanupPaths() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, WIBKAC), analysis);
		scanner.filterEligible(analysis);
		scanner.cleanupPaths(analysis);
		analysis.getPathsAll().stream().forEach(System.out::println);

	}

	/**
	 * Test the method dataHandler.saveChanges
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSaveChanges() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);

		dataSaver.saveChanges(new Project(777, "test"), analysis.getChanges());
	}

	/**
	 * Test the method dataHandler.saveSCMPath
	 * 
	 * @throws IOException
	 */
	@Test
	public void testsaveSCMPath() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);

		Project p = new Project(777, "test");
		p.setLocationRepository(new File(String.format(FILE_GIT, WIBKAC)).getAbsolutePath());
		dataSaver.saveRepositoryDirectories(p, analysis.getChanges());
	}

	@Test
	public void testParseRepository() throws IOException, SkillerException {
		Project prj = new Project (777, "vegeo");
		prj.setLocationRepository(String.format(DIR_GIT, WIBKAC));
		projectHandler.addNewProject(prj);
		scanner.parseRepository(prj, new ConnectionSettings());
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}