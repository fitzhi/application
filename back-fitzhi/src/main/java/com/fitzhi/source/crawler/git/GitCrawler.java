package com.fitzhi.source.crawler.git;

import static com.fitzhi.Error.CODE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static com.fitzhi.Error.CODE_PARSING_SOURCE_CODE;
import static com.fitzhi.Error.CODE_PROJECT_CANNOT_RETRIEVE_INITIAL_COMMIT;
import static com.fitzhi.Error.CODE_UNEXPECTED_VALUE_PARAMETER;
import static com.fitzhi.Error.CODE_CANNOT_CREATE_DIRECTORY;
import static com.fitzhi.Error.MESSAGE_CANNOT_CREATE_DIRECTORY;
import static com.fitzhi.Error.MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_PARSING_SOURCE_CODE;
import static com.fitzhi.Error.MESSAGE_PROJECT_CANNOT_RETRIEVE_INITIAL_COMMIT;
import static com.fitzhi.Error.MESSAGE_UNEXPECTED_VALUE_PARAMETER;
import static com.fitzhi.Error.getStackTrace;
import static com.fitzhi.Global.DASHBOARD_GENERATION;
import static com.fitzhi.Global.INTERNAL_FILE_SEPARATORCHAR;
import static com.fitzhi.Global.LN;
import static com.fitzhi.Global.PROJECT;
import static com.fitzhi.Global.NO_PROGRESSION;

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
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
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

import com.fitzhi.Error;
import com.fitzhi.Global;
import com.fitzhi.ShouldNotPassHereRuntimeException;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.AsyncTask;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataChartHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.RiskProcessor;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.SkylineProcessor;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.bean.impl.RiskCommitAndDevActiveProcessorImpl.StatActivity;
import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.SourceCodeDiffChange;
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
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;
import com.fitzhi.source.crawler.impl.AbstractScannerDataGenerator;
import com.google.gson.Gson;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffConfig;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RenameCallback;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchConnection;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
public class GitCrawler extends AbstractScannerDataGenerator {

    /**
     * <p>
     * Do we create dynamicaly staff member from the ghosts list 
     * </p>
     */
    @Value("${autoStaffCreation}")
    private boolean autoStaffCreation;

    /**
     * Patterns to take account, OR NOT, a file within the parsing process.<br/>
     * For example, a file with the suffix .java is involved.
     */
    @Value("${patternsInclusion}")
    private String patternsInclusion;

    /**
     * Do we log each commit records in the logger ? When true, this settings will
     * produce a large amount of data
     */
    @Value("${logAllCommitRecords}")
    private boolean logAllCommitRecords;

    /**
     * Markers of dependencies.<br/>
     * These markers will be used to detect the possible presence of dependencies in
     * the repository.
     */
    @Value("${dependenciesMarker}")
    private String dependenciesMarker;

    /**
     * <p>
     * <em>Optional</em> repositories location.
     * </p>
     * <ul>
     * <Li>
     * If this member variable is {@code null}, GitCrawler will create a temporary directory.
     * </li>
     * <li>
     * If not, this variable hosts the local destination.
     * </li>
     * </ul>
     */
    @Value("${gitcrawler.repositories.location:#{null}}")
    private String reposDir;

    /**
     * This boolean will be set to {@code true} if the location reposDir exists on file system, and does not need to be create.
     */
    private boolean reposDirExist = false;

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
     * <b>BUT</b> Possibly, there is no source files present in
     * <code><b>fr</b></code>. So instead of keeping 2 levels of hierarchy (with an
     * empty one), it would be easier to aggregate the 2 directories into the
     * resulting one : <code>fr.my-package</code>
     * </p>
     */
    @Value("${collapseEmptyDirectory}")
    private boolean collapseEmptyDirectory;

    /**
     * <p>
     * This {@code boolean} is setting the fact that the eligibility validation is
     * made <u>prior</u> to the creation of the repository-chart data file, or
     * <u>after</u>.<br/>
     * Techxhì is storing intermediate data on a file named
     * "{@link Project#getId()}-{@link Project#getName()}.json".
     * </p>
     * <p>
     * The consequence of this settings is :
     * <ul>
     * <li>if {@code true}, the full global generation will be faster, because data
     * are already filtered. But, if you want to change the pattern of inclusion, on
     * the fly, you will have to regenerate the full chart.</li>
     * <li>If {@code false}, the crawler catch all files in the repository (e.g. the
     * whole repository), before working, or filtering on it. The generation will be
     * slower, but the chart will be faster to filter.</li>
     * </ul>
     * <font color="darkGreen"><b>Our recommendation is to setup this property to
     * {@code true}.</b></font>
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
     * Service in charge of handling the skills.
     */
    @Autowired
    SkillHandler skillHandler;

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

    /**
     * Spring service in charge of the data chart generation and management.
     */
    @Autowired
    DataChartHandler dataChartHandler;

    /**
     * Spring service in charge of saving the working data on the server filesystem.
     */
    @Autowired
    DataHandler dataSaver;

    /**
     * Service Spring in charge of the generation of the skyline
     */
    @Autowired
    SkylineProcessor skylineProcessor;

    /**
     * Filter in charge to filter some debugging information.<br/>
     * This string is updated with the system property
     * <code>crawler.filter.debug</code>.<br/>
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

        this.crawlerFilterDebug = System.getProperty("crawler.filter.debug");

        if ((log.isDebugEnabled()) && (this.crawlerFilterDebug != null)) {
            log.debug(String.format("Debugging filter %s", this.crawlerFilterDebug));
        }
    }

    @Override
    protected ProjectHandler projectHandler() {
        return this.projectHandler;
    }

    @Override
    protected StaffHandler staffHandler() {
        return this.staffHandler;
    }

    @Override
    protected SkillHandler skillHandler() {
        return this.skillHandler;
    }

    @Override
    protected AsyncTask tasks() {
        return this.tasks;
    }

    @Override
    public void clone(final Project project, ConnectionSettings settings)
            throws IOException, GitAPIException, ApplicationException {

        // Will we execute a git.clone() or a git.pull() ?
        // This boolean will decide. If TRUE, this will be a clone
        boolean execClone;

        Path path;
        if (project.getLocationRepository() == null) {
            path = createDirectoryAsCloneDestination(project, settings);
            // We remove the destination directory if any.
            removeCloneDir(path);
            if (log.isInfoEnabled()) {
                log.info(String.format("The directory %s has been removed", path.toAbsolutePath().toString()));
            }
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
            if (log.isInfoEnabled()) {
                log.info(String.format("Git clone of project %s for branch %s", project.getName(), project.getBranch()));
            }
            if (settings.isPublicRepository()) {
                Git git = Git.cloneRepository()
                    .setDirectory(path.toAbsolutePath().toFile()).setURI(settings.getUrl())
                    .setBranch(project.getBranch())
                    .setProgressMonitor(new CustomProgressMonitor())
                    .call();
                git.close();
            } else {
                Git git = Git.cloneRepository()
                    .setDirectory(path.toAbsolutePath().toFile())
                    .setURI(settings.getUrl())
                    .setCredentialsProvider(
                            new UsernamePasswordCredentialsProvider(settings.getLogin(), settings.getPassword()))
                    .setBranch(project.getBranch())
                    .setProgressMonitor(new CustomProgressMonitor())
                    .call();
                git.close();
            }
            if (log.isDebugEnabled()) {
                log.debug("Clone complete & succcessful !");
            }
        } else {

            try (Git git = Git.open(Paths.get(getLocalDotGitFile(project)).toFile())) {

                if (log.isInfoEnabled()) {
                    log.info(String.format("Git pull of project %s", project.getName()));
                }

                if (log.isDebugEnabled()) {
                    log.debug("We fetch first the local repository");
                }
                git.fetch().setProgressMonitor(new CustomProgressMonitor());

                if (log.isDebugEnabled()) {
                    log.debug("And then we pull it");
                }
                git.pull().setProgressMonitor(new CustomProgressMonitor());
            }
            if (log.isDebugEnabled()) {
                log.debug("Local repository succcessfully updated !");
            }
        }

        // Saving the local repository location
        String locationRepository = path.toFile().getCanonicalPath();
        projectHandler.saveLocationRepository(project.getId(), locationRepository);
        project.setLocationRepository(locationRepository);
    }

    /**
     * This method will remove recurcively the content of a non empty directory.
     * @param path the path of the directory to be removed
     * @throws IOException thrown if any IO exception occurs
     */
    static void removeCloneDir(Path path) throws IOException {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @Override
    public Path createDirectoryAsCloneDestination(Project project, ConnectionSettings settings) throws ApplicationException {

        if (reposDir == null) {
            // Create a temporary local path where the remote project repository will be cloned.
            // Note : For Windows based system, we replace the blank character with an underscore
            Path path = mkdirLocalRepo(project);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Cloning the repository %s inside the CREATED path %s", settings.getUrl(),
                        path.toAbsolutePath()));
            }
            return path;        
        } else {
            mkdirReposDirectory();
            File f =  new File(String.format("%s%s%d", reposDir, File.separator, project.getId()));
            if (!f.exists()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Creation of directory %s", f.getAbsolutePath()));
                }
                f.mkdir();
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("Cloning the repository %s inside the CREATED path %s", settings.getUrl(),
                        f.getPath()));
            }
            return f.toPath();
        }
    }

    /**
     * <p>
     * Create the directory reposDir.
     * </p>
     * @throws ApplicationException throw if any exception occurs.
     */
    private void mkdirReposDirectory() throws ApplicationException {
        try {
            if (!reposDirExist) {
                File f =  new File(reposDir);
                reposDirExist = f.exists();
                if (!reposDirExist)  {
                    if (!f.mkdir()) {
                        throw new ApplicationException(CODE_CANNOT_CREATE_DIRECTORY, MessageFormat.format(MESSAGE_CANNOT_CREATE_DIRECTORY, f.getAbsolutePath()));
                    } else {
                        reposDirExist = true;
                    }
                }
            }
        } catch (final SecurityException se) {
            throw new ApplicationException(CODE_IO_EXCEPTION, se.getLocalizedMessage());
        }
    }

    /**
     * <p>
     * Create the directory local directory.
     * </p>
     * @param project the current project which local repository has to be created
     * @throws ApplicationException throw if any exception occurs.
     */
    private Path mkdirLocalRepo(Project project) throws ApplicationException {
        try {
            return Files.createTempDirectory("fitzhi_jgit_" + project.getName().replace(" ","_")  + "_");
        } catch (final IOException ioe) {
            throw new ApplicationException(CODE_IO_EXCEPTION, ioe.getLocalizedMessage(), ioe);
        }
    }   

    @Override
    public Set<String> allEligibleFiles(Project project) throws ApplicationException {

        Path start = Paths.get(project.getLocationRepository());
        Set<String> allFiles;

        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            allFiles = stream
                    .map(String::valueOf)
                    .map(Paths::get).filter(p -> !p.toFile().isDirectory())
                    .map(start::relativize)
                    .map(Path::toString)
                    .filter(p -> ((p.length() <= 4) || !".git".equals(p.substring(0, 4))))
                    .filter(p -> ((p.length() <= 5) || !".git".equals(p.substring(1, 5))))
                    .sorted()
                    .collect(Collectors.toSet());
        } catch (IOException ioe) {
            throw new ApplicationException(CODE_IO_ERROR,
                    MessageFormat.format(MESSAGE_IO_ERROR, project.getLocationRepository()), ioe);
        }

        //
        // If we have configured the crawl with prefiltering of files AND
        // this path doesn't match the eligibility pattern, we skip it.
        //
        if (log.isDebugEnabled()) {
            log.debug ((this.prefilterEligibility) ? "Prefiltering the files" : "No file prefiltetring");
            if (this.prefilterEligibility) {
                log.debug ("Patterns list");
                patternsInclusionList.stream().forEach(pattern -> log.debug(pattern.toString()));
            }
        }     
        Set<String> files = (this.prefilterEligibility)
                ? allFiles.stream().filter(this::isEligible).collect(Collectors.toSet())
                : allFiles;

        return files;
    }

    private static class DiffCollector extends RenameCallback {
        List<DiffEntry> diffs = new ArrayList<DiffEntry>();

        @Override
        public void renamed(DiffEntry diff) {
            diffs.add(diff);
        }
    }

    private DiffCollector diffCollector;

    public FollowFilter getFollowFilter(Repository repository, String filePath) {

        Config config = repository.getConfig();
        config.setBoolean("diff", null, "renames", true);
        config.setInt("diff", null, "renameLimit", Integer.MAX_VALUE);
        DiffConfig dc = config.get(DiffConfig.KEY);
        
        FollowFilter followFilter = FollowFilter.create(filePath, dc);
        followFilter.setRenameCallback(diffCollector);

        return followFilter;
    }

    @Override
    public List<RevCommit> fileGitHistory(Project project, Repository repository, String filePath)
            throws ApplicationException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("Retrieving the GIT history of %s", filePath));
        }
        List<RevCommit> commits = new ArrayList<>();

        try (RevWalk rw = new RevWalk(repository)) {
            diffCollector = new DiffCollector();

            rw.setTreeFilter(getFollowFilter(repository, filePath));

            try {
                rw.markStart(rw.parseCommit(repository.resolve(Constants.HEAD)));
            } catch (final Exception e) {
                String stackTrace = Stream.of(e.getStackTrace()).map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n"));
                log.error(stackTrace);
                throw new ApplicationException(CODE_PARSING_SOURCE_CODE, MESSAGE_PARSING_SOURCE_CODE, e);
            }

            for (RevCommit c : rw) {
                commits.add(c);
            }
        }
        return commits;
    }

    @Override
    public DiffEntry retrieveDiffEntry(String pathname, Repository repository, RevCommit from, RevCommit to) throws ApplicationException {

        DiffEntry de =  retrieveDiffEntry(pathname, repository, from, to, getFollowFilter(repository, pathname));
        if (de == null) {
            de =  retrieveDiffEntry(pathname, repository, from, to, PathFilter.create(pathname));

            if (de == null) {
                retrieveDiffEntry(pathname, repository, from, to, null);
            }
        }
        return de;
    }

    private DiffEntry retrieveDiffEntry(String pathname, Repository repository, RevCommit from, RevCommit to, TreeFilter filter) throws ApplicationException {

        try (Git git = new Git(repository)) {

            try (ObjectReader reader = repository.newObjectReader()) {

                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, from.getTree().getId());
                
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, to.getTree().getId());
    
                List<DiffEntry> diffs = (filter != null) ?
                    git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).setPathFilter(filter).call() :             
                    git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();             
                
                if  (diffs.size() == 0) {
                    return null;
                }
                
                if  (diffs.size() > 1) {
                    if (log.isDebugEnabled()) {
                        String filename = pathname.substring(pathname.lastIndexOf("/"));
                        log.debug(String.format("[%s] Displaying the full diffs array, just containing the filename", filename));
                        diffs.stream().filter(entry -> entry.getOldPath().indexOf(filename) > 0).forEach(entry -> log.debug(entry.getChangeType() + " " + entry.getOldPath() + " " + entry.getNewPath()));
                        if (logAllCommitRecords) {
                            log.debug("Displaying the full diffs array");
                            diffs.stream().forEach(entry -> log.debug(entry.getChangeType() + " " + entry.getOldPath() + " " + entry.getNewPath()));
                        }
                    }
                    return null;
                }
    
                DiffEntry entry = diffs.get(0);
                if (log.isDebugEnabled()) {
                    log.debug(entry.getChangeType().toString());
                    log.debug("from " + from.getName());
                    log.debug("to " + to.getName());
                    if (ChangeType.RENAME.equals(entry.getChangeType())) {
                        log.debug(String.format("[%s] File renamed from %s to %s", pathname, entry.getOldPath(), entry.getNewPath()));
                    }
                    if (ChangeType.ADD.equals(entry.getChangeType())) {
                        log.debug(String.format("[%s] Adding the file %s", pathname, entry.getNewPath()));
                    }
                }
                
                return entry;
            } catch (final Exception e) {
                log.error(getStackTrace(e));
                throw new ApplicationException(CODE_PARSING_SOURCE_CODE, String.format(MESSAGE_PARSING_SOURCE_CODE, pathname), e);
            }
        }
    }
    @Override
    public RepositoryAnalysis retrieveRepositoryAnalysis(Project project, Repository repository) throws ApplicationException {

        RepositoryAnalysis analysis = dataSaver.loadRepositoryAnalysis(project);
        if (analysis == null) {
            analysis = new RepositoryAnalysis(project);
        }
        
        fillRepositoryAnalysis(project, analysis, repository);

        dataSaver.saveRepositoryAnalysis(project, analysis);

        return analysis;

    }

    @Override
    public void fillRepositoryAnalysis(Project project, RepositoryAnalysis analysis, Repository repository) throws ApplicationException {

        Set<String> allEligibleFiles = this.allEligibleFiles(project);

        tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(),
                String.format("%d files in queue for analysis !", allEligibleFiles.size()), 30);
        int numberOfFiles = 0;

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (String file : allEligibleFiles) {
                sb.append(file).append(LN);
                fileGitHistory(project, repository, file).stream()
                        .forEach(c -> sb.append("\t").append(c.getShortMessage()).append(LN));
            }
            log.debug(sb.toString());
        }

        if (log.isInfoEnabled()) {
            log.info(String.format("Retrieving %d files on the repository %s", allEligibleFiles.size(),
                    repository.getDirectory().getAbsoluteFile()));
        }

        try (Git git = new Git(repository)) {

            //
            // We initialize the parser speed-meter.
            //
            ParserVelocity velocity = new ParserVelocity(project.getId(), this.tasks);

            RevCommit firstCommit = null;
            try {
                firstCommit = initialCommit(git);
            } catch (Exception e) {
                throw new ApplicationException (
                    CODE_PROJECT_CANNOT_RETRIEVE_INITIAL_COMMIT, 
                    MessageFormat.format(MESSAGE_PROJECT_CANNOT_RETRIEVE_INITIAL_COMMIT, project.getName()),
                    e);
            }
            
            int totalNumberOfFiles = 0;
            for (String file : allEligibleFiles) {

                // We normalize the file if the server is running on a Windows plattform.
                // This variable is final and well be used to store the changes in the RepositoryAnalysis container.
                final String finalFilePathName = file.replace("\\", "/");

                // This filePathname might changed if the file has been renamed during its history.
                String filePathName = file.replace("\\", "/");

                
                numberOfFiles++;
                if ((numberOfFiles % 100) == 0) {
                    totalNumberOfFiles += numberOfFiles;
                    int progressionPercentage = progressionPercentage(totalNumberOfFiles, allEligibleFiles.size());
                    tasks.logMessage(
                        DASHBOARD_GENERATION, PROJECT, project.getId(),
                        String.format ("%d files have been analyzed !", totalNumberOfFiles),
                        progressionPercentage);
                        numberOfFiles = 0;
                }

                long start = System.currentTimeMillis();
                List<RevCommit> chronoCommits = this.fileGitHistory(project, repository, filePathName);
                velocity.logDurationInFileGitHistory(System.currentTimeMillis() - start);
               
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Working with the filename %s", filePathName));
                    StringBuilder sb = new StringBuilder(Global.LN);
                    chronoCommits.stream()
                        .forEach(c -> log.debug( sb.append(c.getName()).append(" ").append(c.getShortMessage()).append(Global.LN).toString()));
                }

                RevCommit[] tabCommits = chronoCommits.toArray(new RevCommit[0]);

                for (int i=0; i < tabCommits.length;  i++) {
                    
                    RevCommit previousCommit = (i == tabCommits.length - 1) ? firstCommit :  tabCommits[i+1];
                    try (ObjectReader reader = repository.newObjectReader()) {

                        DiffFormatter diffFormater = new DiffFormatter(DisabledOutputStream.INSTANCE);
                        diffFormater.setRepository(repository);
                        diffFormater.setDiffComparator(RawTextComparator.DEFAULT);
                        diffFormater.setDetectRenames(true);  
  
                        if (tabCommits[i].getName().equals(firstCommit.getName())) {
                            break;
                        }
                        start = System.currentTimeMillis();
                        DiffEntry de = this.retrieveDiffEntry(filePathName, repository, previousCommit, tabCommits[i]);
                        velocity.logDurationInRetrieveDiffEntry(System.currentTimeMillis() - start);

                        // We do not want to stop the treatment for (maybe) one single missing entry in the analysis 
                        if (de == null) {
                            log.error(String.format(
                                "INTERNAL ERROR (BUT NONE CRITICAL) : Error has been detected when processing a DiffEntry for file %s between %s and %s",
                                filePathName,
                                previousCommit.getShortMessage(), 
                                tabCommits[i].getShortMessage()));
                            // We skip the end of history for the current file.
                            break;
                        }
                        
                        // We process the DiffEntry and we store the results into the Changes collection 
                        processDiffEntries(analysis, tabCommits[i], finalFilePathName, de, diffFormater, velocity);


                        // We will use now the previous name
                        if (de.getChangeType().equals(ChangeType.RENAME) || de.getChangeType().equals(ChangeType.COPY)) {
                            filePathName = de.getOldPath();
                        }

                    } catch (final Exception e) {
                        String stackTrace = Stream
                            .of(e.getStackTrace())
                            .map(StackTraceElement::toString)
                            .collect(Collectors.joining("\n"));
                        log.error(stackTrace);
                        throw new ApplicationException(CODE_PARSING_SOURCE_CODE, String.format(MESSAGE_PARSING_SOURCE_CODE, finalFilePathName), e);
                    }
                }
            }

            tasks.logMessage(
                DASHBOARD_GENERATION, PROJECT, project.getId(),
                String.format ("All %d files have been analyzed !", allEligibleFiles.size()), 100);

            velocity.logReport();

            velocity.complete();
       
        }
    }

    /**
     * Process the percentage of progression
     * @param numberOfFiles the current number of files alerady treated
     * @param totalNumberOfFiles the total number of files to be treated
     * @return the percentage
     */
    static int progressionPercentage(int numberOfFiles, int totalNumberOfFiles) {
        final int OFFSET = 30;
        double d = OFFSET + numberOfFiles * (100 - OFFSET) / totalNumberOfFiles;
        int progression = (int) Math.floor( d );
        if (log.isDebugEnabled()) {
            log.debug (String.format("(%d * 0.7) / %d gives the progression %d", numberOfFiles, totalNumberOfFiles, progression));
        }
        return progression;
    }

    @Override
    public RevCommit initialCommit(Git git) throws ApplicationException {

        try {
            Iterable<RevCommit> commits = git.log().all().call();
            RevCommit firstCommit = null;
            for (RevCommit commit : commits) {
                if (log.isDebugEnabled()) {
                    log.debug (String.format("Commit %s %s", commit.getShortMessage(), commit.getName()));
                }
                firstCommit = commit;
            }
            return firstCommit;
        } catch (final Exception e) {
            throw new ApplicationException(CODE_PARSING_SOURCE_CODE, String.format(MESSAGE_PARSING_SOURCE_CODE, ""), e);
        }
    }


    @Override
    public void processDiffEntries(RepositoryAnalysis analysis, RevCommit commit, String finalFilePathname,
            DiffEntry de, DiffFormatter diffFormater, ParserVelocity parserVelocity)
            throws ApplicationException {

        if (log.isDebugEnabled() && (this.crawlerFilterDebug != null)
                && ("*".contentEquals(this.crawlerFilterDebug)
                        || ((de.getNewPath() != null) && (de.getNewPath().contains(this.crawlerFilterDebug)))
                        || ((de.getOldPath() != null) && (de.getOldPath().contains(this.crawlerFilterDebug))))) {
            log.debug(String.format("%s %s %s", de.getChangeType(), de.getOldPath(), de.getNewPath()));
            log.debug(String.format("DiffAttribute key %s value %s", de.getDiffAttribute().getKey(),
                    de.getDiffAttribute().getValue()));
        }
        try {
            switch (de.getChangeType()) {
                case RENAME:
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("%s is renammed into %s", de.getOldPath(), de.getNewPath()));
                    }
                    //
                    // We rename the previous history records with the new name
                    //
                    // analysis.renameFilePath(de.getNewPath(), de.getOldPath());
                    //
                    addHistoryRecord(analysis, commit, finalFilePathname, de, diffFormater, parserVelocity);
                    break;
                case DELETE:
                    if (DEV_NULL.equals(de.getNewPath())) {
                        analysis.removeFilePath(de.getOldPath());
                    } else {
                        throw new ApplicationRuntimeException(String.format("%s REQUIRES TO BE NULL", de.getNewPath()));
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
                case MODIFY: // NOSONAR
                    //
                    // A MODIFY tag might be an ADD.
                    // We first have to test if this file wasn't already taken in account by the
                    // analysis.
                    //
                    if (analysis.containsFile(de.getNewPath())) {
                        analysis.keepPathModified(de.getNewPath());
                    }
                case ADD:
                    addHistoryRecord(analysis, commit, finalFilePathname, de, diffFormater, parserVelocity);
                    break;
                default:
                    if (log.isDebugEnabled()) {
                        log.debug(
                            String.format("Unexpected type of change %s %s %s",
                            de.getChangeType(),
                            de.getOldPath(),
                            de.getNewPath()));
                    }
                    break;
            }
        } catch (final IOException ioe) {
            throw new ApplicationException(CODE_PARSING_SOURCE_CODE, String.format(MESSAGE_PARSING_SOURCE_CODE, finalFilePathname), ioe);
        }
    }

    private SourceCodeDiffChange diffFile(DiffEntry diffEntry, DiffFormatter diffFormater)
            throws IOException, CorruptObjectException {
        int linesDeleted = 0;
        int linesAdded = 0;
        for (Edit edit : diffFormater.toFileHeader(diffEntry).toEditList()) {
            linesDeleted += edit.getEndA() - edit.getBeginA();
            linesAdded += edit.getEndB() - edit.getBeginB();
        }
        return new SourceCodeDiffChange(diffEntry.getNewPath(), linesDeleted, linesAdded);
    }

    /**
     * Take accound of a change
     * 
     * @param analysis       the active change container
     * @param commit         the current commit
     * @param filePathname   the final file pathname
     * @param de             the current diff entry.
     * @param diffFormat     the Difference formater
     * @param parserVelocity the parser velocity tracer
     */
    private void addHistoryRecord(RepositoryAnalysis analysis, RevCommit commit, String filePathname, DiffEntry de,
            DiffFormatter diffFormater, ParserVelocity parserVelocity) throws IOException {
        //
        // We log a new history records with the modified lines
        //
        SourceCodeDiffChange diff = this.diffFile(de, diffFormater);
        PersonIdent author = commit.getAuthorIdent();

        analysis.takeChangeInAccount(filePathname,
                new SourceChange(commit.getId().toString(),
                        author.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
                        author.getName(),
                        author.getEmailAddress(), diff));
        parserVelocity.increment();
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

        //
        // For DEBUG purpose we make a redundant check on the list of changes 
        // Are all files in the repository referenced in the list of change ? 
        // They should be all present.
        //
        if (log.isDebugEnabled()) {
            // Content of the changes set
            log.debug("Changes set");
            analysis.getChanges().keySet().forEach(p -> log.debug(p));
            // Ghost files without record in the Analyssis report.
            log.debug("List of ghost files");
            List<String> content;
            try (Stream<Path> stream = Files.find(Paths.get(sourceLocation), 999, (p, bfa) -> bfa.isRegularFile())) {
                content = stream.map(Path::toString)
                        .map(s -> s.substring(sourceLocation.length() + 1))
                        .map(s -> s.replace("\\", "/"))
                        .collect(Collectors.toList());
                content.stream()
                        .filter(s -> !s.contains(".git"))
                        .filter(s -> !analysis.containsFile(s))
                        .forEach(log::debug);
            }
        }

        Iterator<String> iterPath = analysis.iteratorFilePath();
        while (iterPath.hasNext()) {
            String filePath = iterPath.next();
            // File does not exist anymore on the repository
            File f = Paths.get(sourceLocation + "/" + filePath).toFile();
            if (!f.exists()) {
                iterPath.remove();
            } else {
                // Hidden files, mainly internal GIT files are removed.
                if (f.isHidden()) {
                    iterPath.remove();
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("finalizeListChanges (%s) returns %d entries", analysis.getProject().getName(),
                    analysis.numberOfChanges()));
        }

    }

    @Override
    public void filterEligible(RepositoryAnalysis analysis) {

        analysis.setChanges(new SourceControlChanges(
                analysis.getChanges().entrySet().stream().filter(map -> isEligible(map.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));

        // Entries non filtered
        int roughEntries = analysis.numberOfChanges();

        if (log.isDebugEnabled()) {
            log.debug(String.format("filterEligible (%s) returns %d entries from %d originals", analysis.getProject().getName(),
                    analysis.numberOfChanges(), roughEntries));
        }

        this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, analysis.getProject().getId(),
                String.format("%d changes in the GIT repository are eligible for analysis", analysis.numberOfChanges()), NO_PROGRESSION);

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
    public CommitRepository loadRepositoryFromCacheIfAny(Project project) throws IOException, ApplicationException {

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
                // Pseudo does not match any staff member
                repository.unknownContributors().stream()
                    .filter(pseudo -> (staffHandler.lookup(pseudo) == null)) 
                    .collect(Collectors.toSet()));

            //
            // We update the ghosts list, in the project with the up-to-date list of of
            // ghosts.
            //
            projectHandler.integrateGhosts(project.getId(), repository.unknownContributors());

            return repository;
        } else {
            return null;
        }
    }

    @Override
    public CommitRepository parseRepository(final Project project) throws IOException, ApplicationException {

        //
        // We load the repository from cache, if any exists. (my method name is just
        // perfect!)
        //
        CommitRepository repository = loadRepositoryFromCacheIfAny(project);
        if (repository != null) {
            return repository;
        }

        //
        // The repository has not been loaded from cache,
        // but it looks like that the directory where the repository has been cloned is
        // declared
        //
        if (project.getLocationRepository() == null) {
            throw new ApplicationException(Error.CODE_REPO_MUST_BE_ALREADY_CLONED,
                    Error.MESSAGE_REPO_MUST_BE_ALREADY_CLONED);
        }

        // Repository
        final CommitRepository repositoryOfCommit;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        try (Repository repo = builder.setGitDir(
            new File(getLocalDotGitFile(project))).readEnvironment().findGitDir().build()) {

            //
            // load or generate all raw changes declared in the given repository.
            //
            RepositoryAnalysis analysis = retrieveRepositoryAnalysis(project, repo);
            if (log.isDebugEnabled()) {
                log.debug(String.format("loadChanges (%s) returns %d entries", project.getName(),
                        analysis.numberOfChanges()));
            }

            tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(),
                    MessageFormat.format("{0} changes have been detected on the repository", analysis.numberOfChanges()), NO_PROGRESSION);

            // We finalize & cleanup the content of the collection
            this.finalizeListChanges(project.getLocationRepository() + "/", analysis);

            // Filter the changes to the eligible pathnames only.
            this.filterEligible(analysis);

            // Updating the importance
            this.updateImportance(project, analysis);

            //
            // Retrieve directories candidate for being exclude from the analysis
            // The resulting set contains only source files without a commit history of
            // modification.
            // These files have only be added.
            //
            // We will test each entry of the resulting list if it match the possible
            // eviction critéria
            //
            analysis.extractCandidateForDependencies();

            //
            // We filter the candidates with a dependency marker such as "jquery".
            // The analysis container has a collection of path (pathsCandidate)
            // which might be contain file like /toto/titi/jquery/src/jquery-internal.js,
            // candidate for being excluded from the analysis.
            //
            selectPathDependencies(analysis, dependenciesMarker());

            this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT,
                    project.getId(),
                    "Dependencies have been excluded from analysis", 
                    NO_PROGRESSION);

            //
            // We retrieve the root paths of all libraries present in the project (if any)
            // The resulting list is saved in the project object.
            //
            this.retrieveRootPath(analysis);

            //
            // We remove the non relevant directories from the crawler analysis
            //
            this.removeNonRelevantDirectories(project, analysis);

            // Set of unknown contributors who have work on this repository.
            final Set<String> unknownContributors = new HashSet<String>();

            // Handling the staff aspect from the project.
            this.handlingProjectStaffAndGhost(project, analysis, unknownContributors);

            // We detect the ecosystem in the analysis and we save them in the project.
            this.updateProjectEcosystem(project, analysis);

            // Create a repository.
            repositoryOfCommit = new BasicCommitRepository();

            // Transfer the analysis data in the result file.
            analysis.transferRepository(repositoryOfCommit);

            // We update the ghosts contributors.
            repositoryOfCommit.setUnknownContributors(unknownContributors);

            // We generate & save the skyline history for this project.
            this.generateAndSaveSkyline(project, analysis);

            // Saving the repository into the cache
            cacheDataHandler.saveRepository(project, repositoryOfCommit);

            return repositoryOfCommit;
        }
    }

    /**
     * This private method is call by [@link #parseRepository(Project)}
     * 
     * @param project  the current project
     * @param analysis the given analysis processed by this project
     */
    private void generateAndSaveSkyline(Project project, RepositoryAnalysis analysis) throws ApplicationException {
        ProjectLayers projectLayers = skylineProcessor.generateProjectLayers(project, analysis.getChanges());
        if (log.isDebugEnabled()) {
            log.debug(String.format("The project %s has generated %d layers", project.getName(),
                    projectLayers.getLayers().size()));
        }

        dataSaver.saveSkylineLayers(project, projectLayers);
    }

    /**
     * <p>
     * This method is taking in account the staff, or the ghost, who has contributed
     * in the repository.
     * </p>
     * 
     * @param project             the given project
     * @param analysis            the analysis processed on this project
     * @param unknownContributors set of unknown contributors
     * @throws ApplicationException thrown if any exception occurs
     */
    private void handlingProjectStaffAndGhost(Project project, RepositoryAnalysis analysis,
            Set<String> unknownContributors) throws ApplicationException {

        //
        // We update the staff identifier on each change entry.
        //
        this.updateStaff(project, analysis, unknownContributors);

        //
        // We save the unknown contributors into the "contributing ghosts" collection.
        //
        projectHandler.integrateGhosts(project.getId(), unknownContributors);

        //
        // Retrieving the list of contributors involved in the project.
        //
        List<Contributor> contributors = analysis.gatherContributors();

        //
        // Updating each contributor with his activities by skill.
        //
        this.gatherContributorsActivitySkill(contributors, analysis.getChanges(), analysis.getPathsModified());

        //
        // Update the staff team missions with the contributors.
        //
        staffHandler.involve(project, contributors);

    }

    /**
     * <p>
     * Update the Project ecosystem.
     * </p>
     * 
     * @param project  the given project
     * @param analysis the repository analysis
     * @throws ApplicationException thrown if any exception occurs
     */
    private void updateProjectEcosystem(Project project, RepositoryAnalysis analysis) throws ApplicationException {
        //
        // To identify the eco-system, all files are taken in account.
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

    public void log(RevCommit commit) {
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
        } catch (ApplicationException se) {
            tasks.logMessage(DASHBOARD_GENERATION, "project", project.getId(), se.errorCode, se.errorMessage, NO_PROGRESSION);
            failed = true;
            return null;
        } catch (GitAPIException | IOException e) {
            log.error(e.getMessage());
            tasks.logMessage(DASHBOARD_GENERATION, "project", project.getId(), 666, e.getLocalizedMessage(), NO_PROGRESSION);
            failed = true;
            return null;
        } finally {
            try {
                if (!failed) {
                    tasks.completeTask(DASHBOARD_GENERATION, "project", project.getId());
                } else {
                    tasks.completeTaskOnError(DASHBOARD_GENERATION, "project", project.getId());
                }
            } catch (ApplicationException e) {
                log.error(Error.getStackTrace(e));
            }
        }
    }

    @Override
    public RiskDashboard generate(final Project project, final SettingsGeneration cfgGeneration)
            throws IOException, ApplicationException, GitAPIException {

        this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Starting the generation !", NO_PROGRESSION);

        final ConnectionSettings settings = connectionSettings(project);

        if (!projectHandler.hasValidRepository(project)) {
            try {
                this.clone(project, settings);
            } catch (final Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("GIT clone failed", e);
                }
                throw e;
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("The project %s is cloned into the temporay directory %s", project.getName(), project.getLocationRepository()));
            }

            this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Git clone successfully done!", 10);
        } else {
            this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Re-using the local repository!", 10);
        }


        //
        // If a cache is detected and available for this project, it will be returned by this method.
        // This variable is not final. It might be overridden by the filtering operation
        // (either starting from a date of commit, or filtering for a dedicated staff member)
        //
        CommitRepository repo = this.parseRepository(project);
        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "The repository has been parsed. It contains %d records in the repository, and %d ghosts",
                    repo.size(), repo.unknownContributors().size()));
        }

        this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Parsing of the repository complete!", NO_PROGRESSION);

        //
        // The project is updated with the detected skills in the repository (if any)
        //
        this.projectHandler.updateSkills(project, new ArrayList<CommitHistory>(repo.getRepository().values()));

        //
        // Does the process requires a personalization ?
        // e.g. filtering on a staff identifier or processing the history crawl from a
        // starting date
        //
        if (cfgGeneration.requiresPersonalization()) {
            repo = personalizeRepo(repo, cfgGeneration);
        }

        RiskDashboard data = this.aggregateDashboard(project, repo);

        this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Data aggregation done !", NO_PROGRESSION);

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
         * Evaluate and save the level of risk for the whole project. This estimation
         * will affect the color of the dot-risk on the form project.
         */
        this.riskSurveyor.evaluateProjectRisk(project, data.riskChartData);

        // We send back to new risk level to the front-end application.
        data.setProjectRiskLevel(project.getStaffEvaluation());

        this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(), "Risk evaluation done !", NO_PROGRESSION);

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
                    it -> ((it.getIdStaff() == settings.getIdStaffSelected()) || (settings.getIdStaffSelected() == 0)))
                    .filter(it -> (it.getDateCommit()).isAfter(startingDate))
                    .forEach(item -> personalizedRepo.addCommit(commits.getSourcePath(), item.getIdStaff(),
                            item.getAuthorName(), item.getDateCommit(), commits.getImportance()));
        }
        return personalizedRepo;
    }

    /**
     * <p>
     * Load the connection settings for the given project.
     * </p>
     * 
     * @param project the passed project
     * @return the connection settings.
     * @throws ApplicationException thrown certainly if an IO exception occurs
     */
    private ConnectionSettings connectionSettings(final Project project) throws ApplicationException {

        if (project.isNoUserPasswordAccess()) {
            ConnectionSettings settings = new ConnectionSettings();
            settings.setUrl(project.getUrlRepository());
            settings.setPublicRepository(true);
            return settings;
        }

        if (project.isUserPasswordAccess()) {
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
            } catch (final ApplicationException se) {
                log.error("Cause", se.getCause());
                throw se;
            }
            return settings;
        }

        if (project.isRemoteFileAccess()) {
            final String fileProperties = pathConnectionSettings + project.getConnectionSettingsFile();
            File f = new File(fileProperties);
            if (!f.exists()) {
                throw new ApplicationException(CODE_FILE_CONNECTION_SETTINGS_NOFOUND,
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
                throw new ApplicationException(CODE_FILE_CONNECTION_SETTINGS_NOFOUND,
                        MessageFormat.format(MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND, fileProperties), ioe);
            }

            if (log.isDebugEnabled()) {
                log.debug(String.format("GIT remote URL %s with user %s", settings.getUrl(), settings.getLogin()));
            }
            return settings;
        }
        throw new ApplicationException(CODE_UNEXPECTED_VALUE_PARAMETER, MESSAGE_UNEXPECTED_VALUE_PARAMETER);
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
    public void updateStaff(Project project, RepositoryAnalysis analysis, Set<String> unknownContributors) throws ApplicationException {

        List<Author> authors = analysis.authors();

        for (Author author : authors) {
            String authorName = author.getName();
            final Staff staff = staffHandler.lookup(authorName);                    

            // Either this author is already registered as a ghost with the same pseudo
            // or this author can be linked to a registered developer
            // or this author is a new ghost

            if (staff == null) {

                
                // A setting in applications.properties is equal to TRUE
                // We create staff member with the ghost data 
                if (autoStaffCreation && (authorName.split(" ").length > 1)) {
                    Staff st = staffHandler.createEmptyStaff(author);
                    analysis.updateStaff(authorName, st.getIdStaff());
                } else {
                    //
                    // There is no author related to this author 
                    //
                    manageAuthorWithGhostsList(project, analysis, unknownContributors, authorName);
                }

            } else {
                //
                // We found a staff member corresponding to this author
                // We update the staff collection !
                //
                analysis.updateStaff(authorName, staff.getIdStaff());
            }

        }

        this.tasks.logMessage(DASHBOARD_GENERATION, PROJECT, project.getId(),
                String.format("All changes have been assigned to registered staff members"), NO_PROGRESSION);

    }

    private void manageAuthorWithGhostsList(Project project, RepositoryAnalysis analysis, Set<String> unknownContributors, String author) {

        // The use case behind these lines :
        // A ghost has been linked through the Angular ghost form with the pseudo of an existing staff member.
        // This ghost corresponds to this pseudo.
        // So we update for this author the analysis data
        Optional<Ghost> oGhost = project.getGhosts().stream()
            .filter(g -> !g.isTechnical())
            .filter(g -> g.getIdStaff() > 0)
            .filter(g -> author.equalsIgnoreCase(g.getPseudo()))
            .findFirst();
        if (oGhost.isPresent()) {
            Ghost selectedGhost = oGhost.get();
            if (staffHandler.getStaff(selectedGhost.getIdStaff()) == null) {
                throw new ApplicationRuntimeException("Ghost " + selectedGhost.getPseudo()
                        + " has an invalid idStaff " + selectedGhost.getIdStaff());
            }
            int ghostIdentified = selectedGhost.getIdStaff();

            //
            // We update the staff collection !
            //
            analysis.updateStaff(author, ghostIdentified);

            //
            // We find a staff entry, but we keep the pseudo in the unknowns list
            // in order to be able to change the relation between the ghost & the staff member in the dedicated Angular component
            //
            unknownContributors.add(author);
        } else {
            // It's a new ghost
            if (log.isDebugEnabled()) {
                log.debug(String.format("Adding the ghost : %s", author));
            }
            unknownContributors.add(author);
        }

    }

    @Override
    public void updateImportance(Project project, RepositoryAnalysis analysis) throws ApplicationException {

        final AssessorImportance assessor = new FileSizeImportance();

        List<String> sortedPaths = analysis.getChanges().keySet().stream().sorted().collect(Collectors.toList());

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
    public void selectPathDependencies(RepositoryAnalysis analysis, List<String> dependenciesMarker) {

        for (String marker : dependenciesMarker) {
            for (String pathAdded : analysis.getPathsAdded()) {
                int posMarker = pathAdded.indexOf(marker);
                if (posMarker != -1) {

                    final String pathDependency;
                    if (pathAdded.charAt(posMarker + marker.length() - 1) == INTERNAL_FILE_SEPARATORCHAR) {
                        pathDependency = pathAdded.substring(0, posMarker + marker.length() - 1);
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

        if (log.isDebugEnabled() && (this.crawlerFilterDebug != null)) {
            log.debug("Added files only list");
            if ("*".equals(this.crawlerFilterDebug)) {
                analysis.getPathsAdded().stream().forEach(log::debug);
            } else {
                analysis.getPathsAdded().stream().filter(p -> p.indexOf(this.crawlerFilterDebug) != -1)
                        .forEach(log::debug);
            }
        }

        for (String pathname : analysis.getPathsCandidate()) {

            String absolutePath = analysis.getProject().getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR
                    + pathname;
            if (!Files.exists(Paths.get(absolutePath))) {
                throw new ApplicationRuntimeException(String.format("WTF %s does not exist anymore !", absolutePath));
            }

            if (containFilesOnlyAdded(analysis, new File(absolutePath))) {
                analysis.getProject().add(new Library(pathname, Global.LIBRARY_DETECTED));
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Handling %s as a dependency", pathname));
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Libraries detected in the repository :");
            analysis.getProject().getLibraries().stream()
                    .filter(lib -> lib.getType() == Global.LIBRARY_DETECTED)
                    .forEach(lib ->  log.info(lib.getExclusionDirectory()));
        }
    }

    @Override
    public boolean testConnection(Project project) {
        final FetchConnection connection = retrieveFetchConnection(project);
        return (connection != null);
    }

    @Override
    public Collection<Ref> loadBranches(Project project) {
        final FetchConnection connection = retrieveFetchConnection(project);
        if (log.isDebugEnabled()) {
            if (connection == null) {
                log.debug(String.format("Connection failed with the SCM declared for project %d %s", project.getId(),
                        project.getName()));
            } else {
                connection.getRefs().forEach(ref -> log.debug(ref.getName()));
            }
        }
        return (connection != null) ? connection.getRefs() : new ArrayList<Ref>();
    }

    @Override
    public FetchConnection retrieveFetchConnection(Project project) {
        try {

            switch (project.getConnectionSettings()) {
                case Global.USER_PASSWORD_ACCESS:
                case Global.REMOTE_FILE_ACCESS: {
                    ConnectionSettings settings = connectionSettings(project);
                    URIish uri = new URIish(project.getUrlRepository());
                    try (Transport transport = Transport.open(uri)) {
                        transport.setCredentialsProvider(
                                new UsernamePasswordCredentialsProvider(settings.getLogin(), settings.getPassword()));
                        return transport.openFetch();
                    }
                }
                case Global.NO_USER_PASSWORD_ACCESS: {
                    URIish uri = new URIish(project.getUrlRepository());
                    try (Transport transport = Transport.open(uri)) {
                        return transport.openFetch();
                    }
                }
                default: {
                    throw new ShouldNotPassHereRuntimeException();
                }
            }
        } catch (final Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("testConnection('%s') failed !", project.getName()), e);
            }
            return null;
        }
    }

    private boolean containFilesOnlyAdded(final RepositoryAnalysis analysis, File dependency) throws IOException {

        File[] children = dependency.listFiles();

        if (log.isDebugEnabled()) {
            log.debug(String.format("Project local repository %s", analysis.getProject().getLocationRepository()));
        }

        for (File child : children) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Examining %s", child.getCanonicalPath()));
            }

            if (child.isDirectory()) {
                return containFilesOnlyAdded(analysis, child);
            }

            int lengthLocationReposition = analysis.getProject().getLocationRepository().length();
            String testing = child.getCanonicalPath().substring(lengthLocationReposition + 1);
            testing = testing.replace(File.separatorChar, INTERNAL_FILE_SEPARATORCHAR);

            //
            // First, the file has to be caught by the crawler.
            // (We avoid all files which do not match the filter criteria (.java, .js, ...))
            //
            if (!analysis.containsFile(testing)) {
                continue;
            }

            if (!analysis.getPathsAdded().contains(testing)) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("%s has evicted the dependency %s", child.getCanonicalPath(),
                            dependency.getCanonicalPath()));
                }
                return false;
            }
        }
        return true;
    }

    /**
     * @return the location repository entry point <br/>
     *         <i>i.e. the absolute path to the .git file.</i>
     */
    private String getLocalDotGitFile(Project project) {
        return (project.getLocationRepository()
                .charAt(project.getLocationRepository().length() - 1) == File.pathSeparatorChar)
                        ? project.getLocationRepository() + ".git"
                        : project.getLocationRepository() + "/.git";
    }

    @Override
    public void displayConfiguration() {
        log.info("Display the configuration");
        log.info("----------------------------------");
    }

}

