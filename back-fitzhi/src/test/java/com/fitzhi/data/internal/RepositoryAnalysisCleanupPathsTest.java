package com.fitzhi.data.internal;

import java.io.IOException;
import java.time.LocalDate;

import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
/**
 * Testing {@link RepositoryAnalysis#cleanupPaths(com.fitzhi.bean.ProjectDashboardCustomizer)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class RepositoryAnalysisCleanupPathsTest {
	
	@Autowired
	ProjectDashboardCustomizer pdb;

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCleanupPaths() throws IOException, SkillerException {

		Project project = new Project(1515, "Marignan");
		RepositoryAnalysis ra = new RepositoryAnalysis(project);
		ra.getChanges().addChange("/src/main/java/Test.java", new SourceChange(LocalDate.now(), 1));
		Assert.assertTrue(ra.getChanges().keySet().contains("/src/main/java/Test.java"));
		ra.cleanupPaths(pdb);
		Assert.assertFalse("/src/main/java/Test.java should be not any present", ra.getChanges().keySet().contains("/src/main/java/Test.java"));
		Assert.assertTrue("/Test.java should have replaced /src/main/java/Test.java",  ra.getChanges().keySet().contains("/Test.java"));

		
	}


}
