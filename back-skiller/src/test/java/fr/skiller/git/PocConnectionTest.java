/**
 * 
 */
package fr.skiller.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevCommitList;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.TreeWalk.OperationType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.eclipse.jgit.lib.Constants;

import fr.skiller.data.JsonTest;
import fr.skiller.data.internal.Skill;
import fr.skiller.opennlp.PocNLP;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing some simple action with jgit
 */
public class PocConnectionTest {

	Logger logger = LoggerFactory.getLogger(PocConnectionTest.class.getCanonicalName());
	private static File resourcesDirectory = new File("src/test/resources");

	ObjectId headId;

	class Property {
		/**
		 * Empty Constructor.
		 */
		public Property() {
			super();
		}
		String url;
		String login;
		String password;
	}
	Property prop;
	
	@Before
	public void before() throws IOException {
		Gson gson = new GsonBuilder().create();
		final FileReader fr = new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/poc_git/properties.json"));
		prop = new Property();
		prop = gson.fromJson(fr, prop.getClass());
		fr.close();
	}
	
  	@Test
	public void testConnectionRepo() throws Exception {
		Path path = Files.createTempDirectory("skiller_jgit");
		logger.debug(path.toString());
		

		Git git = Git
				.cloneRepository()
				.setDirectory(path.toFile())
				.setURI(prop.url)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(prop.login, prop.password))
				.call();

		Repository repo = git.getRepository();

		repo.getRefDatabase().getRefs().stream().forEach(ref -> logger.debug(ref.getName()));

		repo.getRefDatabase().getRefs().stream().forEach(ref -> {
			if (Constants.HEAD.equals(ref.getName())) {
				headId = ref.getObjectId();
			}
		});

		
		RevWalk revWalk = new RevWalk(repo);
		RevCommit start = revWalk.parseCommit(headId);
		revWalk.markStart(start);
		
		RevCommitList<RevCommit> list = new RevCommitList<RevCommit>();
		list.source(revWalk);
		list.fillTo(Integer.MAX_VALUE);
		
		final File out = new File("git-scan.csv");
		final BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		
		int numLine = 0;
		TreeWalk treeWalk = new TreeWalk(repo);
		for (RevCommit commit : list) {
//			logger.debug("id : " + commit.getCommitterIdent().getName() + " " + commit.getShortMessage());
			treeWalk.reset();
	        treeWalk.addTree(commit.getTree());
	        treeWalk.setRecursive(true);
	        while (treeWalk.next()) {
        		writer.write (treeWalk.getPathString());
        		writer.write (";");
        		writer.write (commit.getCommitterIdent().getName());
        		writer.write (";");
        		writer.write (commit.getAuthorIdent().getWhen().toString());
        		writer.write (";");
        		writer.write (treeWalk.getOperationType().toString());
        		writer.newLine();
        		numLine++;
	        }
	        if (numLine > 1000) break;
		}
		logger.debug(numLine + " lines retrieved from the reporitory");
		
		writer.flush();
		writer.close();
		
		treeWalk.close();
		revWalk.close();
		git.close();
	}
	
	
}
