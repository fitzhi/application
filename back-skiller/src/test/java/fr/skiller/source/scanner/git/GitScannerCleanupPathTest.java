/**
 * 
 */
package fr.skiller.source.scanner.git;
import static org.assertj.core.api.Assertions.assertThat;
import static fr.skiller.Global.UNKNOWN;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
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

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.RiskChartData;
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
public class GitScannerCleanupPathTest {
	
	Logger logger = LoggerFactory.getLogger(GitScannerCleanupPathTest.class.getCanonicalName());
	
	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Test
	public void cloneAndParseRepo() throws Exception {
		
		System.out.println(scanner.cleanupPath("TOTO/src/main/java/fr/test/MyClass.java"));
		Assert.assertEquals( 
				"TOTO/fr/test/MyClass.java",
				scanner.cleanupPath("TOTO/src/main/java/fr/test/MyClass.java"));
		
	}
	
	@After
	public void after() throws IOException {
	}
}
