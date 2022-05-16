/**
 * 
 */
package com.fitzhi.source.crawler.git;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;

import org.eclipse.jgit.lib.Repository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * 
 * Testing the implementation of {@link RepoScanner#parseRepository(Project) } in {@kink GitCrawler}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerParseRepositoryTest {

	@MockBean
	GitCrawler scanner;

	@MockBean
	AsyncTask asyncTask;

	@MockBean
	StaffHandler staffHandler;

	@MockBean
	CacheDataHandler cacheDataHandler;

	@Test (expected = ApplicationException.class)
	public void noGitrepositoryAvailable() throws ApplicationException, IOException {
		Project project = new Project(1933, "Bad year");
		
		// There is no location repository.
		project.setLocationRepository(null);

		when(scanner.parseRepository(Mockito.any())).thenCallRealMethod();
		scanner.parseRepository(project);
	}

	@Test 
	public void repositoryAlreadyAvailable() throws ApplicationException, IOException {
		Project project = new Project(1933, "Bad year");
		
		when(scanner.parseRepository(any(Project.class))).thenCallRealMethod();
		when(scanner.loadRepositoryFromCacheIfAny(any(Project.class))).thenReturn(new BasicCommitRepository());
		// The upgradeRepository method returns FALSE
		when(scanner.upgradeRepository(any(Project.class), any(CommitRepository.class))).thenReturn(false);
		
		Assert.assertNotNull(scanner.parseRepository(project));

		verify(scanner, never()).retrieveRepositoryAnalysis( any(Project.class), any(Repository.class) );
		verify(scanner, times(1)).upgradeRepository( any(Project.class), any(CommitRepository.class) );
		verify(cacheDataHandler, never()).saveRepository( any(Project.class), any(CommitRepository.class) );
	}

	@Test 
	public void repositoryAlreadyAvailableAndSavedAfterUpgrade() throws ApplicationException, IOException {
		
		Project project = new Project(1933, "Bad year");
		
		when(scanner.parseRepository(any(Project.class))).thenCallRealMethod();
		when(scanner.loadRepositoryFromCacheIfAny(any(Project.class))).thenReturn(new BasicCommitRepository());
		// The upgradeRepository method returns TRUE
		when(scanner.upgradeRepository(any(Project.class), any(CommitRepository.class))).thenReturn(true);
		doNothing().when(scanner).saveRepository(any(Project.class), any(CommitRepository.class)  );

		Assert.assertNotNull(scanner.parseRepository(project));

		verify(scanner, never()).retrieveRepositoryAnalysis( any(Project.class), any(Repository.class) );
		verify(scanner, times(1)).upgradeRepository( any(Project.class), any(CommitRepository.class) );
		verify(scanner, times(1)).saveRepository( any(Project.class), any(CommitRepository.class) );
	}


	@Test 
	public void repositoryCreation() throws ApplicationException, IOException {
		Project project = new Project(1933, "Bad year");
		project.setLocationRepository("..");
		when(scanner.parseRepository(any(Project.class))).thenCallRealMethod();
		when(scanner.loadRepositoryFromCacheIfAny(any(Project.class))).thenReturn(null);
		when(scanner.retrieveRepositoryAnalysis(any(Project.class), any(Repository.class))).thenReturn(new RepositoryAnalysis(project));
		when(scanner.buildCommitRepository(any(Project.class), any(RepositoryAnalysis.class), any())).thenReturn(new BasicCommitRepository());
		doNothing().when(scanner).updateProjectEcosystem(any(Project.class), any(RepositoryAnalysis.class));
		doNothing().when(scanner).generateAndSaveSkyline(any(Project.class), any(RepositoryAnalysis.class));
		scanner.tasks = asyncTask;
		scanner.staffHandler = staffHandler;
		scanner.cacheDataHandler = cacheDataHandler;
		Assert.assertNotNull(scanner.parseRepository(project));
		verify(scanner, times(1)).retrieveRepositoryAnalysis( any(Project.class), any(Repository.class) );
	}

}