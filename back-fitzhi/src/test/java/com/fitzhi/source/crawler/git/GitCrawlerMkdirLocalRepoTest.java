package com.fitzhi.source.crawler.git;

import  java.nio.file.Path;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class is testing the method {@link GitCrawler#mkdirLocalRepo}
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
public class GitCrawlerMkdirLocalRepoTest {
   
	@Test
	public void nominal() throws ApplicationException {
		Path path  = GitCrawler.mkdirLocalRepo(new Project(1, "my name of project"));
		Assert.assertTrue(path.getFileName().toString().indexOf("fitzhi_jgit_my_name_of_project_") == 0);
	}
	
	@Test
	public void userRights() throws ApplicationException {
		Path path  = GitCrawler.mkdirLocalRepo(new Project(1, "my name of project"));
		Assert.assertTrue(path.toFile().canRead());
		Assert.assertTrue(path.toFile().canWrite());
		Assert.assertFalse(path.toFile().canExecute());
	}

}
