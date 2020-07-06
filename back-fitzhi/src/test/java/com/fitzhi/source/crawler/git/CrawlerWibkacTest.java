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
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=false" }) 
@Slf4j
public class CrawlerWibkacTest {

	private static final String FITZHI = "application";

	private static final String DIR_GIT = ".." + File.separator + "git_repo_for_test" + File.separator + "%s" + File.separator;

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
	
	@Before
	public void before() throws SkillerException {
		project = new Project(1000, FITZHI);
    	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1000);
	}
	
	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Repository location %s", new File(String.format(FILE_GIT, FITZHI)).getAbsolutePath()));
		}
		
		repository = builder.setGitDir(new File(String.format(FILE_GIT, FITZHI))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);		
		scanner.finalizeListChanges(String.format(DIR_GIT, FITZHI), analysis);
		
		if (log.isDebugEnabled()) {
			log.debug("analysis.getPathsAll() content : ");
			analysis.getPathsAll().stream().forEach(log::debug);
		}
		
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
		Project p = new Project(777, "test");
		
		dataSaver.saveChanges(p, analysis.getChanges());
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
    	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 777);
		dataSaver.saveRepositoryDirectories(p, analysis.getChanges());
	}

	@Test
	public void testParseRepository() throws IOException, SkillerException {
		Project prj = new Project (777, "vegeo");
	   	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 777);
		prj.setLocationRepository(String.format(DIR_GIT, FITZHI));
		projectHandler.addNewProject(prj);
		scanner.parseRepository(prj, new ConnectionSettings());
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1000);
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 777);
	}
}