/**
 * 
 */
package com.fitzhi.source.crawler.git;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectDashboardCustomizer;

/**
 * <p>Testing the implementation of {@link com.fitzhi.bean.ProjectDashboardCustomizer#cleanupPath(String)}</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectDashboardCustomizerCleanupPathTest {
	
	Logger logger = LoggerFactory.getLogger(ProjectDashboardCustomizerCleanupPathTest.class.getCanonicalName());
	
	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;

	@Test
	public void cloneAndParseRepo()  {
		
		Assert.assertEquals( 
				"TOTO/fr/test/MyClass.java",
				projectDashboardCustomizer.cleanupPath("TOTO/src/main/java/fr/test/MyClass.java"));
		
	}
	
}
