/**
 * 
 */
package fr.skiller.git;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevCommitList;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import static fr.skiller.Global.UNKNOWN;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing some simple action with jgit
 */
public class PocConnectionTestByPassable {

	final String LN = System.getProperty("line.separator");

	Logger logger = LoggerFactory.getLogger(PocConnectionTestByPassable.class.getCanonicalName());
	
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
		String email;
		String path;
	}
	Property prop;
	Path path;
	
	final String fileProperties = resourcesDirectory.getAbsolutePath() + "/poc_git/properties-VEGEO.json";
	
	private boolean bypass = false;
		
	@Before
	public void before() throws Exception {

		if ("Y".equals(System.getProperty("bypass"))) {
			bypass = true;
		}

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
	
  	
 	final List<Pattern> patternsInclusionList = new ArrayList<Pattern>();

 	boolean select = false;
 	
 	/**
 	 * Check if the path is an eligible source for the activity dashboard.
 	 * @param path
 	 * @return True if the path should be included
 	 */
 	boolean isElligible (final String path) {
 		
 		select = true;
 		
 		if (patternsInclusionList.isEmpty()) {
 			patternsInclusionList.add(Pattern.compile("(.java$|.js$|.css$|.html$|.ts$)")); 			
 			patternsInclusionList.add(Pattern.compile("^(?!.*/app/vendor/).*$")); 			
 			patternsInclusionList.add(Pattern.compile("^(?!.*/node_modules/).*$")); 			
 		}
 		
 		patternsInclusionList.stream().forEach(pattern -> {
 			Matcher matcher = pattern.matcher(path);
 			if (!matcher.find()) {
 				select = false;
 			}
 		});
 		
 		return select;
 	}
 
 	final List<Pattern> patternsfilteredList = new ArrayList<Pattern>();

 	String filteredPath = "";
 	
 	/**
 	 * 
 	 * @param path
 	 * @return
 	 */
	public String filterPath (final String path) {
 		
		filteredPath = "";
		
 		if (patternsfilteredList.isEmpty()) {
 			patternsfilteredList.add(Pattern.compile("/src/main/java/"));
 			patternsfilteredList.add(Pattern.compile("/src/test/java/"));
 			patternsfilteredList.add(Pattern.compile("/src/main/resources/"));
 			patternsfilteredList.add(Pattern.compile("/src/test/resources/"));
 		}
 		
 		patternsfilteredList.stream().forEach(pattern -> {
 			Matcher matcher = pattern.matcher(path);
 			if (matcher.find() && (filteredPath.length()==0)) {
 				filteredPath = path.substring(matcher.end());
 			}
 		});
 		return (filteredPath.length() == 0) ? path : filteredPath;
 	}
 	
 	@Test
	public void testConnectionRepo() throws Exception {
 				
 		if (bypass) return;
 		
  		final Git git = Git.open(new File(prop.path));

		Repository repo = git.getRepository();
		headId = repo.resolve(Constants.HEAD);
		
		RevWalk revWalk = new RevWalk(repo);
		RevCommit start = revWalk.parseCommit(headId);
		revWalk.markStart(start);
		
		RevCommitList<RevCommit> list = new RevCommitList<RevCommit>();
		list.source(revWalk);
		list.fillTo(Integer.MAX_VALUE);
				
		final CommitRepository repositoryOfCommit = new BasicCommitRepository();
		
		TreeWalk treeWalk = new TreeWalk(repo);
		for (RevCommit commit : list) {
			treeWalk.reset();
	        treeWalk.addTree(commit.getTree());
	        treeWalk.setRecursive(true);
	        
	        for (RevCommit parent : commit.getParents()) {
	        	treeWalk.addTree(parent.getTree());
	        }
	        
	        while (treeWalk.next()) {

	        	if (isElligible(treeWalk.getPathString())) {
					int similarParents = 0;
					for (int i = 1; i < treeWalk.getTreeCount(); i++) {
						if (treeWalk.getFileMode(i) == treeWalk.getFileMode(0) 
								&& treeWalk.getObjectId(0).equals(treeWalk.getObjectId(i)))
							similarParents++;
					}
					if (similarParents == 0) {
						String str = filterPath(treeWalk.getPathString());
						repositoryOfCommit.addCommit(
								str, 
								UNKNOWN,
								commit.getAuthorIdent().getWhen());
					}
	        	}
	        }
		}
		
		final File out = new File("git-scan.csv");
		final BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		writer.write(repositoryOfCommit.extractCSV());
		writer.flush();
		writer.close();
		
		treeWalk.close();
		revWalk.close();
		git.close();
	}
	
	
}
