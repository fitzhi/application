package com.fitzhi.data.internal;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of the method {@link RepositoryAnalysis#hasBeenAlreadyProcessed(String)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoryAnalysisHasBeenAlreadyProcessedTest {

	@Autowired
	DataHandler dataHandler;

	public RepositoryAnalysis init() throws ApplicationException {
		Project project = new Project(841, "Project 841");
		SourceControlChanges scc = this.dataHandler.loadChanges(project);
		RepositoryAnalysis repositoryAnalysis = new RepositoryAnalysis(project);
		repositoryAnalysis.setChanges(scc);
		return repositoryAnalysis;
	}
	
	@Test
	public void hasAlreadyBeenRegistered() throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertTrue("A registered commit has been retrieved in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed("commit de8ab3de2c39b6e07bb8fdf1979cfff034220983 1495632158 ----sp"));
	}

	@Test
	public void isNewCommit()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis= init();
		Assert.assertFalse("A NEW commit should be taken in account", repositoryAnalysis.hasBeenAlreadyProcessed("new commit -----sp"));
	}

	@Test
	public void hasFilePathAlreadyBeenRegistered()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertTrue("A registered commit has been retrieved in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"/abcde/fghij/VersionService.java",
				"commit de8ab3de2c39b6e07bb8fdf1979cfff034220983 1495632158 ----sp"));
	}

	@Test
	public void filePathExistButNotThisCommit()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertFalse("Not registered commit for the given file in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"/abcde/fghijk/VersionService.java",
				"unknown commit"));
	}

	@Test
	public void commitExistButNotTheFilePath()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertFalse("The given commit does not concern this file path", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"commit 69ae7e4f2d76c1c813de09045893a73155e1a3ad 1417797509 ----sp",
				"unknown.txt"));
	}

}
 