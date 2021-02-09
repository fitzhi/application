package com.fitzhi.source.crawler.git;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * Testing the method {@link GitCrawler#filterEligible(RepositoryAnalysis)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerFilterEligibleTest {

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	private Project project;
	
	
	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException, ApplicationException {

		project = new Project(1000, "Fitzhi");

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);		
		analysis.getChanges().addChange("front-fitzhi/src/assets/img/zhi.png", new SourceChange(LocalDate.now(), 1));
		
		assertTrue(
				analysis.getPathsAll().contains("front-fitzhi/src/assets/img/zhi.png"));

		scanner.filterEligible(analysis);

		assertFalse(analysis.getPathsAll().contains("front-fitzhi/src/assets/img/zhi.png"));

	}

}