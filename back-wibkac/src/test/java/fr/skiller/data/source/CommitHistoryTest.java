/**
 * 
 */
package fr.skiller.data.source;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.bean.StaffHandler;

/**
 * Testing the CommitHistory class
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommitHistoryTest {

	@Autowired
	private StaffHandler staffHandler;

    CommitHistory ch = null;
    
    @Before
    public void before() {
		ch = new CommitHistory("test", 1);
		ch.addOperation(new Operation(1, LocalDate.of(2018, 11, 1)));
		ch.addOperation(new Operation(1, LocalDate.of(2018, 10, 17)));
		ch.addOperation(new Operation(2, LocalDate.of(2018, 11, 25)));
		ch.addOperation(new Operation(3, LocalDate.of(2017, 11, 1)));

		staffHandler.getStaff().get(1).setActive (false);
		staffHandler.getStaff().get(2).setActive (true);
		staffHandler.getStaff().get(3).setActive (true);
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
        
}
