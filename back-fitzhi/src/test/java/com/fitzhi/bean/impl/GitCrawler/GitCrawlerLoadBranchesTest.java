package com.fitzhi.bean.impl.GitCrawler;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.fitzhi.Global;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

import org.eclipse.jgit.lib.Ref;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Testing the method {@link GitCrawler#retrieveConnection(Project)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GitCrawlerLoadBranchesTest {

	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	private Project project;
	
	@Test
	public void testConnectionPublic() throws Exception {
		project = new Project(4, "FITZHI");
		project.setUrlRepository("https://github.com/fitzhi/application");
		project.setConnectionSettings(Global.NO_USER_PASSWORD_ACCESS);
		Collection<Ref> branches = scanner.loadBranches(project);
		branches.stream()
			.map(Ref::getLeaf)
			.map(Ref::getName)
			.filter(s -> s.contains("refs/heads/"))
			.distinct()
			.forEach(log::debug);
		Assert.assertTrue(branches.size() > 0);
		Set<String> uniqueBranches = branches.stream().map(Ref::getName).collect(Collectors.toSet());
		Assert.assertTrue(uniqueBranches.contains("refs/heads/master"));
	}
	
	@Test
	public void testConnectionFailed() throws ApplicationException {
		project = new Project(4, "UNREACHABLE PROJECT");
		project.setUrlRepository("https://github.com/fvidal/wibkac");
		project.setConnectionSettings(Global.USER_PASSWORD_ACCESS);
		project.setUsername("frvidal");
		String encryptedPassword = DataEncryption.encryptMessage("invalid password");
		project.setPassword(encryptedPassword);
		Collection<Ref> branches = scanner.loadBranches(project);
		Assert.assertTrue(branches.isEmpty());
	}
	
}
