/**
 * 
 */
package com.fitzhi.source.crawler.git;

import static com.fitzhi.Error.CODE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static com.fitzhi.Error.CODE_PARSING_SOURCE_CODE;
import static com.fitzhi.Error.CODE_UNEXPECTED_VALUE_PARAMETER;
import static com.fitzhi.Error.MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static com.fitzhi.Error.MESSAGE_PARSING_SOURCE_CODE;
import static com.fitzhi.Error.MESSAGE_UNEXPECTED_VALUE_PARAMETER;
import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.INTERNAL_FILE_SEPARATORCHAR;
import static com.fitzhi.Global.LN;
import static com.fitzhi.Global.PROJECT;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fitzhi.Error;
import com.fitzhi.Global;
import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.data.source.importance.AssessorImportance;
import com.fitzhi.data.source.importance.FileSizeImportance;
import com.fitzhi.data.source.importance.ImportanceCriteria;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.RepoScanner;
import com.fitzhi.source.crawler.impl.AbstractScannerDataGenerator;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * GIT implementation of a source code crawler
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service("GIT")
@Slf4j
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
	 * <p>
	 * This {@code boolean} is setting the fact that the eligibility validation is made 
	 * <u>prior</u> to the creation of the repository-chart data file, or <u>after</u>.<br/>
	 * Techxhì is storing intermediate data on a file named "{@link Project#getId()}-{@link Project#getName()}.json".
	 * </p>
	 * <p>
	 * The consequence of this settings is :
	 * <ul>
	 * <li>
	 * if {@code true}, the full global generation will be faster, because data are already filtered. 
	 * But, if you want to change the pattern of inclusion, on the fly, you will have to regenerate the full chart.
	 * </li>
	 * <li>
	 * If {@code false}, the crawler catch all files in the repository (e.g. the whole repository), 
	 * before working, or filtering on it.
	 * The generation will be slower, but the chart will be faster to filter.
	 * </li>
	 * </ul>
	 * <font color="darkGreen"><b>Our recommendation is to setup this property to {@code true}.</b></font>
	 * </p>
	 */
	@Value("${prefilterEligibility}")
	private boolean prefilterEligibility;
	
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
	DataHandler dataSaver;

	/**
	 * Filter in charge to filter some debugging information.<br/>
	 * This string is updated with the system property <code>crawler.filter.debug</code>.<br/>
	 */
	String crawlerFilterDebug;
	
	/**
	 * Bean in charge of analyzing the repository to retrieve the ecosystem.
	 */
	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
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

		if (log.isDebugEnabled()) {
			log.debug("Pattern INCLUSION loaded from the file application.properties : ");
			patternsInclusionList.stream().forEach(p -> log.debug(p.pattern()));
		}

		// We "Spring-way" injected staff manager handle into the super class.
		super.parentStaffHandler = staffHandler;
		super.parentProjectHandler = projectHandler;
		
		
		 this.crawlerFilterDebug = System.getProperty("crawler.filter.debug");
		 
		 if ( (log.isDebugEnabled()) && (this.crawlerFilterDebug != null) ) {
			 log.debug(String.format("Debugging filter %s", this.crawlerFilterDebug));
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
			if (log.isDebugEnabled()) {
				log.debug(String.format("Pulling the repository %s inside the PREVIOUS path %s", settings.getUrl(),
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
			if (log.isDebugEnabled()) {
				log.debug("Clone done & succcessful !");
			}
		} else {
			
			try (Git git = Git.open(Paths.get(getLocalDotGitFile(project)).toFile())) {
				if (log.isDebugEnabled()) {
					log.debug("Pull?");
				}
				git.pull().setProgressMonitor(new CustomProgressMonitor());
			} 
			if (log.isDebugEnabled()) {
				log.debug("Pull done & succcessful !");
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
		if (log.isDebugEnabled()) {
			log.debug(String.format("Cloning the repository %s inside the CREATED path %s", settings.getUrl(),
					path.toAbsolutePath()));
		}
		return path;
	}

	@Override
	public RepositoryAnalysis loadChanges(Project project, Repository repository) throws SkillerException {

		List<RevCommit> allCommits = new ArrayList<>();
		
		try (Git git = new Git(repository)) {

			List<String> tagOrBranchNames = git.branchList().call().stream()
				.map(Ref::getName).collect(Collectors.toList());
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("Branch or tag names analyzed for %s", repository.getDirectory()));
				tagOrBranchNames.stream().forEach(log::debug);
			}
			
			int nbCommit = 0;
			int nbTotCommit = 0;
			for ( String tagOrBranchName : tagOrBranchNames) {
				for (RevCommit commit : git.log().add(repository.resolve(tagOrBranchName)).call()) {
					allCommits.add(commit);
					if (++nbCommit == 1000) {
						nbTotCommit += nbCommit;
						this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), nbTotCommit + " commits on-boarded!");
						nbCommit = 0;
					}
				}
			}
			
		} catch (final IOException | GitAPIException e) {
			throw new SkillerException(CODE_PARSING_SOURCE_CODE, MESSAGE_PARSING_SOURCE_CODE, e);
		}

		if (log.isInfoEnabled()) {
			log.info(String.format("Retrieving %d on the repository %s", allCommits.size(),
					repository.getDirectory().getAbsoluteFile()));
		}

		final Comparator<RevCommit> dateCommitComparator = (RevCommit revCommit1, RevCommit revCommit2) -> {
			return revCommit1.getCommitterIdent().getWhen().compareTo(revCommit2.getCommitterIdent().getWhen());
		};

		List<RevCommit> allDateAscendingCommits = allCommits.stream().sorted(dateCommitComparator)
				.collect(Collectors.toList());

		ObjectId previous = null;

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);

		//
		// We initialize the parser speed-meter
		//
		ParserVelocity velocity = new ParserVelocity(project.getId(), this.tasks);
		
		for (RevCommit commit : allDateAscendingCommits) {

			if (this.logAllCommitRecords) {
				log(commit); 
			}
			
			if (previous == null) {
				previous = commit.getTree().getId();
			} else {
				ObjectId current = commit.getTree().getId();
				buildRepositoryAnalysis(repository, analysis, commit, previous, current, velocity);
				previous = current;
			}

			if ((log.isDebugEnabled()) && (commit.getParentCount() >= 2)) {
				log.debug(String.format("commit '%s' with merge ?", commit.getShortMessage()));
			}
		}
		
		velocity.finalize();
		return analysis;
	}

	/**
	 * <p>
	 * For a given revision, <br/>
	 * taking account of its files tree by comparison with the previous state of the repository.<br/>
	 * To fulfill that purpose, 2 files tree are passed to this method in order to detect 
	 * <ul>
	 * <li>the real functional implementation changes made by developers, which really matter</li>
	 * <li>or, the simple copy, delete or modify file path modifications</li>
	 * </ul>
	 * </p>
	 * @param repository a GIT repository
	 * @param analysis the repository analysis
	 * @param commit the commit revision examined 
	 * @param prevObjectId the <b>PREVIOUS</b> files tree involved 
	 * @param curObjectId the <b>CURRENT</b> files tree examined.
	 * @param velocity parser velocity to follow up in detail the performance of the application
	 * @throws SkillerException
	 */
	private void buildRepositoryAnalysis(Repository repository, RepositoryAnalysis analysis, RevCommit commit, ObjectId prevObjectId,
			ObjectId curObjectId, ParserVelocity velocity) throws SkillerException {

		final RenameDetector renameDetector = new RenameDetector(repository);

		try (ObjectReader reader = repository.newObjectReader()) {
			
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, prevObjectId);
			
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, curObjectId);

			//
			// finally get the list of changed files
			//
			try (Git git = new Git(repository)) {
				List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();

				// Might be a rename with specific action
				if (isRenamePossible(diffs)) {
					if (log.isDebugEnabled()) {
						log.debug(
								String.format("commit '%s' is treated as a 'rename' commit", commit.getShortMessage()));
					}
					renameDetector.addAll(diffs);
					List<DiffEntry> files = renameDetector.compute();
					processDiffEntries(analysis, commit, files, velocity);
				} else {
					processDiffEntries(analysis, commit, diffs, velocity);
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
				List<DiffEntry> diffs,
				ParserVelocity parserVelocity) {

		for (DiffEntry de : diffs) {
			
			//
			// If we have configured the crawl with prefiltering of files
			// AND
			// this path doesn't match the eligibility pattern, we skip it.
			//
			if (this.prefilterEligibility && !this.isEligible(de.getNewPath())) {
				continue;
			}
			
			if (log.isDebugEnabled() &&
				(this.crawlerFilterDebug != null) &&
					(	"*".contentEquals(this.crawlerFilterDebug)
						|| 	( (de.getNewPath() != null) && (de.getNewPath().contains(this.crawlerFilterDebug)))
						|| 	( (de.getOldPath() != null) && (de.getOldPath().contains(this.crawlerFilterDebug))))) {
				log.debug(String.format("%s %s %s", de.getChangeType(), de.getOldPath(), de.getNewPath()));
			}
			
			switch (de.getChangeType()) {
			case RENAME:
				if (log.isDebugEnabled()) {
					log.debug(String.format("%s is renammed into %s", de.getOldPath(), de.getNewPath()));
				}
				analysis.renameFilePath(de.getNewPath(), de.getOldPath());
				break;
			case DELETE:
				if (DEV_NULL.equals(de.getNewPath())) {
					analysis.removeFilePath(de.getOldPath());
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
				//
				// A MODIFY tag might be an ADD.
				// We first have to test if this file wasn't already taken in account by the analysis.
				//
				if (analysis.containsFile(de.getNewPath())) {
					analysis.keepPathModified(de.getNewPath());
				}
			case ADD: 
				PersonIdent author = commit.getAuthorIdent();
				analysis.addChange(
						de.getNewPath(),
						new SourceChange(
								commit.getId().toString(), 
								author.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
								author.getName(),
								author.getEmailAddress()));
				parserVelocity.increment();
				break;
			default:
				if (log.isDebugEnabled()) {
					log.debug(String.format("Unexpected type of change %s %s %s", de.getChangeType(),
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

		if (log.isDebugEnabled()) {
			log.debug(
					String.format("Finalizing the changes collection with the repository location %s", sourceLocation));
		}
		
		/*
		 * For DEBUG purpose we make a redundant test on the list of changes 
		 * Are all files in the repository referenced in the list of change ? 
		 * They should be all present.
		 */
		if (log.isDebugEnabled()) {
			log.debug("List of ghost files");
			List<String> content;
			try (Stream<Path> stream = Files.find(Paths.get(sourceLocation), 999, (p, bfa) -> bfa.isRegularFile())) {
				content = stream.map(Path::toString).map(s -> s.substring(sourceLocation.length()-1))
						.collect(Collectors.toList());
				content.stream().filter(s -> !analysis.containsFile(s)).forEach(log::debug);
			}
		};

		Iterator<String> iterPath = analysis.iteratorFilePath();
		while (iterPath.hasNext()) {
			String filePath = iterPath.next();
			// File does not exist anymore on the repository
			File f = Paths.get(sourceLocation + filePath).toFile();
			if (!f.exists()) {
				iterPath.remove();
			} else {
				// Hidden files, mainly internal GIT files are removed.
				if (f.isHidden()) {
					iterPath.remove();
				}
			}
		}
	}

	@Override
	public void filterEligible(RepositoryAnalysis analysis) {
		
		analysis.setChanges(
			new SourceControlChanges(
				analysis.getChanges()
					.entrySet()
	                .stream()
	                .filter(map -> isEligible(map.getKey()))
	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
		);
		
	}

	@Override
	public void removeNonRelevantDirectories(Project project, RepositoryAnalysis analysis) {	
		project.getLibraries().stream().forEach(dep -> {
			Iterator<String> iteratorFilePath = analysis.iteratorFilePath();
			while (iteratorFilePath.hasNext()) {
				String path = iteratorFilePath.next();
		 		if (path.contains(dep.getExclusionDirectory())) {
		 			iteratorFilePath.remove();
		 		}
			}
		});
	}
	
	@Override
	public CommitRepository loadRepositoryFromCacheIfAny(Project project) throws IOException, SkillerException {
		
		//
		// Test if this repository is available in cache.
		// If this repository exists, return it immediately.
		//
		if (cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Using cache file for project %s", project.getName()));
			}

			CommitRepository repository = cacheDataHandler.getRepository(project);

			//
			// Since the last parsing of the repository, some developers might have been
			// declared (or updated), and are matching (or not) to unknown pseudos.
			// We test each ghost and re-initialize the set.
			//
			repository.setUnknownContributors(
					repository.unknownContributors()
						.stream()
						.filter(pseudo -> (staffHandler.lookup(pseudo) == null)) // Pseudo does not match any staff member
						.collect(Collectors.toSet()));

			//
			// We update the ghosts list, in the project with the up-to-date list of of ghosts.
			//
			projectHandler.integrateGhosts(project.getId(), repository.unknownContributors());
				
				
			return repository;
		} else {
			return null;
		}
	}
	
	@Override
	public CommitRepository parseRepository(final Project project, final ConnectionSettings settings)
			throws IOException, SkillerException {

		//
		// We load the repository from cache, if any exists. (my method name is just perfect!)
		//
		CommitRepository repository = loadRepositoryFromCacheIfAny(project);
		if (repository != null) {
			return repository;
		}
		
		if (project.getLocationRepository() == null) {
			throw new SkillerException(Error.CODE_REPO_MUST_BE_ALREADY_CLONED,
					Error.MESSAGE_REPO_MUST_BE_ALREADY_CLONED);
		}

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
		if (log.isDebugEnabled()) {
			log.debug(String.format("loadChanges (%s) returns %d entries", project.getName(), analysis.numberOfChanges()));
		}

		tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), String.format("%d changes have been detected on the repository", analysis.numberOfChanges()));
	
		/**
		 * We save the directories of the repository.
		 */
		dataSaver.saveRepositoryDirectories(project, analysis.getChanges());
		
		/**
		 * We save the changes file on the file system.
		 */
		dataSaver.saveChanges(project, analysis.getChanges());

		/**
		 * We finalize & cleanup the content of the collection
		 */
		this.finalizeListChanges(project.getLocationRepository() + "/", analysis);
		if (log.isDebugEnabled()) {
			log.debug(String.format("finalizeListChanges (%s) returns %d entries", project.getName(),
					analysis.numberOfChanges()));
		}

		// Entries non filtered
		int roughEntries = analysis.numberOfChanges();
		
		this.filterEligible(analysis);
		if (log.isDebugEnabled()) {
			log.debug(
					String.format("filterEligible (%s) returns %d entries from %d originals", project.getName(), analysis.numberOfChanges(), roughEntries));
		}
		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), String.format("%d changes are eligible for the analysis", analysis.numberOfChanges()));
		
		// Updating the importance
		this.updateImportance(project, analysis);
		
		// Retrieve directories candidate for being exclude from the analysis 
		// The resulting set contains only source files without a commit history of modification.
		// They have only be added.
		analysis.extractCandidateForDependencies();
	
		//
		// We filter the candidates with a dependency marker such as "jquery".
		// The analysis container has a collection of path (pathsCandidate) 
		// which might be contain file like /toto/titi/jquery/src/jquery-internal.js, candidate for being excluded from the analysis.
		//
		selectPathDependencies (analysis, dependenciesMarker());
		
		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), String.format("Dependencies have been excluded from analysis"));
		
		//
		// We retrieve the root paths of all libraries present in the project (if any) 
		// The resulting list is saved in the project object.
		//
		this.retrieveRootPath(analysis);
		
		/**
		 * We remove the non relevant directories from the crawler analysis
		 */
		this.removeNonRelevantDirectories(project, analysis);
		
		
		/**
		 * We cleanup the pathnames each location (e.g. "src/main/java" is removed)
		 */
		// analysis.cleanupPaths(projectDashboardCustomizer);

		//
		// Set of unknown contributors who have work on this repository.
		//
		final Set<String> unknownContributors = new HashSet<String>();

		//
		// Handling the staff aspect from the project.
		//
		this.handlingProjectStaffAndGhost(project, analysis, unknownContributors);

		//
		// We detect the ecosystem in the analysis and we save them in the project.
		//
		this.updateProjectEcosystem(project, analysis);
		 
		//
		// Create a repository. 
		//
		repositoryOfCommit = new BasicCommitRepository();

		//
		// Transfer the analysis data in the result file.
		//
		analysis.transferRepository(repositoryOfCommit);
		
		//
		// We update the unknown contributors.
		//
		repositoryOfCommit.setUnknownContributors(unknownContributors);
		
		//
		// Saving the repository into the cache
		//
		cacheDataHandler.saveRepository(project, repositoryOfCommit);

		return repositoryOfCommit;
	}

	
	/**
	 * <p>
	 * This method is managing the staff or the ghost detected in the repository.
	 * </p>
	 * @param project the given project
	 * @param analysis the analysis processed on this project
	 * @param unknownContributors set of unknown contributors
	 * @throws SkillerException thrown if any exception occurs
	 */
	private void handlingProjectStaffAndGhost(Project project, RepositoryAnalysis analysis, Set<String> unknownContributors) throws SkillerException {
		
		//
		// We update the staff identifier on each change entry.
		//
		this.updateStaff(project, analysis, unknownContributors);

		//
		// We save the unknown contributors, into the "ghosts project" collection.
		//
		projectHandler.integrateGhosts(project.getId(), unknownContributors);
		
		//
		// Retrieve the list of contributors involved in the project.
		//
		List<Contributor> contributors = analysis.gatherContributors();

		if (log.isDebugEnabled()) {
			log.debug(
				"Taking account of retrieved contributors from the repository into the project list of participants");
		}

		//
		// Update the staff team missions with the contributors.
		//
		staffHandler.involve(project, contributors);
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d contributors retrieved : ", contributors.size()));
			contributors.stream().forEach(contributor -> {
				String fullname = staffHandler.getFullname(contributor.getIdStaff());
				log.debug(String.format("%d %s", contributor.getIdStaff(),
						(fullname != null) ? fullname : "unknown"));
			});
		}
		
		// Displaying results...
		if (log.isInfoEnabled() && (!unknownContributors.isEmpty())) {
			log.info(String.format("Unknown contributors for project %s", analysis.getProject().getName()));
			unknownContributors.stream().forEach(log::info);
		}
		
	}

	/**
	 * <p>
	 * Update the Project ecosystem.
	 * </p>
	 * @param project the given project
	 * @param analysis the repository analysis
	 * @throws SkillerException thrown if any exception occurs
	 */
	private void updateProjectEcosystem(Project project, RepositoryAnalysis analysis) throws SkillerException {
		//
		// To identify the ecosystem, all files are taken in account. 
		//
		Set<String> allPaths = new HashSet<>();
		allPaths.addAll(analysis.getPathsModified());
		allPaths.addAll(analysis.getPathsAdded());
		List<Ecosystem> ecosystems = ecosystemAnalyzer.detectEcosystems(new ArrayList<String>(allPaths));
		
		if (log.isDebugEnabled()) {
			log.debug("List of ecosystems detected");
			ecosystems.stream().map(Ecosystem::getTitle).forEach(log::debug);
		}
		List<Integer> ids = ecosystems.stream().map(Ecosystem::getId).collect(Collectors.toList());
		projectHandler.saveEcosystems(project, ids);
		
	}
	
	/**
	 * Log a commit record in debug mode.
	 * 
	 * @param commit the given commit
	 */

	private void log(RevCommit commit) {
		if (log.isDebugEnabled()) {
			PersonIdent author = commit.getAuthorIdent();
			StringBuilder sb = new StringBuilder();
			sb.append(Global.LN).append("shortMessage : " + commit.getShortMessage()).append(Global.LN)
					.append("id : " + author.getEmailAddress()).append(Global.LN).append("date : " + author.getWhen())
					.append(Global.LN).append("authorIdent.name : " + author.getName()).append(Global.LN)
					.append(Global.LN);
			log.debug(sb.toString());
		}

	}

	/**
	 * Check if the path is an eligible source for the activity dashboard.
	 * 
	 * @param path
	 * @return True if the path should be included
	 */
	boolean isEligible(final String path) {

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
	@Async
	public RiskDashboard generateAsync(final Project project, final SettingsGeneration settings) {
		boolean failed = false;
		try {
			tasks.addTask(DASHBOARD_GENERATION, "project", project.getId());
			return generate(project, settings);
		} catch (SkillerException se) {
			tasks.logMessage(DASHBOARD_GENERATION, "project", project.getId(), se.errorCode, se.errorMessage);
			failed = true;
			return null;
		} catch (GitAPIException | IOException e) {
			tasks.logMessage(DASHBOARD_GENERATION, "project", project.getId(), 666, e.getLocalizedMessage());
			failed = true;
			return null;
		} finally {
			try {
				if (!failed) {
					tasks.completeTask(DASHBOARD_GENERATION, "project", project.getId());
				} else {
					tasks.completeTaskOnError(DASHBOARD_GENERATION, "project", project.getId());					
				}
			} catch (SkillerException e) {
				e.printStackTrace();
				log.error(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public RiskDashboard generate(final Project project, final SettingsGeneration cfgGeneration)
			throws IOException, SkillerException, GitAPIException {

		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), "Starting the generation !");

		final ConnectionSettings settings = connectionSettings(project);

		if (!cacheDataHandler.hasCommitRepositoryAvailable(project)) {
			try {
				this.clone(project, settings);
			} catch (final Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("GIT clone failed", e);
				}
				throw e;
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("The project %s is cloned into a temporay directory", project.getName()));
			}
		}

		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), "Git clone successfully done!");
		
		//
		// If a cache is detected and available for this project, it will be returned this method.
		// This variable is not final. Might be overridden by the filtering operation (date of staff member filtering)
		//
		CommitRepository repo = this.parseRepository(project, settings);
		if (log.isDebugEnabled()) {
			log.debug(String.format("The repository has been parsed. It contains %d records in the repository, and %d ghosts", repo.size(), repo.unknownContributors().size()));
		}

		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), "Parsing of the repository complete!");

		//
		// The project is updated with the detected skills in the repository (if any) 
		//
		this.projectHandler.updateSkills(project, new ArrayList<CommitHistory>(repo.getRepository().values()));
		
		//
		// Does the process requires a personalization ?
		// e.g. filtering on a staff identifier or processing the history crawl from a starting date
		//
		if (cfgGeneration.requiresPersonalization()) {
			repo = personalizeRepo(repo, cfgGeneration);
		}

		RiskDashboard data = this.aggregateDashboard(project, repo);

		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), "Data aggregation done !");
		
		// We collapse empty directory inside their first sub-directory
		// the node com & the node google will become one single node com/google
		if (collapseEmptyDirectory) {
			if (log.isDebugEnabled()) {
				log.debug("Aggregating empty directories in the chart");
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
		 * This estimation will affect the color of the dot-risk on the form project.
		 */
		this.riskSurveyor.evaluateProjectRisk(project, data.riskChartData);

		// We send back to new risk level to the front-end application.
		data.setProjectRiskLevel(project.getStaffEvaluation());
		
		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Risk evaluation done !");
		
		if (log.isDebugEnabled()) {
			if ((data.undefinedContributors != null) && (!data.undefinedContributors.isEmpty())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unknown contributors detected during the dashboard generation").append(LN);
				data.undefinedContributors.stream().forEach(ukwn -> sb.append(ukwn.getPseudo()).append(LN));
				log.debug(sb.toString());
			}

			if ((project.getGhosts() != null) && (!project.getGhosts().isEmpty())) {
				StringBuilder sb = new StringBuilder();
				sb.append("Registered ghosts in the project record :").append(LN);
				project.getGhosts().stream().forEach(g -> sb.append(g.getPseudo()).append(" : ").append(g.getIdStaff())
						.append("/").append(g.isTechnical()).append(LN));
				log.debug(sb.toString());
			}
		}
		return data;
	}

	@Override
	public CommitRepository personalizeRepo(CommitRepository globalRepo, SettingsGeneration settings) {
		final LocalDate startingDate = new Date(settings.getStartingDate()).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Filtering the repositiory for id:{0}, and starting date:{1}",
					settings.getIdStaffSelected(), startingDate));
		}
		CommitRepository personalizedRepo = new BasicCommitRepository();
		for (CommitHistory commits : globalRepo.getRepository().values()) {
			commits.operations.stream().filter(
					it -> ((it.idStaff == settings.getIdStaffSelected()) || (settings.getIdStaffSelected() == 0)))
					.filter(it -> (it.getDateCommit()).isAfter(startingDate))
					.forEach(item -> personalizedRepo.addCommit(commits.sourcePath, item.idStaff, item.getAuthorName(), item.getDateCommit(),
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
			try {
				final String clearPassword;
				if ((project.getPassword() == null) || (project.getPassword().length() == 0)) {
					clearPassword = null;
				} else {
					clearPassword = DataEncryption.decryptMessage(project.getPassword());
				}
				settings.setPassword(clearPassword);
			} catch (final SkillerException se) {
				System.out.println("project.getPassword() " + project.getPassword());
				se.getCause().printStackTrace();
				throw se;
			}
			return settings;
		}

		if (project.isIndirectAccess()) {
			final String fileProperties = pathConnectionSettings + project.getConnectionSettingsFile();
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

			if (log.isDebugEnabled()) {
				log.debug(String.format("GIT remote URL %s with user %s", settings.getUrl(), settings.getLogin()));
			}
			return settings;
		}
		throw new SkillerException(CODE_UNEXPECTED_VALUE_PARAMETER, MESSAGE_UNEXPECTED_VALUE_PARAMETER);
	}

	@Override
	public boolean hasAvailableGeneration(Project project) throws IOException {
		boolean result = cacheDataHandler.hasCommitRepositoryAvailable(project);
		if (log.isDebugEnabled()) {
			log.debug(String.format("hasAvailableGeneration(%d)? : %s", project.getId(), result));
		}
		return result;
	}

	@Override
	public void updateStaff(Project project, RepositoryAnalysis analysis, Set<String> unknownContributors) {

		List<String> authors = analysis.authors();

		authors.forEach(

				author -> {

					final Staff staff = staffHandler.lookup(author);
					if ((staff == null) && (author.split(" ").length == 1)) {
						Optional<Ghost> oGhost = project.getGhosts()
								.stream()
								.filter(g -> !g.isTechnical())
								.filter(g -> g.getIdStaff() > 0)
								.filter(g -> author.equalsIgnoreCase(g.getPseudo()))
								.findFirst();
						if (oGhost.isPresent()) {
							Ghost selectedGhost = oGhost.get();
							if (staffHandler.getStaff(selectedGhost.getIdStaff()) == null) {
								throw new SkillerRuntimeException("Ghost " + selectedGhost.getPseudo() + " has an invalid idStaff " + selectedGhost.getIdStaff());
							}
							int ghostIdentified = staffHandler.getStaff(selectedGhost.getIdStaff()).getIdStaff();
							//
							// We update the staff collection !
							//
							analysis.updateStaff (author, ghostIdentified);
							//
							// We find a staff entry, but we keep the pseudo in the unknowns list
							// in order to be able to change the relation between the ghost & the staff
							// member in the dedicated Angular component
							//
							unknownContributors.add(author);
						}
					}
					if (staff == null) {
						if (log.isDebugEnabled()) {
							log.debug(String.format("No staff found for the criteria %s", author));
						}
						unknownContributors.add(author);
					} else {
						//
						// We update the staff collection !
						//
						analysis.updateStaff (author, staff.getIdStaff());
					}

				});

		this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,  project.getId(), 
				String.format("All changes have been assigned to registered staff members"));

	}

	@Override
	public void updateImportance(Project project, RepositoryAnalysis analysis) throws SkillerException {
		
		final AssessorImportance assessor = new FileSizeImportance();

		List<String> sortedPaths = analysis.getChanges().keySet()
				.stream()
				.sorted()
				.collect(Collectors.toList());
	
		long importance = -1;
		for (String path : sortedPaths) {
			importance = assessor.getImportance(project, path, ImportanceCriteria.FILE_SIZE);
			analysis.setFileImportance(path, (int) importance);
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
					if (pathAdded.charAt(posMarker + marker.length() - 1) == INTERNAL_FILE_SEPARATORCHAR) {
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
		if (log.isDebugEnabled()) {
			log.debug("Dependencies path");
			analysis.getPathsCandidate().stream().forEach(log::debug); 
		}
	}

	@Override
	public void retrieveRootPath(RepositoryAnalysis analysis) throws IOException {
		
		if ( log.isDebugEnabled() && (this.crawlerFilterDebug != null)) {
			log.debug("Added files only list");
			if ("*".equals(this.crawlerFilterDebug)) {
				analysis.getPathsAdded().stream().forEach(log::debug);
			} else {
				analysis
				.getPathsAdded()
				.stream()
				.filter(p -> p.indexOf(this.crawlerFilterDebug) != -1)
				.forEach(log::debug);
			}
		}
		
		for (String pathname : analysis.getPathsCandidate()) {
			
			String absolutePath = analysis.getProject().getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname;
			if (!Files.exists(Paths.get(absolutePath))) {
				throw new SkillerRuntimeException(String.format("WTF %s does not exist anymore !", absolutePath));
			}

			if (containFilesOnlyAdded(analysis, new File(absolutePath))) {
				analysis.getProject().add( new Library(pathname, Global.LIBRARY_DETECTED));
				if (log.isDebugEnabled()) {
					log.debug (String.format("Handling %s as a dependency", pathname));
				}
			}
		}
		if (log.isInfoEnabled()) {
			log.info("Libraries detected in the repository :");
			analysis.getProject().getLibraries()
				.stream()
				.filter(lib -> lib.getType() == Global.LIBRARY_DETECTED)
				.map(Library::getExclusionDirectory)
				.forEach(log::info);
		}
	}
		
	@Override
	public boolean testConnection(Project project) {
		try {
			ConnectionSettings settings = connectionSettings(project);
			URIish uri = new URIish( project.getUrlRepository() );
			Transport transport = Transport.open( uri );
			transport.setCredentialsProvider(
					new UsernamePasswordCredentialsProvider(settings.getLogin(), settings.getPassword()));
			transport.openFetch();
			return true;
		} catch (final Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("testConnection('%s') failed !", project.getName()), e);
			}
			return false;
		}
	}

	private boolean containFilesOnlyAdded(final RepositoryAnalysis analysis, File dependency)  throws IOException {
		
		File[] children = dependency.listFiles();
		
		if (log.isDebugEnabled()) {
			log.debug (String.format("Project local repository %s",  analysis.getProject().getLocationRepository()));
		}
		
		for (File child : children) {
			if (log.isDebugEnabled()) {
				log.debug (String.format("Examining %s", child.getCanonicalPath()));
			}
			
			if (child.isDirectory()) {
				return containFilesOnlyAdded(analysis, child);
			}
			
			int lengthLocationReposition = analysis.getProject().getLocationRepository().length();
			String testing = child.getCanonicalPath().substring(lengthLocationReposition+1);
			testing = testing.replace(File.separatorChar, INTERNAL_FILE_SEPARATORCHAR);
			
			
			//
			// First, the file has to be caught by the crawler.
			// (We avoid all files which do not match the filter criteria (.java, .js, ...))
			//
			if (!analysis.containsFile(testing)) {
				continue;
			}
			
			if (!analysis.getPathsAdded()
					.contains(testing)) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("%s has evicted the dependency %s", 
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

	@Override
	public void displayConfiguration() {
		log.info("Display the configuration");
		log.info("----------------------------------");
	}

	
}
