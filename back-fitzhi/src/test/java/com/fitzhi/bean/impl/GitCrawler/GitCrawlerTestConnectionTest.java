package com.fitzhi.bean.impl.GitCrawler;

import static com.fitzhi.service.ConnectionSettingsType.DIRECT_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.PUBLIC_LOGIN;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.git.GitCrawler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * <p>
 * Testing the method {@link GitCrawler#testConnection(Project)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GitCrawlerTestConnectionTest {

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
		project.setConnectionSettings(PUBLIC_LOGIN);
		Assert.assertTrue(scanner.testConnection(project));
	}
	
	@Test
	public void testConnectionFailed() throws ApplicationException {
		project = new Project(4, "UNREACHABLE PROJECT");
		project.setUrlRepository("https://github.com/fvidal/wibkac");
		project.setConnectionSettings(DIRECT_LOGIN);
		project.setUsername("frvidal");
		String encryptedPassword = DataEncryption.encryptMessage("invalid password");
		project.setPassword(encryptedPassword);
		Assert.assertFalse(scanner.testConnection(project));
	}
	
}
