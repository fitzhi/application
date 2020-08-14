package com.fitzhi.bean.impl;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.Global;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

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
	public void before() throws SkillerException {
		Project project = new Project(ID_PROJECT, "Revolutionary_Project");
		project.setConnectionSettings(Global.NO_USER_PASSWORD_ACCESS);
		project.setUrlRepository("https://github.com/fitzhi/application");

		projectHandler.addNewProject(project);
	}
	
	@Test
	public void testClone() throws GitAPIException, SkillerException, IOException {
		
		Project project = projectHandler.get(ID_PROJECT);
		
		ConnectionSettings settings = new ConnectionSettings();
		settings.setPublicRepository(true);
		
		settings.setUrl(project.getUrlRepository());
		
		scanner.clone(project, settings);		
		logger.debug(project.getLocationRepository());

		scanner.clone(project, settings);		
	}
	
	@After
	public void after() throws SkillerException {
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
