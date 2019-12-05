package fr.skiller.source.crawler.git;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.data.internal.Project;
import fr.skiller.source.crawler.RepoScanner;

/**
 * Test of the method {@link GitCrawler#loadRepositoryFromCacheIfAny(fr.skiller.data.internal.Project)}
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

	/**
	 * Active project.
	 */
	Project project;
	
	@Test
	public void testLoadRepositoryFromExistingCacheIfAnyTest() throws IOException {
		project = new Project(1789, "Revolutionnary project");
		Assert.assertNotNull(scanner.loadRepositoryFromCacheIfAny(project));
	}
	
	@Test
	public void testLoadRepositoryFromNonExistingCacheIfAnyTest() throws IOException {
		project = new Project(1789, "Unregistered project");
		Assert.assertNull(scanner.loadRepositoryFromCacheIfAny(project));
	}
	
}
