/**
 * 
 */
package com.fitzhi.source.crawler.git;

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

import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
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
@TestPropertySource(properties = { "prefilterEligibility=false" }) 
public class CrawlerWibkacTest {

	private static final String FITZHI = "application";

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

	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;
	
	private Repository repository;

	private Project project;
	
	@Before
	public void before() {
		project = new Project(1000, FITZHI);
	}
	
	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, FITZHI))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, FITZHI), analysis);
		assertTrue(
				analysis.getPathsAll().contains("front-fitzhi/src/assets/img/zhi.png"));

		scanner.filterEligible(analysis);

		assertFalse(analysis.getPathsAll().contains("front-fitzhi/src/assets/img/zhi.png"));

	}

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCleanupPaths() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, FITZHI))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, FITZHI), analysis);
		scanner.filterEligible(analysis);
		analysis.cleanupPaths(projectDashboardCustomizer);
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
		repository = builder.setGitDir(new File(String.format(FILE_GIT, FITZHI))).readEnvironment().findGitDir()
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
		repository = builder.setGitDir(new File(String.format(FILE_GIT, FITZHI))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);

		Project p = new Project(777, "test");
		p.setLocationRepository(new File(String.format(FILE_GIT, FITZHI)).getAbsolutePath());
		dataSaver.saveRepositoryDirectories(p, analysis.getChanges());
	}

	@Test
	public void testParseRepository() throws IOException, SkillerException {
		Project prj = new Project (777, "vegeo");
		prj.setLocationRepository(String.format(DIR_GIT, FITZHI));
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