/**
 * 
 */
package fr.skiller.source.crawler.git;

import static fr.skiller.Error.CODE_PARSING_SOURCE_CODE;
import static fr.skiller.Error.MESSAGE_PARSING_SOURCE_CODE;
import static fr.skiller.Global.UNKNOWN;
import static org.eclipse.jgit.diff.DiffEntry.DEV_NULL;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.activation.DataHandler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevCommitList;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
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
	public void loadChangesForFirstTest() throws IOException, SkillerException {

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

		// At this level if a java class move from one package to one another, system
		// does not detect it
		assertTrue(
				gitChanges.stream().map(SCMChange::getPath).noneMatch("com/application/packageA/MyClass.java"::equals));
		assertTrue(
				gitChanges.stream().map(SCMChange::getPath).anyMatch("com/application/packageB/MyClass.java"::equals));

	}

	@Test
	public void finalizeListChangesForFirstTest() throws IOException, SkillerException {

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
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "wibkac"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, "wibkac"), gitChanges);
		assertTrue(
				gitChanges.stream().map(SCMChange::getPath).anyMatch("front-skiller/src/assets/img/pdf.png"::equals));

		scanner.filterEligible(gitChanges);

		assertTrue(
				gitChanges.stream().map(SCMChange::getPath).noneMatch("front-skiller/src/assets/img/pdf.png"::equals));

	}

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCleanupPaths() throws IOException, SkillerException {

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
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSaveChanges() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "wibkac"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);

		dataSaver.saveChanges(new Project(777, "test"), gitChanges);
	}

	public void testDebug() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "first-test"))).readEnvironment().findGitDir()
				.build();

		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, "first-test"), gitChanges);
		gitChanges.stream().map(SCMChange::getPath).forEach(System.out::println);
	}

	public void testVEGEO() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, "vegeo"))).readEnvironment().findGitDir()
				.build();

		List<RevCommit> allCommits = new ArrayList<>();
		try (Git git = new Git(repository)) {

			String treeName = "refs/heads/master"; // tag or branch
			for (RevCommit commit : git.log().add(repository.resolve(treeName)).call()) {
				allCommits.add(commit);
			}
		} catch (final IOException | GitAPIException e) {
			throw new SkillerException(CODE_PARSING_SOURCE_CODE, MESSAGE_PARSING_SOURCE_CODE, e);
		}

		
		allCommits.stream().filter(revc -> revc.getCommitterIdent().getName().contains("Alonso"))
				.map(revc -> revc.getShortMessage() + " " + revc.getCommitterIdent().getName())
			.distinct()
				.forEach(System.out::println);
		
//	      scanner.loadChanges(repository);

	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}