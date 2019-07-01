/**
 * 
 */
package fr.skiller.data.source;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RepositoryAnalysis;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;
import fr.skiller.source.crawler.git.SCMChange;

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
	
	
    @Before
    public void before() {
    	
    	Project p = new Project(100, "Test");
    	
    	RepositoryAnalysis analysis = new RepositoryAnalysis();
    	List<SCMChange> repo = analysis.getChanges();
    	
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST, LocalDate.of(2018, 11, 1), FVIDAL, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST, LocalDate.of(2018, 11, 17), FVIDAL, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST, LocalDate.of(2018, 11, 23), GLUCAS, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST, LocalDate.of(2018, 11, 14), "tintin", ""));


		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 2), FVIDAL, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 2), GLUCAS, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 23), GLUCAS, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 14), GLUCAS, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 1), GLUCAS, ""));
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 10), GLUCAS, ""));

		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST2, LocalDate.of(2018, 11, 2), "tintin", ""));
		
		repo.add(new SCMChange(String.valueOf(System.nanoTime()), TEST3, LocalDate.of(2018, 11, 2), "haddock", ""));
		
		Set<String> unknownContributors = new HashSet<>();
		scanner.updateStaff (p, analysis, unknownContributors);
    	contributors = scanner.gatherContributors(analysis);
    }
    
    @Test
	public void numberOfCommits() throws SkillerException {
    	
    	Contributor frvidal = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 1)
    			.findFirst()
    			.orElseThrow(SkillerException::new); 	
		Assert.assertEquals(3, frvidal.getNumberOfCommitsSubmitted());

	 	Contributor glucas = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 2)
    			.findFirst()
    			.orElseThrow(SkillerException::new);   
		Assert.assertEquals(6, glucas.getNumberOfCommitsSubmitted());
		
	 	Contributor tintin = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 3)
    			.findFirst()
    			.orElseThrow(SkillerException::new);   
		Assert.assertEquals(2, tintin.getNumberOfCommitsSubmitted());
	}

    @Test
	public void lastCommit() throws SkillerException {
    	Contributor frvidal = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 1)
    			.findFirst()
    			.orElseThrow(SkillerException::new); 	
		Assert.assertEquals(LocalDate.of(2018, 11, 17), frvidal.getLastCommit());
		
	 	Contributor glucas = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 2)
    			.findFirst()
    			.orElseThrow(SkillerException::new);   
		Assert.assertEquals(LocalDate.of(2018, 11, 23), glucas.getLastCommit());
		
	 	Contributor tintin = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 3)
    			.findFirst()
    			.orElseThrow(SkillerException::new);   
		Assert.assertEquals(LocalDate.of(2018, 11, 14), tintin.getLastCommit());
	}
    
    @Test
	public void numberOfFiles() throws SkillerException {
    	Contributor frvidal = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 1)
    			.findFirst()
    			.orElseThrow(SkillerException::new); 	
		Assert.assertEquals(2, frvidal.getNumberOfFiles());
		
    	Contributor haddock = contributors.stream()
    			.filter(cont -> cont.getIdStaff() == 4)
    			.findFirst()
    			.orElseThrow(SkillerException::new); 	
		Assert.assertEquals(1, haddock.getNumberOfFiles());
	}
}
