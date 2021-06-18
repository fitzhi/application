package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;
import java.util.List;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * Testing the method {@link DataHandler#saveRepositoryDirectories(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.SourceControlChanges)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataHandlerSaveRepositoryDirectoriesTest {
    
    @Autowired
    DataHandler dataHandler;
    
	/**
	 * Test the method {@link DataHandler#saveSCMPath}
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testsaveSCMPath() throws ApplicationException {

		Project project = new Project(777, "test");
		project.setBranch("master");
		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		analysis.getChanges().addChange("/src/main/java/Test.java", new SourceChange(LocalDate.now(), 1));
		dataHandler.saveRepositoryDirectories(project, analysis.getChanges());

		List<String> directories = dataHandler.loadRepositoryDirectories(project);
		Assert.assertEquals("1 directory has been saved", 1, directories.size());
		Assert.assertEquals("/src/main/java", directories.get(0));

	}


}
