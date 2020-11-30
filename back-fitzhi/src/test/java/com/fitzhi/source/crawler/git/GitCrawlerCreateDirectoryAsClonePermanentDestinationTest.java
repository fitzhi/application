package com.fitzhi.source.crawler.git;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Testing the method
 * {@link GitCrawler#GitCrawlerCreateDirectoryAsCloneDestinationTest}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@TestPropertySource(properties = { "gitcrawler.repositories.location=../data/application/repos"}) 
public class GitCrawlerCreateDirectoryAsClonePermanentDestinationTest {
 
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
    /**
     * Testing using a temporary folder as a destination of the local repository.
     */
    @Test
    public void testTemporary() throws SkillerException {
        Project project = new Project(1789, "The french revolution");
        Path path = scanner.createDirectoryAsCloneDestination(project, new ConnectionSettings());
        if (log.isDebugEnabled()) {
            log.debug(path.toString());
            log.debug(path.toAbsolutePath().toString());
        }
        System.out.println(path.toAbsolutePath().toString());
        Assert.assertTrue("createDirectoryAsCloneDestination returns a directory", Files.isDirectory(path));
    }
}
