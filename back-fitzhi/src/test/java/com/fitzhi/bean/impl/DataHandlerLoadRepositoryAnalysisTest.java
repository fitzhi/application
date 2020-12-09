package com.fitzhi.bean.impl;

import java.io.File;
import java.time.LocalDate;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Testing the mehods {@link DataHandler#loadRepositoryAnalysis(Project)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerLoadRepositoryAnalysisTest {

    @Autowired
    DataHandler dataHandler;

    @Test
    public void test() throws SkillerException {
        Project project = new Project(1796, "Castiglione");
        project.setBranch("master");
        RepositoryAnalysis analysis = new RepositoryAnalysis(project);

		analysis.getChanges().addChange("/src/main/java/Test.java", new SourceChange(LocalDate.now(), 1));
        analysis.getChanges().addChange("/src/main/java/com/fitzhi/bean/Test.java", new SourceChange(LocalDate.now(), 2));
        
        analysis.getPathsAdded().add("pathAddedOne");
        analysis.getPathsAdded().add("pathAddedTwo");

        analysis.getPathsModified().add("pathModifiedOne");
        analysis.getPathsModified().add("pathModifiedTwo");
        analysis.getPathsModified().add("pathModifiedThree");

        analysis.getPathsCandidate().add("candidate One");
        analysis.getPathsCandidate().add("candidate Two");

        // Saving the analysis
        log.debug(String.format("Loading project %s", project.getName()));
        dataHandler.saveRepositoryAnalysis(project, analysis);

        RepositoryAnalysis loadedAnalysis = dataHandler.loadRepositoryAnalysis(project);
        Assert.assertNotNull("loadedAnalysis is not null", loadedAnalysis);
        Assert.assertNotNull("loadedAnalysis.getChanges() is not null", loadedAnalysis.getChanges());
        Assert.assertEquals("Changes file must contain 2 records", 2, loadedAnalysis.getChanges().keySet().size());
        Assert.assertEquals("Added paths file must contain 2 records", 2, loadedAnalysis.getPathsAdded().size());
        Assert.assertEquals("Modified paths file must contain 3 records", 3, loadedAnalysis.getPathsModified().size());
        Assert.assertEquals("Candidate paths file must contain 2 records", 2, loadedAnalysis.getPathsCandidate().size());
    }
}
