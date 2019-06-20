/**
 * 
 */
package fr.skiller.source.crawler.git;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevCommitList;
import org.eclipse.jgit.revwalk.RevFlag;
import org.eclipse.jgit.revwalk.RevFlagSet;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.source.crawler.RepoScanner;

import org.eclipse.jgit.attributes.Attribute;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerTest {

	private static final String FILE_GIT = "../git_repo_for_test/first-test/.git";

	private Repository repository = null;

	@Autowired
	@Qualifier("GIT")
	RepoScanner scanner;
	
	@Before
	public void load() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(new File(FILE_GIT)).readEnvironment().findGitDir().build();
	}

	@Test
	public void loadChanges() throws IOException {
		List<SCMChange> gitChanges = scanner.loadChanges(repository);
		gitChanges.stream().map(SCMChange::getPath).forEach(System.out::println);
	}
	
	public void test() throws IOException {

		// a RevWalk allows to walk over commits based on some filtering that is defined
		RevWalk walk = new RevWalk(repository);
		ObjectId headId = repository.resolve(Constants.HEAD);
		RevCommit start = walk.parseCommit(headId);
		walk.markStart(start);
		
		RevCommitList<RevCommit> list = new RevCommitList<>();
		list.source(walk);
		list.fillTo(Integer.MAX_VALUE);
		
		TreeWalk treeWalk = new TreeWalk(repository);
		for (RevCommit commit : list) {

	    	RenameDetector rd = new RenameDetector(repository);
	
	    	
			System.out.print("-> " + commit.getId() + " " + commit.getFullMessage());

			
			treeWalk.reset();
			RevTree revTree = commit.getTree();
	        treeWalk.addTree(commit.getTree());
	        
	        
	        treeWalk.setRecursive(true);

	        for (RevCommit parent : commit.getParents()) {
				System.out.print("---------> " +  parent.getId() + " " + parent.getFullMessage());	 
				RevTree rt = parent.getTree();
				System.out.println("---------> " +  rt.getId());	 
	        	treeWalk.addTree(rt);
	        }
	        
	        while (treeWalk.next()) {
	        	if (treeWalk.getTreeCount() == 1) {
	        		System.out.println(treeWalk.getOperationType() + " " + treeWalk.getPathString());
	        	}
	        	if (treeWalk.getTreeCount() == 2) {
		        	rd.addAll(DiffEntry.scan(treeWalk));
		        	List<DiffEntry> files = rd.compute();
		        	for (DiffEntry de : files) {
		        		System.out.println(de.getChangeType() + " " + de.getOldPath() + " " + de.getNewPath());
		        	}
	        	}
	        	System.out.println(treeWalk.getPathString() + " " + treeWalk.getFileMode() + " " + treeWalk.getDepth() + " " + treeWalk.getPathString());
	        }
		}		
		treeWalk.close();
		walk.close();
	}

	@After
	public void after() {
		if (repository != null) {
			repository.close();
		}
	}
}