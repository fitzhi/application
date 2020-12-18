/**
 * 
 */
package com.fitzhi.data.source;

import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.PROJECT;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.SourceChange;

/**
 * Test the creation of contributors based on stats retrieve in the source repository.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContributorsCommitRepositoryTest {
	
	private static final String GLUCAS = "glucas";

	private static final String FVIDAL = "frvidal";

	private static final String TEST = "test";

	private static final String TEST3 = "test3";

	private static final String TEST2 = "test2";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	List<Contributor> contributors;

	@Autowired
	StaffHandler staffHandler;
	
	@Autowired
	AsyncTask asyncTask;
	
    @Before
    public void before() throws ApplicationException {
    	
    	Project p = new Project(100, "Test");
    	asyncTask.addTask(DASHBOARD_GENERATION, PROJECT, 100);
    	
    	RepositoryAnalysis analysis = new RepositoryAnalysis(p);
    	SourceControlChanges repo = analysis.getChanges();
    			
		repo.addChange(TEST, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 1), FVIDAL, ""));
		repo.addChange(TEST, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 17), FVIDAL, ""));
		repo.addChange(TEST, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 23), GLUCAS, ""));
		repo.addChange(TEST, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 14), "tintin", ""));

		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 2), FVIDAL, ""));
		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 2), GLUCAS, ""));
		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 23), GLUCAS, ""));
		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 14), GLUCAS, ""));
		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 1), GLUCAS, ""));
		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 10), GLUCAS, ""));

		repo.addChange(TEST2, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 2), "tintin", ""));
		
		repo.addChange(TEST3, 
				new SourceChange(String.valueOf(System.nanoTime()), LocalDate.of(2018, 11, 2), "haddock", ""));
		
		Set<String> unknownContributors = new HashSet<>();
		scanner.updateStaff (p, analysis, unknownContributors);
    	contributors = analysis.gatherContributors();
    }
    
    @Test
	public void numberOfCommits() throws ApplicationException {
    	
    	Contributor frvidal = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 1)
    			.findFirst()
    			.orElseThrow(ApplicationException::new); 	
		Assert.assertEquals(3, frvidal.getNumberOfCommitsSubmitted());

	 	Contributor glucas = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 2)
    			.findFirst()
    			.orElseThrow(ApplicationException::new);   
		Assert.assertEquals(6, glucas.getNumberOfCommitsSubmitted());
		
	 	Contributor tintin = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 3)
    			.findFirst()
    			.orElseThrow(ApplicationException::new);   
		Assert.assertEquals(2, tintin.getNumberOfCommitsSubmitted());
	}

    @Test
	public void lastCommit() throws ApplicationException {
    	Contributor frvidal = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 1)
    			.findFirst()
    			.orElseThrow(ApplicationException::new); 	
		Assert.assertEquals(LocalDate.of(2018, 11, 17), frvidal.getLastCommit());
		
	 	Contributor glucas = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 2)
    			.findFirst()
    			.orElseThrow(ApplicationException::new);   
		Assert.assertEquals(LocalDate.of(2018, 11, 23), glucas.getLastCommit());
		
	 	Contributor tintin = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 3)
    			.findFirst()
    			.orElseThrow(ApplicationException::new);   
		Assert.assertEquals(LocalDate.of(2018, 11, 14), tintin.getLastCommit());
	}
    
    @Test
	public void numberOfFiles() throws ApplicationException {
    	Contributor frvidal = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 1)
    			.findFirst()
    			.orElseThrow(ApplicationException::new); 	
		Assert.assertEquals(2, frvidal.getNumberOfFiles());
		
    	Contributor haddock = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 4)
    			.findFirst()
    			.orElseThrow(ApplicationException::new); 	
		Assert.assertEquals(1, haddock.getNumberOfFiles());
	}
    
    @After
    public void after() {
    	asyncTask.removeTask(DASHBOARD_GENERATION, PROJECT, 100);
    }
}
