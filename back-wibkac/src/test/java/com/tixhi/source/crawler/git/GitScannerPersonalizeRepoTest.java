/**
 * 
 */
package com.tixhi.source.crawler.git;

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

import com.tixhi.controller.ProjectController;
import com.tixhi.controller.in.SettingsGeneration;
import com.tixhi.data.source.BasicCommitRepository;
import com.tixhi.data.source.CommitRepository;
import com.tixhi.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitScannerPersonalizeRepoTest  {
	
	Logger logger = LoggerFactory.getLogger(GitScannerPersonalizeRepoTest.class.getCanonicalName());
	
	/**
	 * Source control parser.
	 */
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	/**
	 * Project controller.
	 */
	@Autowired
	ProjectController projectController;
	
	@Test
	public void test() {
		CommitRepository repo = new BasicCommitRepository();
		repo.addCommit("A", 1, "one", new Date(System.currentTimeMillis()), 1);
		repo.addCommit("A", 1, "one", new Date(System.currentTimeMillis()-1000), 1);
		repo.addCommit("A", 1, "one", new Date(System.currentTimeMillis()-10000), 1);
		repo.addCommit("A", 2, "two", new Date(System.currentTimeMillis()-2000), 1);
		repo.addCommit("A", 2, "two", new Date(System.currentTimeMillis()-20000), 1);
		repo.addCommit("A", 3, "three", new Date(System.currentTimeMillis()-5000), 1);

		repo.addCommit("B", 2, "two", new Date(System.currentTimeMillis()), 1);
		repo.addCommit("B", 2, "two", new Date(System.currentTimeMillis()-1000), 1);
		repo.addCommit("B", 2, "two", new Date(System.currentTimeMillis()-10000), 1);
		repo.addCommit("B", 3, "three", new Date(System.currentTimeMillis()-2000), 1);
		repo.addCommit("B", 3, "three", new Date(System.currentTimeMillis()-20000), 1);
		repo.addCommit("B", 1, "one", new Date(System.currentTimeMillis()-5000), 1);

		CommitRepository personalRepo = scanner.personalizeRepo(repo, new SettingsGeneration(-1, 2));		
		Assert.assertEquals( 2 ,personalRepo.getRepository().get("A").operations.size());
		Assert.assertEquals( 3 ,personalRepo.getRepository().get("B").operations.size());
		
		personalRepo = scanner.personalizeRepo(repo, new SettingsGeneration(-1, 1));
		Assert.assertEquals( 3 ,personalRepo.getRepository().get("A").operations.size());
		Assert.assertEquals( 1 ,personalRepo.getRepository().get("B").operations.size());
		
	}
}
