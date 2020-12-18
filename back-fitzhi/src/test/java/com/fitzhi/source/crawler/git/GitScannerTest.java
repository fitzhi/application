/**
 * 
 */
package com.fitzhi.source.crawler.git;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the implementation of the GIT scanner.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "cache_duration=0" }) 
public class GitScannerTest {
	
	Logger logger = LoggerFactory.getLogger(GitScannerTest.class.getCanonicalName());

	@Value("${versionControl.ConnectionSettings}")
	private String versionControlConnectionSettings;

	ConnectionSettings settings = new ConnectionSettings();
		
	@Autowired
	StaffHandler staffHandler;
	
	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	DataChartHandler dataChartHandler;
	
	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	/**
	 * Risk evaluation processor
	 */
	@Autowired
	@Qualifier("commitAndDevActive")
	RiskProcessor riskProcessor;

	@Before
	public void before() throws IOException, ApplicationException {

		Gson gson = new GsonBuilder().create();
		
		String fileProperties = versionControlConnectionSettings + "/properties-FITZHI.json";		

		File file = new File(fileProperties);
		
		// This test is only available on the PIC of Fitzhì
		assertThat (!file.exists());

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("connection settings pathname %s", file.getAbsolutePath()));
		}
		
		final FileReader fr = new FileReader(file);
		settings = gson.fromJson(fr, settings.getClass());
		fr.close();
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("GIT remote URL %s", settings.getUrl()));
		}

		projectHandler.get(2).setLocationRepository(null);		
	}
	
	@Test
	public void emptyTest() {
		// Empty test to avoid a test execution error if the cloneAndParseRepo is commented.
		// cloneAndParseRepo() is a test which might last a long while.
	}
	
	// @Test
	public void cloneAndParseRepo() throws IOException, ApplicationException, GitAPIException {

		Project project = projectHandler.get(2);
		
		scanner.clone(project, settings);
        assertThat(project.getLocationRepository()).isNotNull();
        
		final CommitRepository repo = scanner.parseRepository(project);
        assertThat(repo.size()).isGreaterThan(0);
        
		RiskDashboard data = scanner.aggregateDashboard(project, repo);
		
		// Evaluate the risk for each directory, and sub-directory, in the repository.
		final List<StatActivity> stats = new ArrayList<>();
		riskProcessor.evaluateTheRisk(repo, data.riskChartData, stats);
		
		StringBuilder dump = new StringBuilder();
		data.riskChartData.dump(dump, "");
		if (logger.isDebugEnabled()) {
			logger.debug(dump.toString());
		}
		
		dataChartHandler.aggregateDataChart(data.riskChartData);
		
		scanner.aggregateDashboard(project, repo);
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.get(2).setLocationRepository(null);		
	}
}
