package com.fitzhi.source.crawler.git;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

/**
 * Test of the method {@link GitCrawler#loadRepositoryFromCacheIfAny(com.fitzhi.data.internal.Project)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cacheDirRepository=./src/test/resources/cacheDirRepository/", "cache_duration=100000" }) 
public class GitCrawlerLoadRepositoryFromCacheIfAnyTest {

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws Exception {		
		Project project = new Project(1789, "Revolutionnary project");
		projectHandler.addNewProject(project);
	}
	
	@Test
	public void testLoadRepositoryFromExistingCacheIfAnyTest() throws IOException, SkillerException {
		Project project = projectHandler.get(1789);
		Assert.assertNotNull(scanner.loadRepositoryFromCacheIfAny(project));
	}
	
	@Test
	public void testLoadRepositoryFromNonExistingCacheIfAnyTest() throws IOException, SkillerException {
		Project project = new Project(1792, "Unregistered project");
		Assert.assertNull(scanner.loadRepositoryFromCacheIfAny(project));
	}
	
	@After
	public void after()throws Exception {
		projectHandler.getProjects().remove(1789);
	}
}
