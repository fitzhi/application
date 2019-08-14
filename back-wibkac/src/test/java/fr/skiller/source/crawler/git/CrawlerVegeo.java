/**
 * 
 */
package fr.skiller.source.crawler.git;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.data.internal.Project;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.RepoScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerVegeo {

	private static final String FILE_GIT = "../git_repo_for_test/%s";

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;

	@Test
	public void testParseRepository() throws IOException, SkillerException, GitAPIException {
		Project prj = new Project (777, "vegeo");
		prj.setLocationRepository(new File(String.format(FILE_GIT, "vegeo")).getCanonicalPath());
		
		scanner.parseRepository(prj, new ConnectionSettings());
	}

}