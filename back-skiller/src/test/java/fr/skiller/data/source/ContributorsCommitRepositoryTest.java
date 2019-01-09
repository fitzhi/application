/**
 * 
 */
package fr.skiller.data.source;

import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the creation of contributors based on stats retrieve in the source repository.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ContributorsCommitRepositoryTest {
	
	
	CommitRepository repo;
	
    @Before
    public void before() {
    	
    	repo = new BasicCommitRepository();
    	
		repo.addCommit("test", 1, new GregorianCalendar(2018, 11, 1).getTime());
		repo.addCommit("test", 1, new GregorianCalendar(2018, 11, 17).getTime());
		repo.addCommit("test", 2, new GregorianCalendar(2018, 11, 23).getTime());
		repo.addCommit("test", 3, new GregorianCalendar(2018, 11, 14).getTime());

		
		repo.addCommit("test2", 1, new GregorianCalendar(2018, 11, 2).getTime());
		repo.addCommit("test2", 2, new GregorianCalendar(2018, 11, 23).getTime());
		repo.addCommit("test2", 2, new GregorianCalendar(2018, 11, 14).getTime());
		repo.addCommit("test2", 2, new GregorianCalendar(2018, 11, 1).getTime());
		repo.addCommit("test2", 2, new GregorianCalendar(2018, 11, 10).getTime());
		repo.addCommit("test2", 3, new GregorianCalendar(2018, 11, 25).getTime());
		
		repo.addCommit("test3", 4, new GregorianCalendar(2018, 11, 2).getTime());
    }
    
    @Test
	public void numberOfCommits() {
		Assert.assertEquals(3, repo.numberOfCommits(1));
		Assert.assertEquals(5, repo.numberOfCommits(2));
		Assert.assertEquals(2, repo.numberOfCommits(3));
	}

    @Test
	public void lastCommit() {
		Assert.assertEquals(new GregorianCalendar(2018, 11, 17).getTime(), repo.lastCommit(1));
		Assert.assertEquals(new GregorianCalendar(2018, 11, 23).getTime(), repo.lastCommit(2));
		Assert.assertEquals(new GregorianCalendar(2018, 11, 25).getTime(), repo.lastCommit(3));
	}

    @Test
	public void numberOfFiles() {
		Assert.assertEquals(2, repo.numberOfFiles(1));
		Assert.assertEquals(1, repo.numberOfFiles(4));
	}
}
