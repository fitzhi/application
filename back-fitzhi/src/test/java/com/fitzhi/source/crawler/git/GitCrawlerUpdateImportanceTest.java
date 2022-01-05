package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.DataChart;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * 
 * Testing the processing of the importance in DataChart.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GitCrawlerUpdateImportanceTest {

	private static final String TEST_PROJECT_ROCROI = "mock-repo-with-branches-for-dev-and-testing-purposes";

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

	private long lengthOfFile = 0;

	@Before
	public void before() throws ApplicationException {
		project = new Project(1643, TEST_PROJECT_ROCROI);
		project.setBranch("master");
		projectHandler.addNewProject(project);
		project.setLocationRepository(String.format(DIR_GIT, TEST_PROJECT_ROCROI));
		project.setGhosts(new ArrayList<Ghost>());
	
		File test = new File(project.getLocationRepository() + "/src/main/java/fr/test/main/Test.java");
		Assert.assertEquals(true, test.exists());
		lengthOfFile = test.length();

		asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1643);
	}

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdateImportance() throws ApplicationException, IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Repository location %s", new File(String.format(FILE_GIT, TEST_PROJECT_ROCROI)).getAbsolutePath()));
		}
	
		repository = builder.setGitDir(new File(String.format(FILE_GIT, TEST_PROJECT_ROCROI))).readEnvironment().findGitDir()
				.build();
		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		
		scanner.updateImportance(project, analysis);

		Assert.assertEquals(true, analysis.getChanges().getChanges().containsKey("src/main/java/fr/test/main/Test.java"));
		SourceFileHistory sourceFileHistory = analysis.getChanges().getChanges().get("src/main/java/fr/test/main/Test.java"); 
		Assert.assertEquals(lengthOfFile, sourceFileHistory.getImportance());;
 
	}
	
	@Test
	public void loadChangesForFirstTest() throws IOException, ApplicationException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(project.getLocationRepository() + "/.git")).readEnvironment().findGitDir()
				.build();

		CommitRepository commitRepository = scanner.parseRepository(project);
		Assert.assertNotNull(commitRepository);

		RiskDashboard data = scanner.aggregateDashboard(project, commitRepository);
		Assert.assertNotNull(data);

		DataChart dataChart = data.riskChartData.getChildren().get(0);
		Assert.assertEquals("src", dataChart.getLocation());
		
		dataChart = dataChart.getChildren().get(0);
		Assert.assertEquals("main", dataChart.getLocation());

		dataChart = dataChart.getChildren().get(0);
		Assert.assertEquals("java", dataChart.getLocation());

		dataChart = dataChart.getChildren().get(0);
		Assert.assertEquals("fr", dataChart.getLocation());

		dataChart = dataChart.getChildren().get(0);
		Assert.assertEquals("test", dataChart.getLocation());

		
		dataChartHandler.aggregateDataChart(data.riskChartData);
		Assert.assertNotNull(data.riskChartData);
		
		dataChart = data.riskChartData.getChildren().get(0);
		Assert.assertEquals("src/main/java/fr/test/main", dataChart.getLocation());
		Assert.assertEquals(lengthOfFile, dataChart.getImportance());
	}

	@After
	public void after() throws ApplicationException {
		if (repository != null) {
			repository.close();
		}
		projectHandler.removeProject(1643);
		asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1643);
 	}
}