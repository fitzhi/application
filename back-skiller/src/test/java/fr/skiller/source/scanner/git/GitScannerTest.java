/**
 * 
 */
package fr.skiller.source.scanner.git;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

import fr.skiller.data.internal.Project;
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
public class GitScannerTest {
	
	Logger logger = LoggerFactory.getLogger(GitScannerTest.class.getCanonicalName());
	
	private static File resourcesDirectory = new File("src/test/resources");

	final String fileProperties = resourcesDirectory.getAbsolutePath() + "/poc_git/properties-VEGEO.json";
	
	ConnectionSettings settings = new ConnectionSettings();
	
	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Before
	public void before() throws Exception {
		Gson gson = new GsonBuilder().create();
		final FileReader fr = new FileReader(new File(fileProperties));
		settings = gson.fromJson(fr, settings.getClass());
		fr.close();
		
		if (logger.isDebugEnabled()) {
			logger.debug("GIT remote URL " + settings.url);
		}
	}

	@Test
	public void cloneAndParseRepo() throws Exception {
		
		Project project = new Project(1, "VEGEO");
		
		scanner.clone(project, settings);
        assertThat(settings.localRepository).isNotNull();
        
		final CommitRepository repo = scanner.parseRepository(project, settings);
        assertThat(repo.size()).isGreaterThan(0);
	}
	
	@After
	public void after() throws IOException {
		FileUtils.deleteDirectory(new File(settings.localRepository));
	}
}
