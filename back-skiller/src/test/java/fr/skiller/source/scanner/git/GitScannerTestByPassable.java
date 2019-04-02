/**
 * 
 */
package fr.skiller.source.scanner.git;
import static org.assertj.core.api.Assertions.assertThat;
import static fr.skiller.Global.UNKNOWN;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.service.ResumeParserService;
import fr.skiller.source.scanner.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the implementation of the GIT scanner.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GitScannerTestByPassable {
	
	Logger logger = LoggerFactory.getLogger(GitScannerTestByPassable.class.getCanonicalName());
	
	private static File resourcesDirectory = new File("src/test/resources");

	final String fileProperties = resourcesDirectory.getAbsolutePath() + "/poc_git/properties-VEGEO.json";
	
	ConnectionSettings settings = new ConnectionSettings();
	
	/**
	 * Should we bypass the test.
	 */
	boolean bypass;
	
	@Autowired
	StaffHandler staffHandler;
	
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
	@Qualifier("messOfCriteria")
	RiskProcessor riskProcessor;

	@Before
	public void before() throws Exception {
		if ("Y".equals(System.getProperty("bypass"))) {
			bypass = true;
		}
		
		Gson gson = new GsonBuilder().create();
		final FileReader fr = new FileReader(new File(fileProperties));
		settings = gson.fromJson(fr, settings.getClass());
		fr.close();
		
		if (logger.isDebugEnabled()) {
			logger.debug("GIT remote URL " + settings.getUrl());
		}
	}

	@Test
	public void cloneAndParseRepo() throws Exception {
		
		if (bypass) return;
		
		Project project = new Project(1, "VEGEO");
		
		scanner.clone(project, settings);
        assertThat(settings.getLocalRepository()).isNotNull();
        
		final CommitRepository repo = scanner.parseRepository(project, settings);
        assertThat(repo.size()).isGreaterThan(0);
        
		RiskDashboard data = scanner.aggregateDashboard(project, repo);
		
		if (logger.isDebugEnabled()) {
			repo.contributors()
				.stream()
				.filter(contributor -> contributor.getIdStaff() != UNKNOWN)
				.forEach(idStaff -> {
					Staff staff = staffHandler.getStaff().get(idStaff);
					if (staff == null) {
						logger.debug("Do not retrieve the staff with the id "+idStaff);
					} else {
						logger.debug(staff.getLogin() + " " + staff.isActive());
					}
				});
		}
		// Evaluate the risk for each directory, and sub-directory, in the repository.
		final List<StatActivity> stats = new ArrayList<>();
		riskProcessor.evaluateTheRisk(repo, data.riskChartData, stats);
       
	}
	
	@After
	public void after() throws IOException {
		if (!bypass)
			FileUtils.deleteDirectory(new File(settings.getLocalRepository()));
	}
}
