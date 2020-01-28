package com.tixhi.bean.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tixhi.Global;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.data.encryption.DataEncryption;
import com.tixhi.data.internal.Project;
import com.tixhi.source.crawler.RepoScanner;
import com.tixhi.source.crawler.git.GitCrawler;

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
public class GitCrawlerTestConnectionTest {

	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	private Project project;
	
	@Before
	public void before() throws Exception {
		project = new Project(4, "UNREACHABLE PROJECT");
		project.setUrlRepository("https://github.com/fvidal/wibkac");
		project.setConnectionSettings(Global.DIRECT_ACCESS);
		project.setUsername("frvidal");
		String encryptedPassword = DataEncryption.encryptMessage("invalid password");
		project.setPassword(encryptedPassword);
		
//		projectHandler.addNewProject(project);
	}
	
	@Test
	public void testConnectionFailed() {
		Assert.assertFalse(scanner.testConnection(project));
	}
	
	@After
	public void after() throws Exception {
//		projectHandler.getProjects().remove(4);
	}

}
