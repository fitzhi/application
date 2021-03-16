package com.fitzhi.source.crawler.git;

import com.fitzhi.data.internal.Project;
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
