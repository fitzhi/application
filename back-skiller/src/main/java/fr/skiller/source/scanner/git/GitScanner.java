/**
 * 
 */
package fr.skiller.source.scanner.git;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.management.RuntimeErrorException;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.internal.RiskChartData;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitHistory;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.data.source.Contributor;
import fr.skiller.data.source.Operation;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.scanner.AbstractScannerDataGenerator;
import fr.skiller.source.scanner.RepoScanner;
import fr.skiller.Error;
import fr.skiller.Global;
import fr.skiller.bean.AsyncTask;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import fr.skiller.controller.ProjectController.SettingsGeneration;

import static fr.skiller.Global.LN;
import static fr.skiller.Global.UNKNOWN;
import static fr.skiller.controller.ProjectController.DASHBOARD_GENERATION;
import static fr.skiller.Error.CODE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static fr.skiller.Error.MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static fr.skiller.Error.CODE_UNEXPECTED_VALUE_PARAMETER;
import static fr.skiller.Error.MESSAGE_UNEXPECTED_VALUE_PARAMETER;

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
	final Logger logger = LoggerFactory.getLogger(GitScanner.class.getCanonicalName());

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
	 * Service in charge of handling the projects.
	 */
	@Autowired
	ProjectHandler projectHandler;
	
	/**
	 * Service in charge of caching the parsed repository.
	 */
	@Autowired
	CacheDataHandler cacheDataHandler;
	
	/**
	 * Service in charge of the evaluation of the risks.
	 */
	@Autowired()
	@Qualifier("commitAndDevActive")
	RiskProcessor riskSurveyor;
	
	/**
	 * Collection of active tasks.
	 */
	@Autowired()
	AsyncTask tasks;
	
	/**
	 * Path access to retrieve the properties file for a given project  
	 * By default, this file will be located in {@code /src/main/resources/repository-settings/}
	 */
	@Value("${versionControl.ConnectionSettings}")
	private String pathConnectionSettings;
	
	// Should the slices without source be average to the value of their children, or stayed in the void color.
	@Value("${Sunburst.fillTheHoles}")
	private boolean fillTheHoles;

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
		// This constructor is an empty one.
	}
	
	@PostConstruct
    public void init() {
		
        patternsCleanupList = 
				Arrays.asList(patternsCleanup.split(";"))
				.stream()
				.map( Pattern::compile )
				.collect( Collectors.toList());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Pattern CLEANUP loaded from the file application.properties : ");
			patternsCleanupList.stream().forEach(p -> logger.debug(p.pattern()));
		}
 		
		patternsInclusionList = 
				Arrays.asList(patternsInclusion.split(";"))
				.stream()
				.map(Pattern::compile)
				.collect( Collectors.toList());
		
		if (logger.isDebugEnabled()) {
			logger.debug("Pattern INCLUSION loaded from the file application.properties : ");
			patternsInclusionList.stream().forEach(p -> logger.debug(p.pattern()));
		}

		// We "Spring-way" injected staff manager handle into the super class.
		super.parentStaffHandler = staffHandler;
		super.parentProjectHandler = projectHandler;
	}
	
	@Override
	public void clone(final Project project, ConnectionSettings settings) throws Exception {

		// Creating a temporary local path where the remote project repository will be cloned.
		Path path = Files.createTempDirectory("skiller_jgit_" +  project.getName() + "_");
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"Using GIT repository path %s cloned in %s", 
					settings.getUrl(), path.toAbsolutePath()));
		}

		Git.cloneRepository().setDirectory(path.toAbsolutePath().toFile()).setURI(settings.getUrl())
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(settings.getLogin(), settings.getPassword()))
				.call();

		if (logger.isDebugEnabled()) {
			logger.debug("clone of repository done !");
		}
		// Saving the local repository location
		settings.setLocalRepository(path.toAbsolutePath().toString());
		
	}

	@Override
	public CommitRepository parseRepository(final Project project, final ConnectionSettings settings) throws Exception {
 
		// Test if this repository is available in cache. 
		// If this repository exists, return it immediately.
		if (cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Using cache file for project %s", project.getName()));
			}

			CommitRepository repository = cacheDataHandler.getRepository(project);
			
			// Since the last parsing of the repository, some developers might have been declared and are responding now to unknown pseudos.
			// We cleanup the set.
			repository.setUnknownContributors(
						repository
						.unknownContributors()	
						.stream()
						.filter(pseudo -> (staffHandler.lookup(pseudo) == null))
						.collect(Collectors.toSet())
						);
			
			return repository;
		}
		
		if (settings.getLocalRepository() == null) {
			throw new SkillerException(Error.CODE_REPO_MUST_BE_ALREADY_CLONED, Error.MESSAGE_REPO_MUST_BE_ALREADY_CLONED);
		}
		
		// Unknown committers
		final Set<String> unknown;
		
		// Repository 
		final CommitRepository repositoryOfCommit;
		
  		try (Git git = Git.open(new File(settings.getLocalRepository())) ) {

			Repository repo = git.getRepository();
			ObjectId headId = repo.resolve(Constants.HEAD);
			
			RevWalk revWalk = new RevWalk(repo);
			RevCommit start = revWalk.parseCommit(headId);
			revWalk.markStart(start);
			
			RevCommitList<RevCommit> list = new RevCommitList<>();
			list.source(revWalk);
			list.fillTo(Integer.MAX_VALUE);
					
			repositoryOfCommit = new BasicCommitRepository();
	
	        /**
	         * Set of unknown contributors having work on this repository.
	         */
	        unknown = repositoryOfCommit.unknownContributors();
			
			TreeWalk treeWalk = new TreeWalk(repo);
			for (RevCommit commit : list) {
				if (logger.isDebugEnabled()) {
					StringBuilder sb = new StringBuilder();
					sb.append(Global.LN)
						.append ("shortMessage : " + commit.getShortMessage())
						.append(Global.LN)
						.append("id : " + commit.getAuthorIdent().getEmailAddress())
						.append (Global.LN)
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
		        Map<String, Staff> cacheCriteriaStaff = new HashMap<>();
		        	        
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
								// The author contains 1 word
								// We check if this unknown author has a related developer in the ghosts collection
								if ( (staff == null) && (author.split(" ").length == 1) ) {
									Optional<Ghost> oGhost = project.getGhosts().stream()
										.filter(g -> (!g.isTechnical()))
										.filter(g -> 
											author.equalsIgnoreCase(g.getPseudo())
										).findFirst();
									if (oGhost.isPresent()) {
										Ghost selectedGhost =  oGhost.get();
										staff = staffHandler.getStaff().get(selectedGhost.getIdStaff());
										// We find a staff entry, but we keep the pseudo in the unknowns list
										// in order to be able to change the connection in the dedicated dialog box
										unknown.add(author);
									}
								}
								if (staff == null) {
									if (logger.isDebugEnabled()) {
										logger.debug(String.format("No staff found for the criteria %s", author));
									}
									unknown.add(author);
								}
								cacheCriteriaStaff.put(author, staff);
							} else {
								staff = cacheCriteriaStaff.get(author);
							}
							
							repositoryOfCommit.addCommit(
									sourceCodePath, 
									(staff != null) ? staff.getIdStaff() : UNKNOWN,
									commit.getAuthorIdent().getWhen());
						}
		        	}
		        }
			}
			
			treeWalk.close();
			revWalk.close();
  		}
		
        // Displaying results...
        if (logger.isWarnEnabled()) {
        	unknown.stream().forEach(logger::warn);
        }

        // Saving the repository into the cache
		cacheDataHandler.saveRepository(project, repositoryOfCommit);
		
		return repositoryOfCommit;
	}
 	
 	/**
 	 * Check if the path is an eligible source for the activity dashboard.
 	 * @param path
 	 * @return True if the path should be included
 	 */
 	boolean isElligible (final String path) {
 		
 		boolean select = true;
 		for (Pattern pattern : patternsInclusionList) {
 			Matcher matcher = pattern.matcher(path);
 			if (!matcher.find()) {
 				select = false;
 			}
 		}
 		
 		return select;
 	}
 
 	@Override
	public String cleanupPath (final String path) {

 		String cleanupPath = "";

 		for (Pattern pattern : patternsCleanupList) {
 			Matcher matcher = pattern.matcher(path);
 			if (matcher.find() && (cleanupPath.length()==0)) {
 				cleanupPath = path.substring(0, matcher.start()+1) + path.substring(matcher.end());
 			}
 		}
 		return (cleanupPath.length() == 0) ? path : cleanupPath;
 	}

	@Override
	@Async
	public RiskDashboard generateAsync(final Project project, final SettingsGeneration settings) throws Exception {
		try {
			tasks.addTask( DASHBOARD_GENERATION, "project", project.getId());
			return generate(project, settings);
		} finally {
			tasks.removeTask(DASHBOARD_GENERATION, "project", project.getId());
		}
	}
	
	@Override
	public RiskDashboard generate(final Project project, final SettingsGeneration cfgGeneration) throws Exception {
		
		final ConnectionSettings settings = connectionSettings(project);

		if (!cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			this.clone(project, settings);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("The project %s is cloned into a temporay directory", project.getName()));
			}
		}	

		// If a cache is detected and available for this project, it will be returned by this method.
		// This variable is not final. Might be overridden by the filtering operation (date of staff member filtering)
		CommitRepository repo = this.parseRepository(project, settings);
		if (logger.isDebugEnabled()) {
			logger.debug(
					"The repository has been parsed. It contains "
					+ repo.size() + " records in the repository");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Taking account of retrieved contributors from the repository into the project list of participants");
		}
		List<Contributor> contributors = staffHandler.involve(project, repo);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%d contributors retrieved : ", contributors.size()));
			contributors.stream().forEach(contributor -> {
				String fullname = staffHandler.getFullname(contributor.getIdStaff());
				logger.debug(String.format(
						"%d %s", 
						contributor.getIdStaff(), (fullname != null) ? fullname : "unknown"));
			});
		}

		// Does the process requires a personalization ?
		if (cfgGeneration.requiresPersonalization()) {
			repo = personalizeRepo(repo, cfgGeneration);
		}
		
		RiskDashboard data = this.aggregateDashboard(project, repo);
		
		// Evaluate the risk for each directory, and sub-directory, in the repository.
		final List<StatActivity> statsCommit = new ArrayList<>();
		this.riskSurveyor.evaluateTheRisk(repo, data.riskChartData, statsCommit);
		
		// Fill the holes for directories without source files, and therefore without risk level measured.
		// We do not fill the holes if the chart is filtered on a specific developer
		if (fillTheHoles && cfgGeneration.getIdStaffSelected()==0) {
			this.riskSurveyor.meanTheRisk(data.riskChartData);
		}
		
		// Evaluate the preview display for each slice of the sunburst chart.  
		this.riskSurveyor.setPreviewSettings(data.riskChartData);

		if (logger.isDebugEnabled()) {
			if ( (data.undefinedContributors != null) && (!data.undefinedContributors.isEmpty()) ) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unknown contributors detected during the dashboard generation").append(LN);
				data.undefinedContributors.stream().forEach(ukwn -> sb.append(ukwn.getCommitPseudo()).append(LN));
				logger.debug(sb.toString());
			}
			
			if ((project.getGhosts() != null) && (!project.getGhosts().isEmpty())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Registered ghosts in the project record :").append(LN);
				project.getGhosts().stream().forEach(g -> sb.append(g.getPseudo()).append(" : ").append(g.getIdStaff()).append("/").append(g.isTechnical()).append(LN));
				logger.debug(sb.toString());					
			}
		}
		return data;
	}

	@Override
	public CommitRepository personalizeRepo(CommitRepository globalRepo, SettingsGeneration settings) {
		final Date startingDate = new Date(settings.getStartingDate());
		if (logger.isDebugEnabled()) {
			logger.debug(
				MessageFormat.format(
					"Filtering the repositiory for id:{0}, and starting date:{1}", 
					settings.getIdStaffSelected(), startingDate));
		}
		CommitRepository personalizedRepo = new BasicCommitRepository(); 
		for (CommitHistory commits : globalRepo.getRepository().values()) {
			commits.operations.stream()
				.filter(it -> ((it.idStaff == settings.getIdStaffSelected()) || (settings.getIdStaffSelected() == 0)))
				.filter(it -> (it.dateCommit).after(startingDate))
				.forEach(item -> personalizedRepo.addCommit(commits.sourcePath, item.idStaff, item.dateCommit));
		}
		return personalizedRepo;
	}
	
	/**
	 * Load the connection settings for the given project.
	 * @param project the passed project
	 * @return the connection settings.
	 * @throws SkillerException thrown certainly if an IO exception occurs
	 */
	private ConnectionSettings connectionSettings(final Project project) throws SkillerException {

		if (project.isDirectAccess()) {
			ConnectionSettings settings = new ConnectionSettings();
			settings.setUrl(project.getUrlRepository());
			settings.setLogin(project.getUsername());
			settings.setPassword(project.getPassword());
			return settings;
		}
		
		if (project.isIndirectAccess()) {
			final String fileProperties = pathConnectionSettings + project.getFilename();
			File f = new File(fileProperties);
			if (!f.exists()) {
				throw new SkillerException(CODE_FILE_CONNECTION_SETTINGS_NOFOUND, 
						MessageFormat.format(MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND, fileProperties));
			}
			
			ConnectionSettings settings = new ConnectionSettings();
			try ( FileReader fr = new FileReader(f )) {
				settings = gson.fromJson(fr, settings.getClass());
				
				// We accept a global URL declared in the connection file, but its value will be overridden if the project get its own.
				if (project.getUrlRepository() != null) {
					settings.setUrl(project.getUrlRepository());
				}
			} catch (IOException  ioe) {
				throw new SkillerException(
						CODE_FILE_CONNECTION_SETTINGS_NOFOUND, 
						MessageFormat.format(MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND, fileProperties), 
						ioe);
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("GIT remote URL %s with user %s", settings.getUrl(), settings.getLogin()));
			}
			return settings;
		}
		
		throw new SkillerException(CODE_UNEXPECTED_VALUE_PARAMETER, "[Project : "+project.getName()+"] "+
				MessageFormat.format(MESSAGE_UNEXPECTED_VALUE_PARAMETER, "project.connection_Settings", project.getConnection_settings()));
	}

	@Override
	public boolean hasAvailableGeneration(Project project) throws Exception {
		boolean result = cacheDataHandler.hasCommitRepositoryAvailable(project);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("hasAvailableGeneration(%d)? : %s", project.getId(), result));
		}
		return result;
	}

}
