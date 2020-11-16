package com.fitzhi.source.crawler.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GitCrawlTest {

    private static final String TESTING_REPOSITORY = "repo-test-number-of-lines/";

    private static final String DIR_GIT = "../git_repo_for_test/%s";

    @Test
    public void test() throws Exception {

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY + "/.git")))
                .readEnvironment().findGitDir().build();

        RevCommit first = first(repository);
        if (log.isDebugEnabled()) {
            log.debug(first.getShortMessage());
        }

        
    }

    private RevCommit first(Repository repository) throws Exception {

        RevWalk rw = new RevWalk(repository);
        RevCommit commit = null;
        AnyObjectId headId;
        try {
            headId = repository.resolve(Constants.HEAD);
            RevCommit root = rw.parseCommit(headId);
            rw.sort(RevSort.REVERSE);
            rw.markStart(root);
            commit = rw.next();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            rw.close();
        }
        return commit;

        /*
         * 
         * RevCommit commit; ObjectId commitId = repository.resolve("HEAD"); try(RevWalk
         * revWalk = new RevWalk(repository)) { commit = revWalk.parseCommit(commitId);
         * commitId = commit.toObjectId(); } return commit;
         * 
         */
    }
}
