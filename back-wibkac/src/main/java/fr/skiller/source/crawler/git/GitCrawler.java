/**
 * 
 */
package fr.skiller.source.crawler.git;

import static fr.skiller.Error.CODE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static fr.skiller.Error.CODE_PARSING_SOURCE_CODE;
import static fr.skiller.Error.CODE_UNEXPECTED_VALUE_PARAMETER;
import static fr.skiller.Error.MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static fr.skiller.Error.MESSAGE_PARSING_SOURCE_CODE;
import static fr.skiller.Error.MESSAGE_UNEXPECTED_VALUE_PARAMETER;
import static fr.skiller.Error.SHOULD_NOT_PASS_HERE;
import static fr.skiller.Global.LN;
import static fr.skiller.Global.UNKNOWN;
import static fr.skiller.controller.ProjectController.DASHBOARD_GENERATION;
import static org.eclipse.jgit.diff.DiffEntry.DEV_NULL;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fr.skiller.Error;
import fr.skiller.Global;
import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.AsyncTask;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.bean.DataChartHandler;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectDashboardCustomizer;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.RiskProcessor;
import fr.skiller.bean.StaffHandler;
import fr.skiller.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import fr.skiller.controller.ProjectController.SettingsGeneration;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Library;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RepositoryAnalysis;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.internal.Staff;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitHistory;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.data.source.Contributor;
import fr.skiller.data.source.importance.AssessorImportance;
import fr.skiller.data.source.importance.FileSizeImportance;
import fr.skiller.data.source.importance.ImportanceCriteria;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.AbstractScannerDataGenerator;
import fr.skiller.source.crawler.RepoScanner;

/**
 * <p>
 * GIT implementation of a source code crawler
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service("GIT")
public class GitCrawler extends AbstractScannerDataGenerator implements RepoScanner {

	/**
	 * Patterns to take account, OR NOT, a file within the parsing process.<br/>
	 * For example, a file with the suffix .java is involved.
	 */
	@Value("${patternsInclusion}")
	private String patternsInclusion;

	
	/**
	 * Do we log each commit records in the logger ?
	 * When true, this settings will produce a large amount of data
	 */
	@Value("${logAllCommitRecords}")
	private boolean logAllCommitRecords;
	
	
	/**
	 * Markers of dependencies.<br/>
	 * These markers will be used to detect the possible presence of dependencies in the repository. 
	 */
	@Value("${dependenciesMarker}")
	private String dependenciesMarker;
	
	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;
	
	/**
	 * List of file patterns, to be included, or excluded, from the parsing process
	 */
	private List<Pattern> patternsInclusionList;

	/**
	 * The logger for the GitScanner.
	 */
	final Logger logger = LoggerFactory.getLogger(GitCrawler.class.getCanonicalName());

	/**
	 * A tree representing a class like <code>fr.common.my-package.MyClass"</code>
	 * might create 3 nodes of <code>RiskChartData</code>.
	 * <ul>
	 * <li>one for <code><b>fr</b></code></li>
	 * <li>one for <code><b>common</b></code></li>
	 * <li>one for <code><b>my-package</b></code></li>
	 * </ul>
	 * <b>BURT</b> Possibly, there is no source files present in
	 * <code><b>fr</b></code>. So instead of keeping 2 levels of hierarchy (with an
	 * empty one), it would be easier to aggregate the 2 directories into the
	 * resulting one : <code>fr.my-package</code>
	 * </p>
	 */
	@Value("${collapseEmptyDirectory}")
	private boolean collapseEmptyDirectory;

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
	 * Path access to retrieve the properties file for a given project By default,
	 * this file will be located in {@code /src/main/resources/repository-settings/}
	 */
	@Value("${versionControl.ConnectionSettings}")
	private String pathConnectionSettings;

	// Should the slices without source be average to the value of their children,
	// or stayed in the void color.
	@Value("${Sunburst.fillTheHoles}")
	private boolean fillTheHoles;

	@Autowired
	DataChartHandler dataChartHandler;

	@Autowired
	DataSaver dataSaver;

	/**
	 * Filter in charge to filter some debugging information.<br/>
	 * This string is updated with the system property <code>crawler.filter.debug</code>.<br/>
	 */
	String crawlerFilterDebug;
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new Gson();

	/**
	 * GitScanner constructor.
	 */
	public GitCrawler() {
		// This constructor is an empty one.
	}

	@PostConstruct
	public void init() {

		patternsInclusionList = Arrays.asList(patternsInclusion.split(";")).stream().map(Pattern::compile)
				.collect(Collectors.toList());

		if (logger.isDebugEnabled()) {
			logger.debug("Pattern INCLUSION loaded from the file application.properties : ");
			patternsInclusionList.stream().forEach(p -> logger.debug(p.pattern()));
		}

		// We "Spring-way" injected staff manager handle into the super class.
		super.parentStaffHandler = staffHandler;
		super.parentProjectHandler = projectHandler;
		
		
		 this.crawlerFilterDebug = System.getProperty("crawler.filter.debug");
		 
		 if ( (logger.isDebugEnabled()) && (this.crawlerFilterDebug != null) ) {
			 logger.debug(String.format("Debugging filter %s", this.crawlerFilterDebug));
		 }
	}

	@Override
	public void clone(final Project project, ConnectionSettings settings)
			throws IOException, GitAPIException, SkillerException {

		// Will we execute a git.clone() or a git.pull().
		// if TRUE, this will be a clone
		boolean execClone;

		Path path;
		if (project.getLocationRepository() == null) {
			path = createDirectoryAsCloneDestination(project, settings);
			execClone = true;
		} else {
			path = Paths.get(project.getLocationRepository());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Pulling the repository %s inside the PREVIOUS path %s", settings.getUrl(),
						path.toAbsolutePath()));
			}

			// If the directory has been cleanup, we create a new one.
			if (!path.toFile().exists()) {
				path = createDirectoryAsCloneDestination(project, settings);
				execClone = true;
			} else {
				execClone = false;
			}
		}
		if (execClone) {
			Git.cloneRepository().setDirectory(path.toAbsolutePath().toFile()).setURI(settings.getUrl())
					.setCredentialsProvider(
							new UsernamePasswordCredentialsProvider(settings.getLogin(), settings.getPassword()))
					.setProgressMonitor(new CustomProgressMonitor()).call();
			if (logger.isDebugEnabled()) {
				logger.debug("Clone done & succcessful !");
			}
		} else {
			
			try (Git git = Git.open(Paths.get(getLocalDotGitFile(project)).toFile())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Pull?");
				}
				git.pull().setProgressMonitor(new CustomProgressMonitor());
			} 
			if (logger.isDebugEnabled()) {
				logger.debug("Pull done & succcessful !");
			}
		}

		// Saving the local repository location
		projectHandler.saveLocationRepository(project.getId(), path.toFile().getCanonicalPath());

	}

	/**
	 * Create a directory in the temp directory as a destination of the clone
	 * process.
	 * 
	 * @param project  the actual project
	 * @param settings the connection settings <i>(these settings are given for
	 *                 trace only support)</i>
	 * @return the resulting path
	 * @throws IOException an IO oops ! occurs. Too bad!
	 */
	private Path createDirectoryAsCloneDestination(Project project, ConnectionSettings settings) throws IOException {
		// Creating a temporary local path where the remote project repository will be
		// cloned.
		Path path = Files.createTempDirectory("skiller_jgit_" + project.getName() + "_");
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Cloning the repository %s inside the CREATED path %s", settings.getUrl(),
					path.toAbsolutePath()));
		}
		return path;
	}

	@Override
	public RepositoryAnalysis loadChanges(Project project, Repository repository) throws SkillerException {

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);

		List<RevCommit> allCommits = new ArrayList<>();
		
		try (Git git = new Git(repository)) {

			List<String> tagOrBranchNames = git.branchList().call().stream()
				.map(Ref::getName).collect(Collectors.toList());
			
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Branch or tag names analyzed for %s", repository.getDirectory()));
				tagOrBranchNames.stream().forEach(logger::debug);
			}
			
			for ( String tagOrBranchName : tagOrBranchNames) {
				for (RevCommit commit : git.log().add(repository.resolve(tagOrBranchName)).call()) {
					allCommits.add(commit);
				}
			}
		} catch (final IOException | GitAPIException e) {
			throw new SkillerException(CODE_PARSING_SOURCE_CODE, MESSAGE_PARSING_SOURCE_CODE, e);
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Retrieving %d on the repository %s", allCommits.size(),
					repository.getDirectory().getAbsoluteFile()));
		}

		final Comparator<RevCommit> dateCommitComparator = (RevCommit revCommit1, RevCommit revCommit2) -> {
			return revCommit1.getCommitterIdent().getWhen().compareTo(revCommit2.getCommitterIdent().getWhen());
		};

		List<RevCommit> allDateAscendingCommits = allCommits.stream().sorted(dateCommitComparator)
				.collect(Collectors.toList());

		ObjectId previous = null;

		for (RevCommit commit : allDateAscendingCommits) {

			if (this.logAllCommitRecords) {
				log(commit); 
			}
			
			if (previous == null) {
				previous = commit.getTree().getId();
			} else {
				ObjectId current = commit.getTree().getId();
				diff(repository, analysis, commit, previous, current);
				previous = current;
			}

			if ((logger.isDebugEnabled()) && (commit.getParentCount() >= 2)) {
				logger.debug(String.format("commit '%s' with merge ?", commit.getShortMessage()));
			}
		}
		return analysis;
	}

	private void diff(Repository repository, RepositoryAnalysis analysis, RevCommit commit, ObjectId prevObjectId,
			ObjectId curObjectId) throws SkillerException {

		final RenameDetector renameDetector = new RenameDetector(repository);

		try (ObjectReader reader = repository.newObjectReader()) {
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, prevObjectId);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, curObjectId);

			// finally get the list of changed files
			try (Git git = new Git(repository)) {
				List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();

				// Might be a rename with specific action
				if (isRenamePossible(diffs)) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								String.format("commit '%s' is treated as a 'rename' commit", commit.getShortMessage()));
					}
					renameDetector.addAll(diffs);
					List<DiffEntry> files = renameDetector.compute();
					processDiffEntries(analysis, commit, files);
				} else {
					processDiffEntries(analysis, commit, diffs);
				}
			}
		} catch (final Exception e) {
			throw new SkillerException(CODE_PARSING_SOURCE_CODE, MESSAGE_PARSING_SOURCE_CODE, e);
		}
	}

	/**
	 * Take in account the list of files impacted by a commit
	 * 
	 * @param gitChanges the complete list of changes detected in the studying
	 *                   repository
	 * @param commit     the actual commit evaluated
	 * @param diffs      the list of difference between this current commit and the
	 *                   previous one
	 */
	private void processDiffEntries(	
				RepositoryAnalysis analysis, 
				RevCommit commit, 
				List<DiffEntry> diffs) {

		List<SCMChange> gitChanges = analysis.getChanges();
		
		for (DiffEntry de : diffs) {
						
			if (logger.isDebugEnabled() &&
				(this.crawlerFilterDebug != null) &&
					(	"*".contentEquals(this.crawlerFilterDebug)
						|| 	( (de.getNewPath() != null) && (de.getNewPath().contains(this.crawlerFilterDebug)))
						|| 	( (de.getOldPath() != null) && (de.getOldPath().contains(this.crawlerFilterDebug))))) {
				logger.debug(String.format("%s %s %s", de.getChangeType(), de.getOldPath(), de.getNewPath()));
			}
			
			switch (de.getChangeType()) {
			case RENAME:
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s is renammed into %s", de.getOldPath(), de.getNewPath()));
				}
				renameFilePath(de.getNewPath(), de.getOldPath(), gitChanges);
				break;
			case DELETE:
				if (DEV_NULL.equals(de.getNewPath())) {
					removeFilePath(de.getOldPath(), gitChanges);
				} else {
					throw new SkillerRuntimeException(String.format("%s REQUIRES TO BE NULL", de.getNewPath()));
				}
				break;
			case COPY:
				//
				// We assume that the COPY change is connected to MERGE operations
				// Therefore the author of the merge is useless for the scope of this
				// application
				// This committer did not touch the content of the source
				//
				break;
			case MODIFY: //NOSONAR
				// A MODIFY tag might be an ADD. 
				if (gitChanges.parallelStream()
						.map(SCMChange::getPath)
						.anyMatch(s -> s.equals(de.getNewPath()))) { 
					analysis.getPathsModified().add(de.getNewPath());
				}
			case ADD: 
				PersonIdent author = commit.getAuthorIdent();
				SCMChange change = new SCMChange(commit.getId().toString(), de.getNewPath(),
						author.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), author.getName(),
						author.getEmailAddress());
				gitChanges.add(change);
				break;
			default:
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Unexpected type of change %s %s %s", de.getChangeType(),
							de.getOldPath(), de.getNewPath()));
				}
				break;
			}
		}
	}

	/**
	 * <p>
	 * The rename of file is not correctly treated by the {@code git.diff()}.<br/>
	 * This function detects the possibility of a rename, and jump into another Diff
	 * process more time consuming.
	 * </p>
	 * 
	 * @param diffs
	 * @return
	 */
	public boolean isRenamePossible(List<DiffEntry> diffs) {
		boolean rename = diffs.stream().map(DiffEntry::getChangeType).anyMatch(ChangeType.ADD::equals);
		if (rename) {
			return diffs.stream().map(DiffEntry::getChangeType).anyMatch(ChangeType.DELETE::equals);
		}
		return rename;
	}

	@Override
	public void finalizeListChanges(String sourceLocation, RepositoryAnalysis analysis) throws IOException {

		List<SCMChange> changes = analysis.getChanges();
		
		if (logger.isDebugEnabled()) {
			logger.debug(
					String.format("Finalizing the changes collection with the repository location %s", sourceLocation));
		}
		
		/*
		 * For DEBUG purpose we make a redundant test on the list of changes 
		 * Are all files in the repository referenced in the list of change ? 
		 * They should be all present.
		 */
		if (logger.isDebugEnabled()) {
			logger.debug("List of ghost files");
			List<String> content;
			try (Stream<Path> stream = Files.find(Paths.get(sourceLocation), 999, (p, bfa) -> bfa.isRegularFile())) {
				content = stream.map(Path::toString).map(s -> s.substring(sourceLocation.length()-1))
						.collect(Collectors.toList());

				content.stream().filter(s -> changes.stream().map(SCMChange::getPath).noneMatch(s::equals))
						.forEach(logger::debug);
			}
		}

		Iterator<SCMChange> iter = changes.iterator();
		while (iter.hasNext()) {
			SCMChange change = iter.next();
			// File does not exist anymore on the repository
			File f = Paths.get(sourceLocation + change.getPath()).toFile();
			if (!f.exists()) {
				iter.remove();
			} else {
				// Hidden files, mainly internal GIT files are removed.
				if (f.isHidden()) {
					iter.remove();
				}
			}
		}
	}

	@Override
	public void filterEligible(RepositoryAnalysis analysis) {
		
		Iterator<SCMChange> iter = analysis.getChanges().iterator();
		while (iter.hasNext()) {
			SCMChange change = iter.next();
			if (!isElligible(change.getPath())) {
				iter.remove();
			}
		}
	}

	@Override
	public void cleanupPaths(RepositoryAnalysis analysis) {
		analysis.getChanges().stream().forEach(change -> change.setPath(projectDashboardCustomizer.cleanupPath(change.getPath())));
	}

	@Override
	public void removeNonRelevantDirectories(Project project, RepositoryAnalysis analysis) {
		
		project.getLibraries().stream().forEach(dep ->
			analysis.getChanges().removeIf(change -> change.getPath().contains(dep.getExclusionDirectory()))
		);
/*
		analysis.getChanges().removeIf(change -> change.getPath().contains("docs/")); 
		analysis.getChanges().removeIf(change -> change.getPath().contains("com/microsoft/schemas")); 
		analysis.getChanges().removeIf(change -> change.getPath().contains("vegeo-ihm-testing/")); 
		analysis.getChanges().removeIf(change -> change.getPath().contains("maquettes")); 
		analysis.getChanges().removeIf(change -> change.getPath().contains("perf/")); 
		analysis.getChanges().removeIf(change -> change.getPath().contains("env-dev/config")); 
*/
	}
	
	/**
	 * <p>
	 * Take in account the fact that a file has been renamed.<br/>
	 * All records with the old path will be renamed to the new one.
	 * </p>
	 * 
	 * @param newPath    the new file path
	 * @param oldPath    the old file path
	 * @param gitChanges the changes collection
	 */
	private void renameFilePath(String newPath, String oldPath, List<SCMChange> gitChanges) {
		gitChanges.stream().filter(change -> oldPath.equals(change.getPath()))
				.forEach(change -> change.setPath(newPath));
	}

	/**
	 * <p>
	 * Remove a path from the collection of changes.
	 * </p>
	 * 
	 * @param removedPath the path removed from the file system
	 * @param gitChanges  the changes collection
	 */
	private void removeFilePath(String removedPath, List<SCMChange> gitChanges) {
		Iterator<SCMChange> iter = gitChanges.iterator();
		while (iter.hasNext()) {
			SCMChange change = iter.next();
			if (change.getPath().equals(removedPath)) {
				iter.remove();
			}
		}
	}

	@Override
	public CommitRepository parseRepository(final Project project, final ConnectionSettings settings)
			throws IOException, SkillerException {

		// Test if this repository is available in cache.
		// If this repository exists, return it immediately.
		if (cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Using cache file for project %s", project.getName()));
			}

			CommitRepository repository = cacheDataHandler.getRepository(project);

			// Since the last parsing of the repository, some developers might have been
			// declared and are responding now to unknown pseudos.
			// We cleanup the set.
			repository.setUnknownContributors(repository.unknownContributors().stream()
					.filter(pseudo -> (staffHandler.lookup(pseudo) == null)).collect(Collectors.toSet()));

			return repository;
		}

		if (project.getLocationRepository() == null) {
			throw new SkillerException(Error.CODE_REPO_MUST_BE_ALREADY_CLONED,
					Error.MESSAGE_REPO_MUST_BE_ALREADY_CLONED);
		}

		// Unknown committers
		final Set<String> unknown;

		// Repository
		final CommitRepository repositoryOfCommit;
	
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repo = builder.setGitDir(new File(getLocalDotGitFile(project)))
				.readEnvironment()
				.findGitDir()
				.build();
		
		/**
		 * We load all raw changes declared in the given repository
		 */
		RepositoryAnalysis analysis = this.loadChanges(project, repo);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("loadChanges (%s) returns %d entries", project.getName(), analysis.sizeChanges()));
		}
		
		/**
		 * We save the directories of the repository.
		 */
		dataSaver.saveRepositoryDirectories(project, analysis.getChanges());
		
		/**
		 * For test and debug purpose, we save the changes file on the file system.
		 */
		dataSaver.saveChanges(project, analysis.getChanges());

		/**
		 * We finalize & cleanup the content of the collection
		 */
		this.finalizeListChanges(project.getLocationRepository() + "/", analysis);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("finalizeListChanges (%s) returns %d entries", project.getName(),
					analysis.sizeChanges()));
		}

		/**
		 * We filter the collection on eligible entries (.java; .js...)
		 */
		this.filterEligible(analysis);
		if (logger.isDebugEnabled()) {
			logger.debug(
					String.format("filterEligible (%s) returns %d entries", project.getName(), analysis.sizeChanges()));
		}

		// Updating the importance
		this.updateImportance(project, analysis);
		
		// Retrieve directories candidate for being exclude from the analysis 
		// The resulting set contains only source files without a commit history of modification.
		// They have only be added.
		analysis.extractCandidateForDependencies();
	
		//
		// We filter the candidates with dependencies marker such as "jquery"
		// The analysis container has a set of path (pathsCandidate) 
		// which might be contain file as /toto/titi/jquery/src/jquery-internal.js
		//
		selectPathDependencies (analysis, dependenciesMarker());
		
		//
		// We retrieve the root paths of all libraries present in the project (if any) 
		// The resulting list is saved in the project object.
		//
		this.retrieveRootPath(analysis);
		
		/**
		 * We remove the non relevant directories from the crawl
		 */
		this.removeNonRelevantDirectories(project, analysis);
		
		/**
		 * We cleanup the pathnames each location (e.g. "src/main/java" is removed)
		 */
		this.cleanupPaths(analysis);

		
		repositoryOfCommit = new BasicCommitRepository();

		/**
		 * Set of unknown contributors who have work on this repository.
		 */
		unknown = repositoryOfCommit.unknownContributors();

		/**
		 * We update the staff identifier on each change entry.
		 */
		this.updateStaff(project, analysis, unknown);

		/**
		 * Retrieve the list of contributors involved in the project.
		 */
		List<Contributor> contributors = this.gatherContributors(analysis);

		if (logger.isDebugEnabled()) {
			logger.debug(
					"Taking account of retrieved contributors from the repository into the project list of participants");
		}

		/**
		 * Update the staff team missions with the contributors.
		 */
		staffHandler.involve(project, contributors);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%d contributors retrieved : ", contributors.size()));
			contributors.stream().forEach(contributor -> {
				String fullname = staffHandler.getFullname(contributor.getIdStaff());
				logger.debug(String.format("%d %s", contributor.getIdStaff(),
						(fullname != null) ? fullname : "unknown"));
			});
		}

		// Displaying results...
		if (logger.isInfoEnabled() && (!unknown.isEmpty())) {
			logger.info(String.format("Unknown contributors for project %s", analysis.getProject().getName()));
			unknown.stream().forEach(logger::info);
		}

		analysis.getChanges().stream()
				.forEach(change -> repositoryOfCommit.addCommit(change.getPath(),
						change.isIdentified() ? change.getIdStaff() : UNKNOWN, change.getDateCommit(),
						change.getImportance()));


		// Saving the repository into the cache
		cacheDataHandler.saveRepository(project, repositoryOfCommit);

		return repositoryOfCommit;
	}

	/**
	 * Log a commit record in debug mode.
	 * 
	 * @param commit the given commit
	 */

	private void log(RevCommit commit) {
		if (logger.isDebugEnabled()) {
			PersonIdent author = commit.getAuthorIdent();
			StringBuilder sb = new StringBuilder();
			sb.append(Global.LN).append("shortMessage : " + commit.getShortMessage()).append(Global.LN)
					.append("id : " + author.getEmailAddress()).append(Global.LN).append("date : " + author.getWhen())
					.append(Global.LN).append("authorIdent.name : " + author.getName()).append(Global.LN)
					.append(Global.LN);
			logger.debug(sb.toString());
		}

	}

	/**
	 * Check if the path is an eligible source for the activity dashboard.
	 * 
	 * @param path
	 * @return True if the path should be included
	 */
	boolean isElligible(final String path) {

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
	public List<Contributor> gatherContributors(RepositoryAnalysis analysis) {
		Set<Integer> idContributors = new HashSet<>();
		analysis.getChanges().stream().map(SCMChange::getIdStaff).filter(idStaff -> idStaff != 0).distinct()
				.forEach(idContributors::add);

		List<Contributor> contributors = new ArrayList<>();
		for (int idStaff : idContributors) {

			// The first commit submitted by this staff member
			LocalDate firstCommit = analysis.getChanges().stream().filter(change -> idStaff == change.getIdStaff())
					.map(SCMChange::getDateCommit).min(Comparator.comparing(LocalDate::toEpochDay))
					.orElseThrow(() -> new SkillerRuntimeException(SHOULD_NOT_PASS_HERE));

			// The last commit submitted by this staff member
			LocalDate lastCommit = analysis.getChanges().stream().filter(change -> idStaff == change.getIdStaff())
					.map(SCMChange::getDateCommit).max(Comparator.comparing(LocalDate::toEpochDay))
					.orElseThrow(() -> new SkillerRuntimeException(SHOULD_NOT_PASS_HERE));

			long numberOfCommits = analysis.getChanges().stream().filter(change -> idStaff == change.getIdStaff())
					.map(SCMChange::getCommitId).distinct().count();

			long numberOfFiles = analysis.getChanges().stream().filter(change -> idStaff == change.getIdStaff())
					.map(SCMChange::getPath).distinct().count();

			contributors
					.add(new Contributor(idStaff, firstCommit, lastCommit, (int) numberOfCommits, (int) numberOfFiles));
		}

		return contributors;
	}

	@Override
	@Async
	public RiskDashboard generateAsync(final Project project, final SettingsGeneration settings)
			throws SkillerException, GitAPIException, IOException {
		try {
			tasks.addTask(DASHBOARD_GENERATION, "project", project.getId());
			return generate(project, settings);
		} finally {
			tasks.removeTask(DASHBOARD_GENERATION, "project", project.getId());
		}
	}

	@Override
	public RiskDashboard generate(final Project project, final SettingsGeneration cfgGeneration)
			throws IOException, SkillerException, GitAPIException {

		final ConnectionSettings settings = connectionSettings(project);

		if (!cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			this.clone(project, settings);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("The project %s is cloned into a temporay directory", project.getName()));
			}
		}

		// If a cache is detected and available for this project, it will be returned by
		// this method.
		// This variable is not final. Might be overridden by the filtering operation
		// (date of staff member filtering)
		CommitRepository repo = this.parseRepository(project, settings);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("The repository has been parsed. It contains %d records in the repository", repo.size()));
		}

		// Does the process requires a personalization ?
		// e.g. filtering on a staff identifier or starting the history crawl from a
		// starting date
		if (cfgGeneration.requiresPersonalization()) {
			repo = personalizeRepo(repo, cfgGeneration);
		}

		RiskDashboard data = this.aggregateDashboard(project, repo);

		// We collapse empty directory inside their first sub-directory
		// the node com & the node google will become one single node com/google
		if (collapseEmptyDirectory) {
			if (logger.isDebugEnabled()) {
				logger.debug("Aggregating empty directories in the chart");
			}
			dataChartHandler.aggregateDataChart(data.riskChartData);
		}

		// Evaluate the risk for each directory, and sub-directory, in the repository.
		final List<StatActivity> statsCommit = new ArrayList<>();
		this.riskSurveyor.evaluateTheRisk(repo, data.riskChartData, statsCommit);

		// Fill the holes for directories without source files, and therefore without
		// risk level measured.
		// We do not fill the holes if the chart is filtered on a specific developer
		if (fillTheHoles && cfgGeneration.getIdStaffSelected() == 0) {
			this.riskSurveyor.meanTheRisk(data.riskChartData);
		}

		// Evaluate the preview display for each slice of the sunburst chart.
		this.riskSurveyor.setPreviewSettings(data.riskChartData);

		/**
		 * Evaluate and save the level of risk for the whole project.
		 * This estimation will affect the color of the dot-risk in the form project.
		 */
		this.riskSurveyor.evaluateProjectRisk(project, data.riskChartData);

		// We send back to new risk level to the front-end application.
		data.setProjectRiskLevel(project.getRisk());
		
		if (logger.isDebugEnabled()) {
			if ((data.undefinedContributors != null) && (!data.undefinedContributors.isEmpty())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unknown contributors detected during the dashboard generation").append(LN);
				data.undefinedContributors.stream().forEach(ukwn -> sb.append(ukwn.getPseudo()).append(LN));
				logger.debug(sb.toString());
			}

			if ((project.getGhosts() != null) && (!project.getGhosts().isEmpty())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Registered ghosts in the project record :").append(LN);
				project.getGhosts().stream().forEach(g -> sb.append(g.getPseudo()).append(" : ").append(g.getIdStaff())
						.append("/").append(g.isTechnical()).append(LN));
				logger.debug(sb.toString());
			}
		}
		return data;
	}

	@Override
	public CommitRepository personalizeRepo(CommitRepository globalRepo, SettingsGeneration settings) {
		final LocalDate startingDate = new Date(settings.getStartingDate()).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();

		if (logger.isDebugEnabled()) {
			logger.debug(MessageFormat.format("Filtering the repositiory for id:{0}, and starting date:{1}",
					settings.getIdStaffSelected(), startingDate));
		}
		CommitRepository personalizedRepo = new BasicCommitRepository();
		for (CommitHistory commits : globalRepo.getRepository().values()) {
			commits.operations.stream().filter(
					it -> ((it.idStaff == settings.getIdStaffSelected()) || (settings.getIdStaffSelected() == 0)))
					.filter(it -> (it.getDateCommit()).isAfter(startingDate))
					.forEach(item -> personalizedRepo.addCommit(commits.sourcePath, item.idStaff, item.getDateCommit(),
							commits.getImportance()));
		}
		return personalizedRepo;
	}

	/**
	 * Load the connection settings for the given project.
	 * 
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
			try (FileReader fr = new FileReader(f)) {
				settings = gson.fromJson(fr, settings.getClass());

				// We accept a global URL declared in the connection file, but its value will be
				// overridden if the project get its own.
				if (project.getUrlRepository() != null) {
					settings.setUrl(project.getUrlRepository());
				}
			} catch (IOException ioe) {
				throw new SkillerException(CODE_FILE_CONNECTION_SETTINGS_NOFOUND,
						MessageFormat.format(MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND, fileProperties), ioe);
			}

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("GIT remote URL %s with user %s", settings.getUrl(), settings.getLogin()));
			}
			return settings;
		}

		throw new SkillerException(CODE_UNEXPECTED_VALUE_PARAMETER,
				"[Project : " + project.getName() + "] " + MessageFormat.format(MESSAGE_UNEXPECTED_VALUE_PARAMETER,
						"project.connection_Settings", project.getConnectionSettings()));
	}

	@Override
	public boolean hasAvailableGeneration(Project project) throws IOException {
		boolean result = cacheDataHandler.hasCommitRepositoryAvailable(project);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("hasAvailableGeneration(%d)? : %s", project.getId(), result));
		}
		return result;
	}

	@Override
	public void updateStaff(Project project, RepositoryAnalysis analysis, Set<String> unknownContributors) {

		List<String> authors = analysis.getChanges().stream().filter(SCMChange::isAuthorIdentified).map(SCMChange::getAuthorName)
				.distinct().collect(Collectors.toList());

		authors.forEach(

				author -> {

					final Staff staff = staffHandler.lookup(author);
					if ((staff == null) && (author.split(" ").length == 1)) {
						Optional<Ghost> oGhost = project.getGhosts().stream().filter(g -> (!g.isTechnical()))
								.filter(g -> author.equalsIgnoreCase(g.getPseudo())).findFirst();
						if (oGhost.isPresent()) {
							Ghost selectedGhost = oGhost.get();
							int ghostIdentified = staffHandler.getStaff().get(selectedGhost.getIdStaff()).getIdStaff();
							//
							// We update the staff collection !
							//
							analysis.getChanges().stream().filter(change -> author.equals(change.getAuthorName()))
									.forEach(change -> change.setIdStaff(ghostIdentified));
							//
							// We find a staff entry, but we keep the pseudo in the unknowns list
							// in order to be able to change the relation between the ghost & the staff
							// membre
							// in the dedicated dialog box
							//
							unknownContributors.add(author);
						}
					}
					if (staff == null) {
						if (logger.isDebugEnabled()) {
							logger.debug(String.format("No staff found for the criteria %s", author));
						}
						unknownContributors.add(author);
					} else {
						//
						// We update the staff collection !
						//
						analysis.getChanges().stream().filter(change -> author.equals(change.getAuthorName()))
								.forEach(change -> change.setIdStaff(staff.getIdStaff()));
					}

				});

	}

	@Override
	public void updateImportance(Project project, RepositoryAnalysis analysis) throws SkillerException {
		
		final AssessorImportance assessor = new FileSizeImportance();

		final Comparator<SCMChange> filePathComparator = (SCMChange change1, SCMChange change2) -> {
			return change1.getPath().compareTo(change2.getPath());
		};
		
		List<SCMChange> sortedChanges = analysis.getChanges().stream()
					.sorted(filePathComparator)
					.collect(Collectors.toList());

		String pathMem = "";
		long importanceMem = -1;
		for (SCMChange change : sortedChanges) {
			
			if (!pathMem.equals(change.getPath())) {
				importanceMem = assessor.getImportance(project, change, ImportanceCriteria.FILE_SIZE);
				pathMem = change.getPath();
			}
			change.setImportance(importanceMem);
		}
	}
	
	/**
	 * @return the list of markers of dependencies.
	 */
	@Override
	public List<String> dependenciesMarker() {
		return Arrays.asList(dependenciesMarker.split(";"));		
	}
	
	@Override
	public void selectPathDependencies (
			RepositoryAnalysis analysis, List<String> dependenciesMarker) {

		for (String marker : dependenciesMarker) {
			for (String pathAdded : analysis.getPathsAdded()) {
				int posMarker = pathAdded.indexOf(marker);
				if (posMarker != -1) {
					
					final String pathDependency;
					if (pathAdded.charAt(posMarker + marker.length() - 1) == File.separatorChar) {
						pathDependency = pathAdded.substring(0, posMarker + marker.length() -	 1);
					} else {
						pathDependency = pathAdded.substring(0, posMarker + marker.length());
					}
					
					if (!analysis.getPathsCandidate().contains(pathDependency)) {
						analysis.getPathsCandidate().add(pathDependency);
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Dependencies path");
			analysis.getPathsCandidate().stream().forEach(logger::debug); 
		}
	}

	@Override
	public void retrieveRootPath(RepositoryAnalysis analysis) throws IOException {
		
		if ( logger.isDebugEnabled() && (this.crawlerFilterDebug != null)) {
			logger.debug("Added files only list");
			if ("*".equals(this.crawlerFilterDebug)) {
				analysis.getPathsAdded().stream().forEach(logger::debug);
			} else {
				analysis
				.getPathsAdded()
				.stream()
				.filter(p -> p.indexOf(this.crawlerFilterDebug) != -1)
				.forEach(logger::debug);
			}
		}
		
		for (String pathname : analysis.getPathsCandidate()) {
			
			String absolutePath = analysis.getProject().getLocationRepository() + File.separatorChar + pathname;
			if (!Files.exists(Paths.get(absolutePath))) {
				throw new SkillerRuntimeException(String.format("WTF %s does not exist anymore !", absolutePath));
			}

			if (containFilesOnlyAdded(analysis, new File(absolutePath))) {
				analysis.getProject().add( new Library(pathname, Global.LIBRARY_DETECTED));
				if (logger.isDebugEnabled()) {
					logger.debug (String.format("Handling %s as a dependency", pathname));
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Libraries detected in the repository :");
			analysis.getProject().getLibraries()
				.stream()
				.filter(lib -> lib.getType() == Global.LIBRARY_DETECTED)
				.map(Library::getExclusionDirectory)
				.forEach(logger::info);
		}
	}
		
	private boolean containFilesOnlyAdded(final RepositoryAnalysis analysis, File dependency)  throws IOException {
		
		File[] children = dependency.listFiles();
		
		if (logger.isDebugEnabled()) {
			logger.debug (String.format(" Project local repository %s",  analysis.getProject().getLocationRepository()));
		}
		
		for (File child : children) {
			if (logger.isDebugEnabled()) {
				logger.debug (String.format("Examining %s", child.getCanonicalPath()));
			}
			
			if (child.isDirectory()) {
				return containFilesOnlyAdded(analysis, child);
			}
			
			int lengthLocationReposition = analysis.getProject().getLocationRepository().length();
			String testing = child.getCanonicalPath().substring(lengthLocationReposition+1);
			
			//  First, the file has to be captured by the crawler.
			// We avoid all files which do not match the filter criteria (.java, .js, ...)
			if (!analysis.isCatchedFile(testing)) {
				continue;
			}
			
			if (!analysis.getPathsAdded()
					.contains(testing)) {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s has evicted the dependency %s", 
							child.getCanonicalPath(), dependency.getCanonicalPath()));
				}
				return false;
			}
		}
		return true;
	}
	/**
	 * @return the location repository entry point <br/><i>i.e. the absolute path to the .git file.</i>
	 */
	private String getLocalDotGitFile(Project project) {
		return ( project.getLocationRepository().charAt(project.getLocationRepository().length()-1) == File.pathSeparatorChar) 
			? project.getLocationRepository() + ".git" : project.getLocationRepository() + "/.git";
	}

}
