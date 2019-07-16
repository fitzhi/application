/**
 * 
 */
package fr.skiller.source.crawler.git;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
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
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "patternsInclusion=.*." }) 
public class CrawlerFirstTest {

	private static final String FIRST_TEST = "first-test/";

	private static final String DIR_GIT = "../git_repo_for_test/%s";

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
		project = new Project(1000, FIRST_TEST);
	}
	
	@Test
	public void loadChangesForFirstTest() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST+"/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		List<SCMChange> gitChanges = analysis.getChanges();

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
		
		assertTrue (new File(String.format(DIR_GIT, FIRST_TEST)).getAbsolutePath(), new File(String.format(DIR_GIT, FIRST_TEST)).exists());
		
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST + "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = scanner.loadChanges(project, repository);
		
		scanner.finalizeListChanges(String.format(DIR_GIT, FIRST_TEST), analysis);
		
		assertTrue(analysis.getChanges().stream().map(SCMChange::getPath).noneMatch("moduleA/creationInA.txt"::equals));
		// This file has been updated and renamed, and therefore not detected as a
		// rename by the JGIT RenameDetector
		assertTrue(analysis.getChanges().stream().map(SCMChange::getPath).anyMatch("moduleAchanged/creationInA.txt"::equals));
	}


	public void testDebug() throws IOException, SkillerException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, FIRST_TEST))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis =  scanner.loadChanges(project, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, FIRST_TEST), analysis);
		analysis.getChanges().stream().map(SCMChange::getPath).forEach(System.out::println);
	}

	@Test
	public void testParseRepository() throws IOException, SkillerException, GitAPIException {
		Project prj = new Project (777, "First test");
		prj.setLocationRepository(String.format(DIR_GIT, FIRST_TEST));
		scanner.parseRepository(prj, new ConnectionSettings());
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}