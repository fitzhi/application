/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@TestPropertySource(properties = { "cron.tasks.report=* 0/5 * * * ?" }) 
public class CrawlerVgo {

	private static final String FILE_GIT = "../git_repo_for_test/%s";
	// private static final String FILE_GIT = "../deploy/data/repos/8";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	AsyncTask asyncTask;

	@Test
	public void testParseRepository() throws IOException, ApplicationException, GitAPIException {
		Project prj = new Project (1789, "testParseRepo");
		prj.setBranch("my-branch");
		prj.setLocationRepository(new File(FILE_GIT).getCanonicalPath());

		asyncTask.addTask(Global.DASHBOARD_GENERATION, "project", 1789);
		
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