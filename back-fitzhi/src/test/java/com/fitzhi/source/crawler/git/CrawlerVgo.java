/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerVgo {

	/**
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(CrawlerVgo.class.getCanonicalName());
	
	private static final String FILE_GIT = "../git_repo_for_test/%s";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Test
	public void testParseRepository() throws IOException, SkillerException, GitAPIException {
		Project prj = new Project (777, "testParseRepo");
		
		File f = new File(String.format(FILE_GIT, "testParseRepo"));
		
		// This kind of test is supposed to be executed on our IC platform.
		if (!f.exists()) {
			logger.info("testParseRepo is unplugged on this environment.");
			return;
		}
		
		prj.setLocationRepository(new File(String.format(FILE_GIT, "testParseRepo")).getCanonicalPath());
		
		scanner.parseRepository(prj);
	}

}