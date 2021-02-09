/**
 * 
 */
package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.io.File;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
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
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "prefilterEligibility=true" }) 
public class RepoTestNumberOfLinesGitCrawlerTest {

	private static final String TESTING_REPOSITORY = "repo-test-number-of-lines/";

	private static final String DIR_GIT = "../git_repo_for_test/%s";

	private int NUMBER_OF_STEPS = 10;

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	AsyncTask asyncTask;

	@Autowired
	SkylineProcessor skylineProcessor;

	private Repository repository;

	private Project project;
	
	@Before
	public void before() throws Exception {
		project = new Project(1571 , "Lepante");
		asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1571);
		project.setLocationRepository(new File(String.format(DIR_GIT, TESTING_REPOSITORY)).getAbsolutePath());
	}

	@Test
	public void testAddingAnExternalLibraryOf7LinesEvictedFromAudit() throws Exception {

		project.getLibraries().add(new Library("two/lib", 1));

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// 7 lines of the library should not be taken in account id the libray directory is declared as an external library 
		Assert.assertEquals(
			"7 lines of the library should not be taken in account if the libray directory is declared as an external library",
			18, 
			pl.getLayers().get(0).getLines());
	}

	@Test
	public void testAddingAnExternalLibraryOf7Lines() throws Exception {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// 7 lines added with the undeclared library
		Assert.assertEquals(
			"7 lines added in an undeclared library",
			25, 
			pl.getLayers().get(0).getLines());
	}

	@Test
	public void testAdding2LinesOnFileTwo() throws Exception {

		resetToStep(NUMBER_OF_STEPS-9);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());

		// 2 lines have been added into FileTwo
		Assert.assertEquals(18, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileAnotherFileTwoOnMain() throws Exception {

		resetToStep(NUMBER_OF_STEPS-8);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// The file AnotherFileTwo has been created with 4 lines 
		Assert.assertEquals(16, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileThreeIsRemoved() throws Exception {

		resetToStep(NUMBER_OF_STEPS-7);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// The file Tree is removed. Just 
		Assert.assertEquals(11, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileThreeIsCreatedAndFileOneIsModifiedMergedFromBranch() throws Exception {

		resetToStep(NUMBER_OF_STEPS-6);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// 1 file is added (8 lines), 1 file is modified (Adding 3 lines)
		Assert.assertEquals(19, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileOneIsMoveIntoFileTwoMergedFromBranch() throws Exception {

		resetToStep(NUMBER_OF_STEPS-5);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// 1 file is deleted. Only  a 6 lines file remain.
		Assert.assertEquals(8, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileTwoIsRemoved() throws Exception {

		resetToStep(NUMBER_OF_STEPS-4);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		
		// 1 file is deleted. Only 6 lines remain.
		Assert.assertEquals(8, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileOneIsMovedIntoPackageOne() throws Exception {

		resetToStep(NUMBER_OF_STEPS-3);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		// 2 files of 6 lines : FileOne (8 lines with the package), FileTwo (6 lines)
		Assert.assertEquals(14, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testFileTwoIsAdded() throws Exception {

		resetToStep(NUMBER_OF_STEPS-2);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		// 2 files of 6 lines : FileOne, FileTwo
		Assert.assertEquals(12, pl.getLayers().get(0).getLines());
	}
	
	@Test
	public void testFileOneIsAdded() throws Exception {

		resetToStep(NUMBER_OF_STEPS-1);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
		Assert.assertEquals(6, pl.getLayers().get(0).getLines());
	}

	@Test
	public void testEmpty() throws Exception {
		
		resetToStep(NUMBER_OF_STEPS);

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git"))).readEnvironment().findGitDir()
				.build();

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		scanner.fillRepositoryAnalysis(project, analysis, repository);
		scanner.finalizeListChanges(String.format(DIR_GIT, TESTING_REPOSITORY), analysis);

		Assert.assertTrue("Only the read me file is present", analysis.getChanges().keySet().contains("README.md"));

		ProjectLayers pl = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
		Assert.assertEquals(1, pl.getLayers().size());
	}

	private void resetToStep(int step) throws Exception {
		Git git = Git.open(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git")));
		git.reset().setMode(org.eclipse.jgit.api.ResetCommand.ResetType.HARD).setRef("HEAD~"+step).call();
	}

	@After
	public void after() throws Exception {
		if (repository != null) {
			repository.close();
		}
		asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1571);

		Git git = Git.open(new File(String.format(DIR_GIT, TESTING_REPOSITORY+ "/.git")));
		git.pull().call();
		
	}
}