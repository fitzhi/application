package com.fitzhi.source.crawler.git;

import  java.nio.file.Path;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.apache.commons.lang3.SystemUtils;
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
		if(SystemUtils.IS_OS_UNIX) {
			Assert.assertTrue(path.toFile().canRead());
			Assert.assertTrue(path.toFile().canWrite());
			Assert.assertFalse(path.toFile().canExecute());
		} else {
			Assert.assertTrue(SystemUtils.IS_OS_WINDOWS);
		}
	}

}
