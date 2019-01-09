/**
 * 
 */
package fr.skiller.data.source;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;

/**
 * Testing the CommitHistory class
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommitHistoryTest {

    @Autowired
	private StaffHandler staffHandler;

    CommitHistory ch = null;
    
    @Before
    public void before() {
		ch = new CommitHistory("test");
		ch.addOperation(new Operation(1, new GregorianCalendar(2018, 12, 1).getTime()));
		ch.addOperation(new Operation(1, new GregorianCalendar(2018, 11, 17).getTime()));
		ch.addOperation(new Operation(2, new GregorianCalendar(2018, 12, 25).getTime()));
		ch.addOperation(new Operation(3, new GregorianCalendar(2017, 12, 1).getTime()));

		staffHandler.getStaff().get(1).isActive = false;
		staffHandler.getStaff().get(2).isActive = true;
		staffHandler.getStaff().get(3).isActive = true;
    }
    
    @Test
	public void countDistinctDevelopers() {
		Assert.assertEquals(3, ch.countDistinctDevelopers());
	}

    @Test
 	public void countDistinctActiveDevelopers() {
 		Assert.assertEquals(2, ch.countDistinctActiveDevelopers(staffHandler));
 	}

    @Test
    public void ultimateContributor() {
    	Assert.assertEquals(2, ch.ultimateContributor());
    }

    @Test
	public void countCommitsByActiveDevelopers() {
		Assert.assertEquals(2, ch.countCommitsByActiveDevelopers(staffHandler));
	}

    @Test
	public void countCommits() {
		Assert.assertEquals(4, ch.countCommits());
	}
    
    @Test
	public void collectors() {
    	CommitRepository commit = new BasicCommitRepository();
    	commit.addCommit("test.java", 1, new Date());
    	commit.addCommit("test.java", 7, new Date());
    	commit.addCommit("test.java", 10, new Date());
    	commit.addCommit("other_test.java", 5, new Date());
    	commit.addCommit("other_test.java", 10, new Date());
    	int iStaff[] = {1, 5, 7, 10};
		Assert.assertArrayEquals(iStaff, 
				commit.contributors().stream()
				.mapToInt(contributor -> contributor.idStaff).toArray());
	}
    
}
