package com.fitzhi.source.crawler.git;

import java.io.File;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * Testing the method {@link GitCrawler#generateAsync(com.fitzhi.data.internal.Project, com.fitzhi.controller.in.SettingsGeneration)}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerGenerateAsyncTest {

    @Autowired
    @Qualifier("GIT")
    RepoScanner scanner;

    @Test
    public void test() throws ApplicationException {

        Project project = new Project(1789, "The French revolution");
        project.setLocationRepository("undefined");
        scanner.generateAsync(project, null);
        
    }

}
