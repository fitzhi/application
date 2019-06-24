/**
 * 
 */
package fr.skiller.source.crawler.git;

import static fr.skiller.Global.UNKNOWN;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevCommitList;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.DataChartHandler;
import fr.skiller.bean.DataSaver;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerTest {

	private static final String DIR_GIT = "../git_repo_for_test/%s/";

	private static final String FILE_GIT = DIR_GIT + ".git";
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	DataSaver dataSaver;
	
	@Autowired
	DataChartHandler dataChartHandler;
	
	private Repository repository;

	@Test
	public void loadChangesForFirstTest() throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "first-test"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);

		assertTrue(gitChanges.stream().map(SCMChange::getPath).noneMatch("moduleA/test.txt"::equals));
		assertTrue(gitChanges.stream().map(SCMChange::getPath).anyMatch("moduleB/test.txt"::equals));

		assertTrue(gitChanges.stream().map(SCMChange::getPath).noneMatch("moduleA/creationInA.txt"::equals));
		// This file has been updated and renamed, and therefore not detected as a
		// rename by the JGIT RenameDetector
		assertTrue(gitChanges.stream().map(SCMChange::getPath).anyMatch("moduleAchanged/creationInA.txt"::equals));

	}

	@Test
	public void finalizeListChangesForFirstTest() throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "first-test"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, "first-test"), gitChanges);

		assertTrue(gitChanges.stream().map(SCMChange::getPath).noneMatch("moduleA/creationInA.txt"::equals));
		// This file has been updated and renamed, and therefore not detected as a
		// rename by the JGIT RenameDetector
		assertTrue(gitChanges.stream().map(SCMChange::getPath).anyMatch("moduleAchanged/creationInA.txt"::equals));
	}

	/**
	 * Test the method filterElibilible
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "wibkac"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, "wibkac"), gitChanges);
		assertTrue (gitChanges.stream()
			.map(SCMChange::getPath)
			.anyMatch("front-skiller/src/assets/img/pdf.png"::equals));

		
		scanner.filterEligible(gitChanges);

		assertTrue (gitChanges.stream()
				.map(SCMChange::getPath)
				.noneMatch("front-skiller/src/assets/img/pdf.png"::equals));		

	}
	
	
	/**
	 * Test the method filterElibilible
	 * @throws IOException
	 */
	@Test
	public void testCleanupPaths() throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "wibkac"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, "wibkac"), gitChanges);
		scanner.filterEligible(gitChanges);
		scanner.cleanupPaths(gitChanges);
		gitChanges.stream().map(SCMChange::getPath).forEach(System.out::println);

	}
	
	/**
	 * Test the method dataHandler.saveChanges
	 * @throws IOException
	 */
	@Test
	public void testSaveChanges() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "wibkac"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		
		dataSaver.saveChanges(new Project (777, "test"), gitChanges);
	}
	
	public void testDebug() throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "first-test"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, "first-test"), gitChanges);
		gitChanges.stream().map(SCMChange::getPath).forEach(System.out::println);
	}
	
	public void testVIP() throws IOException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "VIP-MIDDLEWARE"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);

		scanner.finalizeListChanges(String.format(DIR_GIT, "VIP-MIDDLEWARE"), gitChanges);

		
		/**
		 * We filter the collection on eligible entries (.java; .js...)
		 */
		scanner.filterEligible(gitChanges);
		
		/**
		 * We cleanup the pathnames each location (e.g. "src/main/java" is removed) 
		 */
		scanner.cleanupPaths(gitChanges);
		
		CommitRepository repositoryOfCommit = new BasicCommitRepository();
		
        /**
         * Set of unknown contributors having work on this repository.
         */
        Set<String >unknown = repositoryOfCommit.unknownContributors();

        Project p = new Project(7, "VIP");
        /**
		 * We update the staff identifier on each change entry.
		 */
		scanner.updateStaff(p, gitChanges, unknown);

		gitChanges.stream().forEach(
				change -> 
				repositoryOfCommit.addCommit(
						change.getPath(), 
						change.isIdentified() ? change.getIdStaff() : UNKNOWN,
						change.getDateCommit())
				);
		
		gitChanges.stream().map(SCMChange::getPath).forEach(System.out::println);

		RiskDashboard data = scanner.aggregateDashboard(p, repositoryOfCommit);

		dataChartHandler.aggregateDataChart(data.riskChartData);
		
		StringBuilder sb = new StringBuilder();
		data.riskChartData.dump(sb,"");
		System.out.println(sb);
	
	}

	/*
	 * public void test() throws IOException {
	 * 
	 * // a RevWalk allows to walk over commits based on some filtering that is
	 * defined RevWalk walk = new RevWalk(repository); ObjectId headId =
	 * repository.resolve(Constants.HEAD); RevCommit start =
	 * walk.parseCommit(headId); walk.markStart(start);
	 * 
	 * RevCommitList<RevCommit> list = new RevCommitList<>(); list.source(walk);
	 * list.fillTo(Integer.MAX_VALUE);
	 * 
	 * TreeWalk treeWalk = new TreeWalk(repository); for (RevCommit commit : list) {
	 * 
	 * RenameDetector rd = new RenameDetector(repository);
	 * 
	 * 
	 * System.out.print("-> " + commit.getId() + " " + commit.getFullMessage());
	 * 
	 * 
	 * treeWalk.reset(); RevTree revTree = commit.getTree();
	 * treeWalk.addTree(commit.getTree());
	 * 
	 * 
	 * treeWalk.setRecursive(true);
	 * 
	 * for (RevCommit parent : commit.getParents()) { System.out.print("---------> "
	 * + parent.getId() + " " + parent.getFullMessage()); RevTree rt =
	 * parent.getTree(); System.out.println("---------> " + rt.getId());
	 * treeWalk.addTree(rt); }
	 * 
	 * while (treeWalk.next()) { if (treeWalk.getTreeCount() == 1) {
	 * System.out.println(treeWalk.getOperationType() + " " +
	 * treeWalk.getPathString()); } if (treeWalk.getTreeCount() == 2) {
	 * rd.addAll(DiffEntry.scan(treeWalk)); List<DiffEntry> files = rd.compute();
	 * for (DiffEntry de : files) { System.out.println(de.getChangeType() + " " +
	 * de.getOldPath() + " " + de.getNewPath()); } }
	 * System.out.println(treeWalk.getPathString() + " " + treeWalk.getFileMode() +
	 * " " + treeWalk.getDepth() + " " + treeWalk.getPathString()); } }
	 * treeWalk.close(); walk.close(); }
	 */
	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}