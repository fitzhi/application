package com.fitzhi.source.crawler.git;

import java.nio.file.Paths;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * Testing the method {@link GitCrawler#clone(com.fitzhi.data.internal.Project, com.fitzhi.data.source.ConnectionSettings) Git.clone()}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "gitcrawler.repositories.location=../repos_test" }) 
@Slf4j
public class GitCrawlerCloneTest {

    Project project;

    @Autowired
	@Qualifier("GIT")
    RepoScanner repoScanner;

    @Autowired
    ProjectHandler projectHandler;

    @Before
    public void before() throws ApplicationException {
        project = new Project(1515, "Marignan");
        projectHandler.addNewProject(project);
    }

    /**
     * The application stores the local repositories into {@code gitcrawler.repositories.location}
     * @throws Exception
     */
    @Test
    public void testCloneSimpleProject() throws Exception {
        ConnectionSettings settings = new ConnectionSettings();
        settings.setPublicRepository(true);
        settings.setUrl("https://github.com/frvidal/first-test.git");
        repoScanner.clone(project, settings);
        log.debug(project.getLocationRepository());
        // The removal of the directory has to be successful.
        GitCrawler.removeCloneDir(Paths.get("../repos_test/1515"));
    }

    @After
    public void after() throws ApplicationException {
        projectHandler.getProjects().remove(1515);
    }
}
