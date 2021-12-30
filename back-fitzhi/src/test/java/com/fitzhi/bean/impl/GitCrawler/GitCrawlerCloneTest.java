package com.fitzhi.bean.impl.GitCrawler;

import static com.fitzhi.service.ConnectionSettingsType.PUBLIC_LOGIN;

import java.io.File;
import java.io.IOException;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * <p>
 * Testing the method {@link GitCrawler#testConnection(Project)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerCloneTest {

	private final Logger logger = LoggerFactory.getLogger(GitCrawlerCloneTest.class.getCanonicalName());

	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	RepoScanner scanner;
	
	private final int ID_PROJECT = 1789;
	
	@Before
	public void before() throws ApplicationException {
		Project project = new Project(ID_PROJECT, "Revolutionary_Project");
		project.setConnectionSettings(PUBLIC_LOGIN);
		project.setUrlRepository("https://github.com/fitzhi/application");

		projectHandler.addNewProject(project);
	}
	
	@Test
	public void testClone() throws GitAPIException, ApplicationException, IOException {
		
		Project project = projectHandler.lookup(ID_PROJECT);
		
		ConnectionSettings settings = new ConnectionSettings();
		settings.setPublicRepository(true);
		
		settings.setUrl(project.getUrlRepository());
		
		scanner.clone(project, settings);		
		logger.debug(project.getLocationRepository());

		scanner.clone(project, settings);		
	}
	
	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(ID_PROJECT);

	}

	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
}
