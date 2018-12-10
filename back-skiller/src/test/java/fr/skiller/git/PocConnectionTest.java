/**
 * 
 */
package fr.skiller.git;

import java.io.BufferedReader;
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

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.attributes.Attribute;
import org.eclipse.jgit.attributes.Attributes;
import org.eclipse.jgit.blame.BlameResult;
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
import org.eclipse.jgit.lib.FileMode;

import fr.skiller.data.JsonTest;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.opennlp.PocNLP;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing some simple action with jgit
 */
public class PocConnectionTest {

	final String LN = System.getProperty("line.separator");

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
		String path;
	}
	Property prop;
	Path path;
	
	final String fileProperties = resourcesDirectory.getAbsolutePath() + "/poc_git/properties-SKILLER.json";
	@Before
	public void before() throws Exception {
		Gson gson = new GsonBuilder().create();
		final FileReader fr = new FileReader(new File(fileProperties));
		prop = new Property();
		prop = gson.fromJson(fr, prop.getClass());
		
		fr.close();
		if ( !new File(prop.path).exists() ) {
			// Creating the new path
			path = Files.createTempDirectory("skiller_jgit_vegeo");
			prop.path = path.toString();
			logger.debug("Using GIT repository path " + path.toString());
			
			// Writing the new Path.
			String s = gson.toJson(prop);
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(fileProperties));
			bw.write(s);
			bw.close();

			Git.cloneRepository()
				.setDirectory(path.toFile())
				.setURI(prop.url)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(prop.login, prop.password))
				.call();
			
		} else {
			logger.debug("Using GIT repository path " + prop.path);
		}
	}
	
  	@Test
	public void test() throws Exception {
  		final Git git = Git.open(new File(prop.path));

		Repository repo = git.getRepository();
		headId = repo.resolve(Constants.HEAD);

		BlameCommand blameCommand = git.blame();
		blameCommand.setStartCommit(headId);

		/*
		File f = new File("");
		System.out.println(f.getAbsolutePath());
		f = new File("src/main/java/fr/skiller/controler/StaffController.java");
		System.out.println(f.getAbsolutePath());
		System.out.println(f.exists());
		*/
		
		File f = new File("src/main/java/fr/skiller/controler/StaffController.java");
		System.out.println(f.exists());
		blameCommand.setFilePath(f.getAbsolutePath());
		
	    BlameResult blameResult = blameCommand.call();
	    
	    System.out.println(blameResult.getResultContents());
	    
	    
	    blameResult.computeAll();
	    System.out.println(blameResult.getResultContents());
		
  	}
	public void testConnectionRepo() throws Exception {
  		final Git git = Git.open(new File(prop.path));

		Repository repo = git.getRepository();
		headId = repo.resolve(Constants.HEAD);
		
		repo.getRefDatabase().getRefs().stream().forEach(ref -> {
			System.out.println (ref.getName());
		});

		RevWalk revWalk = new RevWalk(repo);
		RevCommit start = revWalk.parseCommit(headId);
		revWalk.markStart(start);
		
		RevCommitList<RevCommit> list = new RevCommitList<RevCommit>();
		list.source(revWalk);
		list.fillTo(Integer.MAX_VALUE);
		
		final File out = new File("git-scan.txt");
		final BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		
		final CommitRepository repositoryOfCommit = new BasicCommitRepository();
		
		TreeWalk treeWalk = new TreeWalk(repo);
		final StringBuilder sb = new StringBuilder();
		for (RevCommit commit : list) {
			sb.append(commit.getShortMessage()).append(LN);
			treeWalk.reset();
	        treeWalk.addTree(commit.getTree());
	        treeWalk.setRecursive(true);
	        Attributes atrs = treeWalk.getAttributes();
	        if (atrs != null) {
		        Collection<Attribute> c = atrs.getAll();
		        for (Attribute atr : c) {
		        	System.out.println(atr.getKey() + " " + atr.getValue());
		        }
	        }
	        /**
	        while (treeWalk.next()) {
	        	String path = treeWalk.getPathString().replace("back-skiller/src/main/java", "").replace("back-skiller/src/test/java", "");
	        	sb.append(path).append(LN);

	        	// repositoryOfCommit.addCommit(treeWalk.getPathString(), commit.getCommitterIdent().getName(), commit.getAuthorIdent().getWhen());
	        }
	        */
		}
		
//		writer.write(repositoryOfCommit.extractCSV());
		writer.write(sb.toString());
		writer.flush();
		writer.close();
		
		treeWalk.close();
		revWalk.close();
		git.close();
	}
	
	
}
