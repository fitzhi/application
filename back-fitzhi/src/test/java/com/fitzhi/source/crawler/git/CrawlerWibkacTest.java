package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.io.File;
import java.io.IOException;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

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

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=true" }) 
@Slf4j
public class CrawlerWibkacTest {

	// private static final String FITZHI = "application";
	private static final String FITZHI = "first-test";

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
		project.setLocationRepository(new File(String.format(DIR_GIT, FITZHI)).getAbsolutePath());
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

		RepositoryAnalysis analysis = scanner.generateAnalysis(project, repository);

		log.debug(String.format("List of %d all paths", analysis.getPathsAll().size()));
		analysis.getPathsAll().stream().forEach(path -> log.debug(path));

		log.debug(String.format("List of %d added paths", analysis.getPathsAdded().size()));
		analysis.getPathsAdded().stream().forEach(path -> log.debug(path));

		log.debug(String.format("List of %d modified paths", analysis.getPathsModified().size()));
		analysis.getPathsModified().stream().forEach(path -> log.debug(path));

		log.debug(String.format("List of %d candidate paths", analysis.getPathsCandidate().size()));
		analysis.getPathsCandidate().stream().forEach(path -> log.debug(path));

		Project p = new Project(777, "test");
		
//		dataSaver.saveChanges(p, analysis.getChanges());
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1000);
	}
}