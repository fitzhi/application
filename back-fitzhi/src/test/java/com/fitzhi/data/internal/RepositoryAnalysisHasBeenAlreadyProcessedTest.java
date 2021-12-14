package com.fitzhi.data.internal;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ch.qos.logback.core.net.SyslogOutputStream;

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

	private RepositoryAnalysis repositoryAnalysis;

	@Before
	public void before() throws ApplicationException {
		Project project = new Project(777, "Project 777");
		SourceControlChanges scc = this.dataHandler.loadChanges(project);

		repositoryAnalysis = new RepositoryAnalysis(project);
		repositoryAnalysis.setChanges(scc);

		System.out.println ("Repository created successfully.")
	}
	
	@Test
	public void hasAlreadyBeenRegistered() {
		Assert.assertTrue("A registered commit has been retrieved in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed("commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp"));
	}

	@Test
	public void isNewCommit() {
		Assert.assertTrue("A NEW commit should be taken in account", 
			repositoryAnalysis.hasBeenAlreadyProcessed("commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp"));
	}

	@Test
	public void hasFilePathAlreadyBeenRegistered() {
		Assert.assertTrue("A registered commit has been retrieved in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"moduleCopyOfC/fic_in_C_1.txt",
				"commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp"));
	}

	@Test
	public void filePathExistButNotThisCommit() {
		Assert.assertFalse("Not registered commit for the given file in history", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"moduleCopyOfC/fic_in_C_1.txt",
				"unknown commit"));
	}

	@Test
	public void CommitExistButNotTheFilePath() {
		Assert.assertFalse("The given commit does not concern this file path", 
			repositoryAnalysis.hasBeenAlreadyProcessed(
				"commit ed0e618127f4669f66106e33c8b1965a0a0b56e7 1561109606 -----sp",
				"unknown.txt"));
	}

}
 