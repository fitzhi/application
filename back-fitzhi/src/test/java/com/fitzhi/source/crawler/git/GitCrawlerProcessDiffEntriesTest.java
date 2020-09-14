/**
 * 
 */
package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fitzhi.Global;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;
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

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=false" }) 
@Slf4j
public class GitCrawlerProcessDiffEntriesTest {

	private static final String TESTING_APPLICATION = "first-test";

	private static final String DIR_GIT = ".." + File.separator + "git_repo_for_test" + File.separator + "%s" + File.separator;

	private static final String FILE_GIT = DIR_GIT + ".git";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	DataHandler dataSaver;

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	DataChartHandler dataChartHandler;

	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;

	@Autowired
	AsyncTask asyncTask;

	private Repository repository;

	private Project project;
    
    private Git git;

    private RevWalk rw;

    private DiffFormatter df;

	@Before
	public void before() throws Exception {
		project = new Project(1000, TESTING_APPLICATION);
        asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1000);
        
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Repository location %s", new File(String.format(FILE_GIT, TESTING_APPLICATION)).getAbsolutePath()));
		}
		
		repository = builder.setGitDir(new File(String.format(FILE_GIT, TESTING_APPLICATION))).readEnvironment().findGitDir()
				.build();

        git = new Git(repository);

        rw = new RevWalk(repository);
        df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repository);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);

	}
    private int linesAdded = 0;
    private int linesDeleted = 0;

    	/**
	 * This test is simply display the diff resume for the while repository.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testProcessDiffEntries() throws IOException, GitAPIException {

		ParserVelocity velocity = new ParserVelocity(project.getId(), this.asyncTask);

        RevCommit commit = rw.parseCommit(repository.resolve("c05bbbd5c2ec19d354b2bcd7ab1f737b31624d6a")); 
        RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
        System.out.println(parent.getId());
        List<DiffEntry>  diffs = df.scan(parent.getTree(), commit.getTree());

        final RepositoryAnalysis analysis = new RepositoryAnalysis(project);
        Assert.assertTrue("no file recorded", analysis.getPathsAll().isEmpty());
        scanner.processDiffEntries(analysis, commit, diffs, df, velocity);
        analysis.getPathsAll().stream().forEach(s -> System.out.println(s));
        Assert.assertEquals("no file recorded", 3, analysis.getPathsAll().size());
    }

	/**
	 * This test is simply display the diff resume for the while repository.
	 * 
	 * @throws IOException
	 */
    @Test
	public void testDisplayDiffAll() throws IOException, GitAPIException {

        final String[] commitIds = {
            "adf0045228ee185cada76333f2a4b98ca7b26886",
            "4eb66b28bbffe527052c3fde2df60db0baee3dc8",
            "ed0e618127f4669f66106e33c8b1965a0a0b56e7",
            "e8c9dbac4d5886f02f90d719fb2e13164a3f011e",
            "e89ca4d4a579efb4538377072ed1f106393ee8fb",
            "126900f5fe1022575b9b8c70131bc873c45e44b3",
            "2d2c04085fa8988b047c490c185786d40119c6df",
            "fe4db9abf5d0aa34556d5725bf92a16b12e54999",
            "c05bbbd5c2ec19d354b2bcd7ab1f737b31624d6a",
 //           "66bd3dadc9ddc42472229dc870c65a40977c1a4e"
        };

        final StringBuilder sbLog = new StringBuilder();
        Arrays.stream(commitIds).forEach(commitId -> {
            try {
                test(rw, df, commitId, sbLog);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (log.isDebugEnabled()) {
                log.debug(sbLog.toString());
            }
            
        });
	}

    public void test(RevWalk rw, DiffFormatter df,  String commitId, StringBuilder sbLog) throws IOException, IncorrectObjectTypeException {
        RevCommit commit = rw.parseCommit(repository.resolve(commitId)); 
        RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
        sbLog.append(Global.LN);
        sbLog.append(commit.getShortMessage());
        sbLog.append(Global.LN);
        List<DiffEntry>  diffs = df.scan(parent.getTree(), commit.getTree());
        countLines(df, diffs, sbLog);
    }

    public void countLines(DiffFormatter diffFormater, List<DiffEntry> diffs, StringBuilder sbLog) {
        diffs.stream().forEach(diff -> {
            sbLog.append(diff.getOldPath()).append(" ");
            linesDeleted = 0;
            linesAdded = 0;
            try {
                for (Edit edit : diffFormater.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                    linesAdded += edit.getEndB() - edit.getBeginB();
                }            
                sbLog.append(diff.getNewPath()).append(" ").append(linesDeleted).append(" ").append(linesAdded).append(Global.LN);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

    }

	@After
	public void after() {
        asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1000);
		if (repository != null) {
			repository.close();
        }
        df.close();
        git.close();
 	}
}