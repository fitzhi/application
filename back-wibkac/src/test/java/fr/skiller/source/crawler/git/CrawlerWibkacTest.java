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
import org.eclipse.jgit.lib.Ref;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.DataChartHandler;
import fr.skiller.bean.DataSaver;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RepositoryAnalysis;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerWibkacTest {

	private static final String WIBKAC = "wibkac";

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

	private Project project;
	
	@Before
	public void before() {
		project = new Project(1000, WIBKAC);
	}
	
	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilterEligible() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, WIBKAC), analysis);
		assertTrue(
				analysis.getChanges().stream().map(SCMChange::getPath).anyMatch("front-skiller/src/assets/img/pdf.png"::equals));

		scanner.filterEligible(analysis);

		assertTrue(
				analysis.getChanges().stream().map(SCMChange::getPath).noneMatch("front-skiller/src/assets/img/pdf.png"::equals));

	}

	/**
	 * Test the method filterElibilible
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCleanupPaths() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, WIBKAC), analysis);
		scanner.filterEligible(analysis);
		scanner.cleanupPaths(analysis);
		analysis.getChanges().stream().map(SCMChange::getPath).forEach(System.out::println);

	}

	/**
	 * Test the method dataHandler.saveChanges
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSaveChanges() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);

		dataSaver.saveChanges(new Project(777, "test"), analysis.getChanges());
	}

	/**
	 * Test the method dataHandler.saveSCMPath
	 * 
	 * @throws IOException
	 */
	@Test
	public void testsaveSCMPath() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(FILE_GIT, WIBKAC))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);

		Project p = new Project(777, "test");
		p.setLocationRepository(new File(String.format(FILE_GIT, WIBKAC)).getAbsolutePath());
		dataSaver.saveRepositoryDirectories(p, analysis.getChanges());
	}

	@Test
	public void testParseRepository() throws IOException, SkillerException {
		Project prj = new Project (777, "vegeo");
		prj.setLocationRepository(String.format(DIR_GIT, WIBKAC));
		scanner.parseRepository(prj, new ConnectionSettings());
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}