/**
 * 
 */
package fr.skiller.source.crawler.git;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the implementation of the GIT scanner.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
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
		
		Assert.assertEquals( 
				"TOTO/fr/test/MyClass.java",
				scanner.cleanupPath("TOTO/src/main/java/fr/test/MyClass.java"));
		
	}
	
	@After
	public void after() throws IOException {
	}
}
