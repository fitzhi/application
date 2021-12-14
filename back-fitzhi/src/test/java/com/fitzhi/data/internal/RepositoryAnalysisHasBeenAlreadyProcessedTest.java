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
		Project project = new Project(777, "Project 777");
		SourceControlChanges scc = this.dataHandler.loadChanges(project);
		RepositoryAnalysis repositoryAnalysis = new RepositoryAnalysis(project);
		repositoryAnalysis.setChanges(scc);
		return repositoryAnalysis;
	}
	
	@Test
	public void hasAlreadyBeenRegistered() throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertTrue("A registered commit has been retrieved in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed("commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp"));
	}

	@Test
	public void isNewCommit()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis= init();
		Assert.assertTrue("A NEW commit should be taken in account", 
			repositoryAnalysis.hasBeenAlreadyProcessed("commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp"));
	}

	@Test
	public void hasFilePathAlreadyBeenRegistered()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertTrue("A registered commit has been retrieved in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"moduleCopyOfC/fic_in_C_1.txt",
				"commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp"));
	}

	@Test
	public void filePathExistButNotThisCommit()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertFalse("Not registered commit for the given file in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"moduleCopyOfC/fic_in_C_1.txt",
				"unknown commit"));
	}

	@Test
	public void CommitExistButNotTheFilePath()  throws ApplicationException {
		RepositoryAnalysis repositoryAnalysis = init();
		Assert.assertFalse("The given commit does not concern this file path", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp",
				"unknown.txt"));
	}

}
 