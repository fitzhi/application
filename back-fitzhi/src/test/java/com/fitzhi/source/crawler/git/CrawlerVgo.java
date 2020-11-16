/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CrawlerVgo {

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
			log.info("testParseRepo is unplugged on this environment.");
			return;
		}
		
		prj.setLocationRepository(new File(String.format(FILE_GIT, "testParseRepo")).getCanonicalPath());
		
		scanner.parseRepository(prj);
	}

}