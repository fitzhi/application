package com.fitzhi.source.crawler.git;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

/**
 * Management of dependencies test.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "dependenciesMarker=/jquery/;/bootstrap/" }) 
public class GitCrawlerNonRelevantDependenciesMgtTest {
	

	private static final String SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_D_JAVA = "src/test/java/com/sample/source/dependency/asset/D.java";
	private static final String SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_C_JAVA = "src/test/java/com/sample/source/dependency/asset/C.java";
	private static final String SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_B_JAVA = "src/test/java/com/sample/source/dependency/B.java";
	private static final String SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_A_JAVA = "src/test/java/com/sample/source/dependency/A.java";
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Autowired
	AsyncTask asyncTask;
	
	Project p;
	
	@Before
	public void before() throws SkillerException {
		p = new Project(1000, "test");
    	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 1000);
	}
	
	@Test
	public void testExtractPathDependencies() throws IOException {
		
		p.setLocationRepository(new File(".").getCanonicalPath());
		RepositoryAnalysis analysis = new RepositoryAnalysis(p);
		
		analysis.getPathsAdded().add("src/main/java/com/OneDependency/1");
		analysis.getPathsAdded().add("src/main/java/com/OneDependency/2");
		analysis.getPathsAdded().add("src/main/java/com/OneDependency");
		analysis.getPathsAdded().add("src/main/java/com/OneDependency/1/One");
		analysis.getPathsAdded().add("src/main/java/com/OneDependency/1/Two");
		analysis.getPathsAdded().add("src/main/java/com");

		analysis.getPathsAdded().add("src/main/java/fr/TwoDependency");
		analysis.getPathsAdded().add("src/main/java/fr/TwoDependency/1");
		analysis.getPathsAdded().add("src/main/java/fr/TwoDependency/2");
		analysis.getPathsAdded().add("src/main/java/fr/TwoDependency/3");
		
		analysis.getPathsAdded().add("src/main/nope/fr/ThreeDependency/a");
		analysis.getPathsAdded().add("src/main/nope/fr/ThreeDependency/Joombaya");
		analysis.getPathsAdded().add("src/main/nope/fr/ThreeDependency/test/hello"); 
		analysis.getPathsAdded().add("src/main/nope/fr/ThreeDependency/franckly");

		analysis.getPathsAdded().add("src/main/fred/fr/nope/1");
		analysis.getPathsAdded().add("src/main/fred/fr/nope");
		analysis.getPathsAdded().add("src/main/fred/fr/nope/2");
		
		List<String> dependenciesMarkers = new ArrayList<>();
		dependenciesMarkers.add("OneDependency");
		dependenciesMarkers.add("TwoDependency");
		dependenciesMarkers.add("ThreeDependency");
		
		scanner.selectPathDependencies(analysis, dependenciesMarkers);
		Assert.assertEquals(3, analysis.getPathsCandidate().size());
		Assert.assertTrue(
				analysis.getPathsCandidate()
					.stream()
					.anyMatch("src/main/java/com/OneDependency"::equals));
		Assert.assertTrue(
				analysis.getPathsCandidate()
					.stream()
					.anyMatch("src/main/java/fr/TwoDependency"::contains));
		Assert.assertTrue(
				analysis.getPathsCandidate()
					.stream()
					.anyMatch("src/main/nope/fr/ThreeDependency"::contains));
	}
	
	@Test
	public void testDependenciesMarker() {
		List<String> markers = scanner.dependenciesMarker();
		Assert.assertEquals(2, markers.size());
		Assert.assertEquals(1, markers.stream().filter("/jquery/"::equals).count());
		Assert.assertEquals(1, markers.stream().filter("/bootstrap/"::equals).count());
	}

	@Test
	public void testRetrieveRootPath1() throws IOException {
		p.setLocationRepository(new File(".").getCanonicalPath());
		RepositoryAnalysis analysis = new RepositoryAnalysis(p);

		analysis.takeChangeInAccount( "src/main/java/com/fitzhi/data/source/importance/FileSizeImportance.java",
				new SourceChange("1",  LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( "src/main/java/com/fitzhi/data/source/importance/AssessorImportance.java",
				new SourceChange("2", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( "src/main/java/com/fitzhi/data/source/importance/ImportanceCriteria.java",
				new SourceChange("3", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( "src/main/java/com/fitzhi/data/source/CommitHistory.java",
				new SourceChange("4",  LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( "src/main/java/com/fitzhi/data/source/CommitRepository.java",
				new SourceChange("5", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( "src/main/java/com/fitzhi/data/source/operation.java",
				new SourceChange("6",  LocalDate.now(), "fvi", "fvi@void.com") );
		
		analysis.getPathsAdded().add("src/main/java/com/fitzhi/data/source/importance/FileSizeImportance.java");
		analysis.getPathsAdded().add("src/main/java/com/fitzhi/data/source/importance/AssessorImportance.java");
		analysis.getPathsAdded().add("src/main/java/com/fitzhi/data/source/importance/ImportanceCriteria.java");
		analysis.getPathsAdded().add("src/main/java/com/fitzhi/data/source/CommitHistory.java");
		analysis.getPathsAdded().add("src/main/java/com/fitzhi/data/source/CommitRepository.java");
		analysis.getPathsAdded().add("src/main/java/com/fitzhi/data/source/operation.java");

		analysis.getPathsCandidate().add("src/main/java/com/fitzhi/data/source/importance");
		scanner.retrieveRootPath(analysis);
		Assert.assertEquals(1, p.getLibraries().size());
	}
	
	@Test
	public void testRetrieveRootPath2() throws IOException {
		p.setLocationRepository(new File(".").getCanonicalPath());
		RepositoryAnalysis analysis = new RepositoryAnalysis(p);
		
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_A_JAVA,
				new SourceChange("1", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_B_JAVA,
				new SourceChange("2",  LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_C_JAVA,
				new SourceChange("3",  LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_D_JAVA,
				new SourceChange("4",  LocalDate.now(), "fvi", "fvi@void.com") );
		
		analysis.getPathsAdded().add(SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_A_JAVA);
		
		analysis.getPathsAdded().add(SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_B_JAVA);

		analysis.getPathsCandidate().add("src/test/java/com/sample/source/dependency");
		scanner.retrieveRootPath(analysis);
		Assert.assertEquals(0, p.getLibraries().size());
		
	}
	
	@Test
	public void testRetrieveRootPath3() throws IOException {
		Project p = new Project(1000, "test");
		p.setLocationRepository(new File(".").getCanonicalPath());
		RepositoryAnalysis analysis = new RepositoryAnalysis(p);
		
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_A_JAVA,
				new SourceChange("1", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_B_JAVA,
				new SourceChange("2", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_C_JAVA,
				new SourceChange("3", LocalDate.now(), "fvi", "fvi@void.com") );
		analysis.takeChangeInAccount( SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_D_JAVA,
				new SourceChange("4", LocalDate.now(), "fvi", "fvi@void.com") );

		analysis.getPathsAdded().add(SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_A_JAVA);
		analysis.getPathsAdded().add(SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_B_JAVA);
		analysis.getPathsAdded().add(SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_C_JAVA);
		analysis.getPathsAdded().add(SRC_TEST_JAVA_COM_SAMPLE_SOURCE_DEPENDENCY_ASSET_D_JAVA);

		analysis.getPathsCandidate().add("src/test/java/com/sample/source/dependency");
		scanner.retrieveRootPath(analysis);
		Assert.assertEquals(1, p.getLibraries().size());
		
	}
	
	@After
	public void after() {
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 1000);
	}
	
}
