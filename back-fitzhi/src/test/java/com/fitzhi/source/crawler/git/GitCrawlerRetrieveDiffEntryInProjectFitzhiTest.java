/**
 * 
 */
package com.fitzhi.source.crawler.git;

import java.io.File;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
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
 * Testing the method {@link GitCrawler#retrieveDiffEntry(String, Repository, RevCommit, RevCommit) retrieveDiffEntry}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "patternsInclusion=.*." , "prefilterEligibility=true" }) 
public class GitCrawlerRetrieveDiffEntryInProjectFitzhiTest {

    private static final String FIRST_TEST = "first-test/";

    private static final String DIR_GIT = "../git_repo_for_test/%s";

    @Autowired
    @Qualifier("GIT")
    RepoScanner scanner;

    @Autowired
    AsyncTask asyncTask;

    private Repository repository;

    @Before
    public void before() throws Exception {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment().findGitDir()
                .build();
    }
    
    @Test
    public void testDiffEntryAdd() throws Exception {
        
        try (Git git = new Git(repository)) {

            RevCommit cmt1 = retrieveRevCommit("c05bbbd5c2ec19d354b2bcd7ab1f737b31624d6a");
            RevCommit cmt2 = retrieveRevCommit("66bd3dadc9ddc42472229dc870c65a40977c1a4e");
            DiffEntry de = scanner.retrieveDiffEntry("moduleA/test.txt", repository, cmt2, cmt1);
            Assert.assertEquals(ChangeType.ADD, de.getChangeType());
            Assert.assertEquals("/dev/null", de.getOldPath());
            Assert.assertEquals("moduleA/test.txt", de.getNewPath());
        }
    }

    private RevCommit retrieveRevCommit (String sha1) throws Exception {
        ObjectId commitId = ObjectId.fromString(sha1);
        try (RevWalk revWalk = new RevWalk(repository)) {
              RevCommit commit = revWalk.parseCommit(commitId);
              return commit;
        }
    }

    @After
    public void after() {
        if (repository != null) {
            repository.close();
        }
    }
}
