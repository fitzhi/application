package com.fitzhi.bean.impl;

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
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

/**
 * 
 * <p>
 * Testing the method {@link GitCrawler#loadRepositoryFromCacheIfAny(com.fitzhi.data.internal.Project)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "cache.working.dir=./src/test/resources/cacheDirRepository/", "cache_duration=100000" }) 
public class GitCrawlerLoadRepositoryFromCacheIfAnyTest {

	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	@Before
	public void before() throws Exception {
		Project project = new Project(4, "NOPE");
		projectHandler.addNewProject(project);
	}
	
	@Test
	public void testProjectNope() throws IOException, SkillerException {
		Project project = projectHandler.get(4);
		CommitRepository repository = scanner.loadRepositoryFromCacheIfAny(project);
		Assert.assertFalse(repository.unknownContributors().isEmpty());
		String[] ghosts = (String[]) project.getGhosts().stream().map(Ghost::getPseudo).toArray(String[]::new);
		Assert.assertArrayEquals(repository.unknownContributors().toArray(new String[0]), ghosts);
	}
	
	@After
	public void after() throws Exception {
		projectHandler.getProjects().remove(4);
	}

}
