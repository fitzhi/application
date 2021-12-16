package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "patternsInclusion=.*." , "prefilterEligibility=true" }) 
public class GitCrawlerDifferentialAnalysisTest {

	private static final String FIRST_TEST = "first-test";

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
	@Qualifier("Console")
	AsyncTask asyncTask;

	@MockBean
	private SkylineProcessor skylineProcessor;

	private Repository repository;

	private Project project;
	
	@Before
	public void before() throws ApplicationException, IOException {
		project = new Project(1000, FIRST_TEST);
		project.setBranch("master");
		project.setLocationRepository(new File(String.format(DIR_GIT, FIRST_TEST)).getAbsolutePath());

		asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1000);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment().findGitDir()
				.build();
	}

	@Test
	public void firstStart() throws IOException, ApplicationException {

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);

		assertEquals(0, analysis.getChanges().getChanges().size());

		scanner.fillRepositoryAnalysis(project, analysis, repository);	

		assertEquals(9, analysis.getChanges().getChanges().size());

		scanner.fillRepositoryAnalysis(project, analysis, repository);	
		assertEquals(9, analysis.getChanges().getChanges().size());

	}

}


