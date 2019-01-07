/**
 * 
 */
package fr.skiller.source.scanner.git;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevCommitList;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.SunburstData;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.scanner.AbstractScannerDataGenerator;
import fr.skiller.source.scanner.RepoScanner;
import fr.skiller.Error;
import fr.skiller.Global;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.StaffHandler;
import static fr.skiller.Global.UNKNOWN;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL GIT implementation of a code Scanner
 */
@Service("GIT")
public class GitScanner extends AbstractScannerDataGenerator implements RepoScanner {

	/**
	 * Cleanup patterns list.
	 */
 	private List<Pattern> patternsCleanupList;

 	/**
 	 * List of file patterns, to be included, or excluded, from the parsing process
 	 */
	private List<Pattern> patternsInclusionList;

 	/**
 	 * The logger for the GitScanner.
 	 */
	Logger logger = LoggerFactory.getLogger(GitScanner.class.getCanonicalName());

	/**
	 * These directories will be removed from the full path of class files<br/>
	 * For example : <code>/src/main/java/java/util/List.java</code> will be treated like <code>java/util/List.java</code>
	 */
	@Value("${patternsCleanup}")
	private String patternsCleanup;
	
	/**
	 * Service in charge of handling the staff collection.
	 */
	@Autowired
	StaffHandler staffHandler;

	/**
	 * Service in charge of caching the parsed repository.
	 */
	@Autowired
	CacheDataHandler cacheDataHandler;
	
	/**
	 * Path access to retrieve the properties file for a given project  
	 * It might be, for example, /src/main/resources/repository-settings/properties-{0}.json where {0} represents the project name.
	 */
	@Value("${repositoryPathPatternSettings}")
	private String repositoryPathPatternSettings;
	
	
	/**
	 * Patterns to take account, OR NOT, a file within the parsing process.<br/>
	 * For example, a file with the suffix .java is involved.
	 */
	@Value("${patternsInclusion}")
	private String patternsInclusion;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new Gson();
	
	/**
	 * GitScanner constructor.
	 */
	public GitScanner() {
	}
	
	@PostConstruct
    public void init() {
		
		patternsCleanupList = 
				Arrays.asList(patternsCleanup.split(";"))
				.stream()
				.map( s -> Pattern.compile(s) )
				.collect( Collectors.toList());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Pattern CLEANUP loaded from the file application.properties : ");
			patternsCleanupList.stream().forEach(p -> logger.debug(p.pattern()));
		}
 		
		patternsInclusionList = 
				Arrays.asList(patternsInclusion.split(";"))
				.stream()
				.map( s -> Pattern.compile(s) )
				.collect( Collectors.toList());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Pattern INCLUSION loaded from the file application.properties : ");
			patternsInclusionList.stream().forEach(p -> logger.debug(p.pattern()));
		}

		// We "Spring-way" injected staff manager handle into the super class.
		super.staffHandler = staffHandler;
	}
	
	@Override
	public void clone(final Project project, ConnectionSettings settings) throws Exception {

		// Creating a temporary local path where the remote project repository will be cloned.
		Path path = Files.createTempDirectory("skiller_jgit_" + project.name + "_");
		if (logger.isDebugEnabled()) {
			logger.debug("Using GIT repository path " + settings.url + " cloned in " + path.toAbsolutePath());
		}

		Git.cloneRepository().setDirectory(path.toAbsolutePath().toFile()).setURI(settings.url)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(settings.login, settings.password))
				.call();

		// Saving the local repository location
		settings.localRepository = path.toAbsolutePath().toString();
		
	}

	@Override
	public CommitRepository parseRepository(final Project project, ConnectionSettings settings) throws Exception {
 
		// Test if this repository is available in cache 
		if (cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using cache file for project " + project.name);
			}
			return cacheDataHandler.getRepository(project);
		}
		
		if (settings.localRepository == null) {
			throw new SkillerException(Error.CODE_REPO_MUST_BE_ALREADY_CLONED, Error.MESSAGE_REPO_MUST_BE_ALREADY_CLONED);
		}
		
  		final Git git = Git.open(new File(settings.localRepository));

		Repository repo = git.getRepository();
		ObjectId headId = repo.resolve(Constants.HEAD);
		
		RevWalk revWalk = new RevWalk(repo);
		RevCommit start = revWalk.parseCommit(headId);
		revWalk.markStart(start);
		
		RevCommitList<RevCommit> list = new RevCommitList<RevCommit>();
		list.source(revWalk);
		list.fillTo(Integer.MAX_VALUE);
				
		final CommitRepository repositoryOfCommit = new BasicCommitRepository();
		
		TreeWalk treeWalk = new TreeWalk(repo);
		for (RevCommit commit : list) {
			if (logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append(Global.LN)
					.append ("shortMessage : " + commit.getShortMessage())
					.append(Global.LN)
					.append("date : " + commit.getAuthorIdent().getWhen())
					.append (Global.LN)
					.append("authorIdent.name : " + commit.getAuthorIdent().getName())
					.append (Global.LN).append(Global.LN);
				logger.debug(sb.toString());
			}	
			treeWalk.reset();
	        treeWalk.addTree(commit.getTree());
	        treeWalk.setRecursive(true);
	        
	        for (RevCommit parent : commit.getParents()) {
	        	treeWalk.addTree(parent.getTree());
	        }
	        
	        // Treatment cache containing the mapping between the criteria retrieved from GIT and the associated staff member
	        Map<String, Staff> cacheCriteriaStaff = new HashMap<String, Staff>();
	        
	        while (treeWalk.next()) {

	        	if (isElligible(treeWalk.getPathString())) {
					int similarParents = 0;
					for (int i = 1; i < treeWalk.getTreeCount(); i++) {
						if (treeWalk.getFileMode(i) == treeWalk.getFileMode(0) && treeWalk.getObjectId(0).equals(treeWalk.getObjectId(i)))
							similarParents++;
					}
					if (similarParents == 0) {
						String sourceCodePath = cleanupPath(treeWalk.getPathString());
						Staff staff = null;
						String author = commit.getCommitterIdent().getName();
						
						if (!cacheCriteriaStaff.containsKey(author)) {
							staff = staffHandler.lookup(author);
							if ((staff == null) & logger.isDebugEnabled()) {
								logger.debug(author + " will be considered as unknown." );
							}
							cacheCriteriaStaff.put(author, staff);
						} else {
							staff = cacheCriteriaStaff.get(author);
						}
						
						repositoryOfCommit.addCommit(
								sourceCodePath, 
								(staff != null) ? staff.idStaff : UNKNOWN,
								commit.getAuthorIdent().getWhen());
					}
	        	}
	        }
		}
		
		treeWalk.close();
		revWalk.close();
		git.close();
		
		// Saving the repository into the cache
		cacheDataHandler.saveRepository(project, repositoryOfCommit);
		
		return repositoryOfCommit;
	}


	// Not a real class variable member : the variable is declared there in order to be updated within the lambda expression in the method below 
 	private boolean select = false;
 	
 	/**
 	 * Check if the path is an eligible source for the activity dashboard.
 	 * @param path
 	 * @return True if the path should be included
 	 */
 	boolean isElligible (final String path) {
 		
 		select = true;
 		
 		patternsInclusionList.stream().forEach(pattern -> {
 			Matcher matcher = pattern.matcher(path);
 			if (!matcher.find()) {
 				select = false;
 			}
 		});
 		
 		return select;
 	}
 
 	private String cleanupPath = "";
 	
 	/**
 	 * Extract from the filename path the non relevant directory (such as /src/main/java) 
 	 * @param path the given path
 	 * @return the cleanup file
 	 */
	private String cleanupPath (final String path) {

		cleanupPath = "";
 		
		patternsCleanupList.stream().forEach(pattern -> {
 			Matcher matcher = pattern.matcher(path);
 			if (matcher.find() && (cleanupPath.length()==0)) {
 				cleanupPath = path.substring(matcher.end());
 			}
 		});
 		return (cleanupPath.length() == 0) ? path : cleanupPath;
 	}

	@Override
	public SunburstData generate(final Project project) throws Exception {
		
		final ConnectionSettings settings = connectionSettings(project);

		if (!cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			this.clone(project, settings);
			if (logger.isDebugEnabled()) {
				logger.debug("The project " + project.name + " has id cloned into a temporay directory");
			}
		}	

		// If a cache is detected and available for this project, it will be returned from this method.
		final CommitRepository repo = this.parseRepository(project, settings);
		if (logger.isDebugEnabled()) {
			logger.debug(
					"The repository has been parsed. It contains "
					+ repo.size() + " records in the repository");
		}
		
		SunburstData data = this.aggregateSunburstData(repo);
		
		// Evaluate the risk for each directory, and sub-directory, in the repository.
		this.evaluateTheRisk(repo, data);
		
		// Fill the holes for directory without source files, and therefore without risk level measured.
		this.meanTheRisk(data);

		// Evaluate the preview display for each sclice of the sunburst chart.  
		this.setPreviewSettings(data);

		return data;
	}

	private ConnectionSettings connectionSettings(Project project) throws Exception {

		final String fileProperties = MessageFormat.format (repositoryPathPatternSettings, project.name);

		ConnectionSettings settings = new ConnectionSettings();
		final FileReader fr = new FileReader(new File(fileProperties));
		settings = gson.fromJson(fr, settings.getClass());
		fr.close();
		if (logger.isDebugEnabled()) {
			logger.debug("GIT remote URL " + settings.url);
		}
		return settings;
	}
}
