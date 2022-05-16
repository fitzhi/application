package com.fitzhi.source.crawler.git;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link RepoScanner#clone(com.fitzhi.data.internal.Project, com.fitzhi.data.source.ConnectionSettings)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RepoScannerCloneValidTest {
 
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	ProjectHandler projectHandler;

	@Before
	public void before() throws Exception {
		Project project = new Project(1790, "One year after the great Revolution");
		project.setBranch("main");
		projectHandler.addNewProject(project);
	}

	@Test
	public void validPassword() throws Exception {
		Project project = projectHandler.getProject(1790);
		ConnectionSettings settings = new ConnectionSettings();
		settings.setPublicRepository(false);
		settings.setUrl("https://github.com/frvidal/test.git");
		settings.setLogin("frvidal");
		settings.setPassword(System.getenv("TOKEN_FRVIDAL"));

		Thread.sleep(1000);
		
		scanner.clone(project, settings);
	}

	@After
	public void after() throws Exception {
		projectHandler.removeProject(1790);
	}

}
