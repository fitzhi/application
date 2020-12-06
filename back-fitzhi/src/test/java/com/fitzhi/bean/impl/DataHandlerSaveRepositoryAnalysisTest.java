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
 * Testing the mehods {@link DataHandler#saveRepositoryAnalysis(com.fitzhi.data.internal.Project, com.fitzhi.data.internal.RepositoryAnalysis)}
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataHandlerSaveRepositoryAnalysisTest {

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
        log.debug(String.format("Saving project %s", project.getName()));
        dataHandler.saveRepositoryAnalysis(project, analysis);

        Assert.assertTrue("Changes file must exist on file system", new File ("./src/test/resources/out_dir/changes-data/1796-changes.csv").exists());
        Assert.assertTrue("Paths added file must exist on file system", new File ("./src/test/resources/out_dir/pathnames-data/1796-master-pathsAdded.txt").exists());
        Assert.assertTrue("Paths modified file must exist on file system", new File ("./src/test/resources/out_dir/pathnames-data/1796-master-pathsModified.txt").exists());
        Assert.assertTrue("Paths candidate file must exist on file system", new File ("./src/test/resources/out_dir/pathnames-data/1796-master-pathsCandidate.txt").exists());
        Assert.assertTrue("All paths file must exist on file system", new File ("./src/test/resources/out_dir/pathnames-data/1796-master-pathsAll.txt").exists());
    }
}
