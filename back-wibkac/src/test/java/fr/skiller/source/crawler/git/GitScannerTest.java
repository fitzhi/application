/**
 * 
 */
package fr.skiller.source.crawler.git;
import static fr.skiller.Global.UNKNOWN;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;

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
	public void before() throws IOException {

		Gson gson = new GsonBuilder().create();
		
		String fileProperties = versionControlConnectionSettings + "/properties-SKILLER.json";		
		
		File file = new File(fileProperties);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("connection settings pathname %s", file.getAbsolutePath()));
		}
		
		final FileReader fr = new FileReader(file);
		settings = gson.fromJson(fr, settings.getClass());
		fr.close();
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("GIT remote URL %s", settings.getUrl()));
		}
		
	}
	@Test
	public void cloneAndParseRepo() throws IOException, SkillerException, GitAPIException {

		
		Project project = new Project(2, "skiller");
		
		scanner.clone(project, settings);
        assertThat(settings.getLocalRepository()).isNotNull();
        
		final CommitRepository repo = scanner.parseRepository(project, settings);
        assertThat(repo.size()).isGreaterThan(0);
        
		RiskDashboard data = scanner.aggregateDashboard(project, repo);
		
		if (logger.isDebugEnabled()) {
			repo.contributors()
				.stream()
				.filter(contributor -> contributor.getIdStaff() != UNKNOWN)
				.forEach(contributor -> {
					Staff staff = staffHandler.getStaff().get(contributor.getIdStaff());
					if (staff == null) {
						logger.debug(String.format("Do not retrieve the staff with the id %d", contributor.getIdStaff()));
					} else {
						logger.debug(String.format("%s %s", staff.getLogin(), staff.isActive()));
					}
				});
		}
		// Evaluate the risk for each directory, and sub-directory, in the repository.
		final List<StatActivity> stats = new ArrayList<>();
		riskProcessor.evaluateTheRisk(repo, data.riskChartData, stats);
       
	}
	
}
