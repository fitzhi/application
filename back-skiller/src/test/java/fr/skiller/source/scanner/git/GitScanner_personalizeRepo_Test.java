/**
 * 
 */
package fr.skiller.source.scanner.git;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.source.scanner.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitScanner_personalizeRepo_Test  {
	
	Logger logger = LoggerFactory.getLogger(GitScanner_personalizeRepo_Test.class.getCanonicalName());
	
	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Test
	public void test() {
		CommitRepository repo = new BasicCommitRepository();
		repo.addCommit("A", 1, new Date(System.currentTimeMillis()));
		repo.addCommit("A", 1, new Date(System.currentTimeMillis()-1000));
		repo.addCommit("A", 1, new Date(System.currentTimeMillis()-10000));
		repo.addCommit("A", 2, new Date(System.currentTimeMillis()-2000));
		repo.addCommit("A", 2, new Date(System.currentTimeMillis()-20000));
		repo.addCommit("A", 3, new Date(System.currentTimeMillis()-5000));

		repo.addCommit("B", 2, new Date(System.currentTimeMillis()));
		repo.addCommit("B", 2, new Date(System.currentTimeMillis()-1000));
		repo.addCommit("B", 2, new Date(System.currentTimeMillis()-10000));
		repo.addCommit("B", 3, new Date(System.currentTimeMillis()-2000));
		repo.addCommit("B", 3, new Date(System.currentTimeMillis()-20000));
		repo.addCommit("B", 1, new Date(System.currentTimeMillis()-5000));

		CommitRepository personalRepo = new BasicCommitRepository();
		scanner.personalizeRepo(repo, personalRepo, 2);		
		Assert.assertEquals( 2 ,personalRepo.getRepository().get("A").operations.size());
		Assert.assertEquals( 3 ,personalRepo.getRepository().get("B").operations.size());
		
		personalRepo = new BasicCommitRepository();
		scanner.personalizeRepo(repo, personalRepo, 1);
		Assert.assertEquals( 3 ,personalRepo.getRepository().get("A").operations.size());
		Assert.assertEquals( 1 ,personalRepo.getRepository().get("B").operations.size());
		
	}
}
