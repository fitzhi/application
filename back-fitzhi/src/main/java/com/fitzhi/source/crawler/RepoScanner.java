/**
 * 
 */
package com.fitzhi.source.crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.StaffActivitySkill;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.git.GitCrawler;
import com.fitzhi.source.crawler.git.ParserVelocity;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchConnection;

/**
 * <p>
 * Source repository scanner.
 * </p>
 * <p>
 * <font color="red">This interface unfortunately has an adherence with <b>GIT</b></font>
 * </p>
 * <p>
 * Future releases should unplugged this link.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface RepoScanner {

	/**
	 * <p>Load the repository from the internal cache.</p>
	 * <p>
	 * <font color="coral">
	 * This method load the repository from cache <b>AND</b> update the ghosts list as well if any new staff member has been created. 
	 * </font>
	 * </p>
	 * @param project the current active project.
	 * @return the repository commit entries if a previous parsing has been saved, or {@code null} if none exists.
	 * @throws IOException thrown if an IO exception occurs when reading the cache file.
	 * @throws ApplicationException thrown most probably, if the project ghosts list update failed.
	 */
	CommitRepository loadRepositoryFromCacheIfAny(Project project) throws IOException, ApplicationException;

	/**
	 * Test if the connection to the SCM will succeed with the actual settings
	 * @param project the current project
	 * @return {@code true} if the connection has succeeded, {@code false} otherwise
	 */
	boolean testConnection(Project project);
	

	/**
	 * Load the collection of reference of <b>branches</b> available on the SCM declared for the given project.
	 * @param project the given project
	 * @return a collection of GIT Ref.
	 */
	Collection<Ref> loadBranches(Project project);

	/**
	 * Retrieve the connection <i>(in any)</i> to the SCM for the current project settings
	 * @param project the current project
	 * @return the fetchConnection object representing the connection to the remote server, or {@code null} if connection fails.
	 */
	FetchConnection retrieveFetchConnection(Project project);
	
	/**
	 * <p>
	 * Create the directory which will host the local repository.
	 * </p>
	 * 
	 * <p>
	 * The current implementation in {@link GitCrawler} will provide 2 solutions :
	 * <ul>
	 * <li> a temporary directory if the setting {@code gitcrawler.repositories.location} is empty</i>
	 * <li>a permanent directory inside the given path given in {@code gitcrawler.repositories.location}</i>
	 * </ul>
	 * </p>
	 * 
	 * @param project  the actual project
	 * @param settings the connection settings <i>(these settings are given for
	 *                 trace only support)</i>
	 * @throws ApplicationException thrown if an IO exception occurs, most probably either {@link IOException}, or {@link SecurityException} 
	 * @return the resulting path
	 */
	Path createDirectoryAsCloneDestination(Project project, ConnectionSettings settings) throws ApplicationException;
	
	/**
	 * <p>
	 * <b>Clone</b> or <b>Pull</b> the source code from the remote repository
	 * </p>
	 * @param project Project whose source code files should be scan in the repository
	 * @param settings connection settings
	 * @throws IOException thrown if any application or network error occurs.
	 * @throws GitAPIException thrown if any application or network error occurs.
	 * @throws ApplicationException will be thrown by only
	 * {@link com.fitzhi.bean.ProjectHandler#saveLocationRepository } 
	 */
	void clone(Project project, ConnectionSettings settings) throws IOException, GitAPIException, ApplicationException;

	/**
	 * <p>
	 * Parse all changes from the repository history and generate (or update) the commits collection analysis.
	 * <p>
	 * <b><font color="red">BE CAUTIOUS : This method has an unsatisfying adherence with GIT</font></b>
	 * </p>
	 * @param project the given project 
	 * @param analysis the analysis container to complete. This analysis hosts the  collection of all changes detected on the passed repository.
	 * @param repository the <b><font color="red">GIT</font></b> repository.
	 * @throws ApplicationException thrown by the crawling operation.
	 */
	void fillRepositoryAnalysis(Project project, RepositoryAnalysis analysis,  Repository repository) throws ApplicationException;

	/**
	 * <p>
	 * Retrieve the commits collection analysis processed for the local repository of the given project.
	 * </p>
	 * <ul>
	 * <li>Either this method will load the generated data from the file system</li>
	 * <li>Or this method will generate the analysis data</li>
	 * </ul>
	 * &nbsp;
	 * @param project the given project 
	 * @param repository the <b><font color="red">GIT</font></b> repository.
	 * @return the {@link RepositoryAnalysis analysis} extracted from the repository. 
	 * <p>This analysis contains the  collection of all changes detected on the passed repository.</p>
	 * @throws ApplicationException thrown by the crawling operation.
	 */
	RepositoryAnalysis retrieveRepositoryAnalysis(Project project, Repository repository) throws ApplicationException;

	/**
	 * <p>
	 * Finalize the loading of the changes.<br/>bra
	 * Useless entries will be removed.
	 * </P>
	 * <p><font color="red">
	 * Some GIT rename operations might not be detected <i>(when <u>simultaneously</u> the path and the content of the file are changed</i>).<br/>
	 * We remove useless entries if the file does not exist anymore on file system.
	 * </font></p>
	 * @param sourceLocation the directory where the sources are located.
	 * @param analysis the analysis generated from the collection.
	 * @throws IOException if any IO exception occurs during the finalization.
	 */
	void finalizeListChanges(String sourceLocation, RepositoryAnalysis analysis) throws IOException;

	/**
	 * <p>
	 * Filter the collection of changes to the eligible pathnames.<br/>
	 * Theses pathnames must match the patterns declared by the settings <b>patternsCleanup<b> in your <b>application.properties</b> file.
	 * </p>
	 * @param analysis the repository analysis.
	 */
	void filterEligible(RepositoryAnalysis analysis);

	/**
	 * <p>
	 * This method remove the non relevant directories from the crawl.
	 * </p>
	 * @param project the current project
	 * @param analysis the repository analysis.
	 */
	void removeNonRelevantDirectories(Project project, RepositoryAnalysis analysis);
	
	/**
	 * <p>
	 * Update the collection changes by setting the staff identifier <b>found</b> for every entry.<br/>
	 * This method lookup in the staff team with the author declared for each commit.<br/>
	 * A set of unknown contributors is also generated.
	 * </p>
	 * @param project the current project
	 * @param analysis the repository analysis.
	 * @param unknownContributors the set of ghost, i.e. the unknown contributors
	 */
	void updateStaff(Project project, RepositoryAnalysis analysis, Set<String> unknownContributors);

	/**
	 * <p>
	 * Update the GIT changes collection by setting the importance for every path impacted by each commit.
	 * </p>
	 * @param project the current project
	 * @param analysis the repository analysis.
	 * @throws ApplicationException thrown if any exceptions occurs.
	 */
	void updateImportance(Project project, RepositoryAnalysis analysis) throws ApplicationException;	
	
	/**
	 * <p>
	 * Parse the repository <u>already</u> cloned on the file system.<br/>
	 * <b>PREREQUESIT = The repository must have be cloned before</b>
	 * </p>
	 * @param project Project whose source code files should be scan in the repository
	 * @return the parsed repository 
	 * @throws IOException thrown if any application or network error occurs.
	 * @throws ApplicationException thrown if any application or network error occurs.
	 */
	CommitRepository parseRepository(Project project) throws IOException, ApplicationException;

	/**
	 * <p>
	 * Take in account the list of files impacted by a commit.
	 * </p>
	 * 
	 * @param analysis the analysis container. 
	 * This container hosts the complete list of changes detected during the crawling repository
	 * @param commit the actual commit revision evaluated
	 * @param finalFilePathname the final file pathname as it appears now, on the local repository
	 * @param de the Diff entry to be taken in account for the pathname
	 * @param diffFormater Difference formatter which will be used to count the number of added, and deleted, lines
	 * @param parserVelocity tracker which is following the velocity of the parser 
	 * @throws ApplicationException throw if any problem occurs, most probably an {@link IOException} or an {@link CorruptObjectException}
	 */
	void processDiffEntries(RepositoryAnalysis analysis, RevCommit commit, String finalFilePathname,
			DiffEntry de, DiffFormatter diffFormatter, ParserVelocity parserVelocity) throws ApplicationException;


	/**
	 * <p>Aggregate the history of the repository into the risks dashboard.</p>
	 * <p>
	 * This method transform the commit repository obtained from GIT and represented by a flat map (one record per file),
	 * into an hierarchical representation with directory and their children (sub-directories / source files). 
	 * </p>
	 * @param project working project
	 * @param commitRepo the parsed repository history retrieved from the version control
	 * @return the dashboard of the current project.
	 */
	RiskDashboard aggregateDashboard(Project project, CommitRepository commitRepo);
		
	/**
	 * Generate and complete the dashboard generation figuring the activities of staff members for the passed project
	 * @param project the project whose source code files should be parsed in the repository
	 * @param settings the dashboard generation settings, such as :
	 * <ul>
	 * <li>filtered from a starting date,</li>
	 * <li>or filtered for a staff member.</li>
	 * </ul>
	 * @return the project risk dashboard 
	 * @throws IOException thrown if any application or network error occurs during the treatment.
	 * @throws ApplicationException thrown if any application or network error occurs during the treatment.
	 * @throws GitAPIException thrown if any application or network error occurs during the treatment.
	 */
	RiskDashboard generate(Project project, SettingsGeneration settings) throws IOException, ApplicationException, GitAPIException;

	/**
	 * This method is an ASYNCHRONOUS wrapper from the method {@link #generate(Project)}
	 * <br/>
	 * Generate and complete the dashboard generation figuring the activities of staff members for the passed project
	 * @param project the project whose source code files should be parsed in the repository
	 * @param settings the dashboard generation settings, such as :
	 * <ul>
	 * <li>filtered from a starting date,</li>
	 * <li>or filtered for a staff member.</li>
	 * </ul>
	 * @return the project risk dashboard 
	 */
	RiskDashboard generateAsync(Project project, SettingsGeneration settings);
	
	/**
	 * Personalize the commit repository on a particular staff member.
	 * @param globalRepo the global repository containing all the staff team
	 * @param settings settings for this personnalized repository
	 * @return a subset of the repository centralized on a particular developer or filtered from a starting date
	 */
	CommitRepository personalizeRepo(CommitRepository globalRepo, SettingsGeneration settings);
	
	/**
	 * Test if the staff risks dashboard generation has been already executed.
	 * @param project the selected project
	 * @return 	<ul><li>{@code true} if the intermediate data are available to complete the dashboard,</li>
	 * 			<li>{@code false} if the full operation is required <i><b>(Therefore, this operation will be asynchronous)</b></i>.</li></ul>  
	 * @throws IOException As we look for the existence of a working file on the file system, this function might return an IOException.
	 */
	boolean hasAvailableGeneration(Project project) throws IOException;
	
	/**
	 * <p>
	 * Select the list of paths (the shortest possible) containing dependency keywords such as {@code jquery}, {@code bootstrap}...<br/>
	 * The resulting list is kept inside the repositoryAnalysis container ({@link RepositoryAnalysis#getPathsCandidate()}.<br/>
	 * The crawler will verify in {@link #retrieveRootPath(List)} 
	 * if a sub-directory in the parent tree can be excluded from the analysis.<br/>
	 * </p>
	 * @param analysis the current repository container analysis.
	 * @param dependencyKeywords list of keywords which might identify the existence of an external dependency inside the repository
	 */
	void selectPathDependencies (RepositoryAnalysis analysis, List<String> dependencyKeywords);

	/**
	 * <p>
	 * Retrieve the root directory on each dependency pathnames where exclusion can start.</br>
	 * e.g. a pathname like {@code src/main/javascript/app/component/jquery/src/internal-js-.js} 
	 * will become {@code src/main/javascript/app/component/jquery}<br/><br/>
	 * <font color="red">IMPORTANT : The method tests also that ALL files on each directory are identified as dependencies</font><br/>
	 * This method will fill the dependencies collection {@link Project#getDependencies()} in the Project.
	 * </p>
	 * @param analysis the current repository container analysis.
	 * @param pathnames collection of pathnames which contains an external dependency keyword such as {@code jquery} or {@code bootstrap} 
	 * throws {@link IOException} if any IOExceptio occurs.
	 */
	void retrieveRootPath (RepositoryAnalysis analysis) throws IOException;

	/**
	 * <p>
	 * Collect the activity for each contributor by skill.
	 * </p>
	 * <p>
	 * Each contributor aside of his global statistics has a list of {@link StaffActivitySkill} figuring the activity of a developer on each skill.
	 * This list is also used to update the skills detected for a developer.
	 * </p>
	 * @param contributors the list a valid contributors whose activities have to be updated.
	 * @param changes the history of changes detected in the repository
	 * @param pathSourceFileNames the set of source filename
	 * @throws ApplicationException thrown if any problem occurs
	 */
	void gatherContributorsActivitySkill(List<Contributor> contributors, SourceControlChanges changes, Set<String> pathSourceFileNames) throws ApplicationException;
	
	/**
	 * @return the list of markers of dependencies such as {@code jquery}, {@code bootstrap}...
	 */
	List<String> dependenciesMarker();

	/**
	 * Display the configuration.
	 */
	void displayConfiguration();

	/**
	 * <p>
	 * load all Source files located in the repository directory.
	 * </p>
	 * <p>
	 * The resulting array will be filtered on an eligible filter. 
	 * <em>Only source files will be selected</em> 
	 * </p>
	 * @param project the current project
	 * @return the complete set of files, or an empty set if none exists. 
	 * @throws ApplicationException thrown if any problem occurs, most probably an IOException
	 */
	Set<String> allEligibleFiles(Project project) throws ApplicationException;
	
	/**
	 * <p>
	 * This method scans the local repository and returns the list of commits where the file has been modified, <b>and ALSO RENAMED as well</b>.
	 * </p>
	 * @param project the current projett
	 * @param repository the project repository
	 * @param filePath the file Path
	 */
	List<RevCommit> fileGitHistory(Project project, Repository repository, String filepath) throws ApplicationException;

	/**
	 * retrieve the first commit registered for the given repository.
	 * @param the "porcelain" API to interact with the git repository
	 * @return the first commit
	 */
	RevCommit initialCommit(Git git) throws ApplicationException;

	/**
	 * Retrrieve the DIFF entry for a specific file between 2 commit revisions
	 * @param pathname the pathname curently analyzed, which will be filtered for the comparaison 
	 * @param repository the GIT repository
	 * @param from the commit revision <b>FROM</b> where the diff delta has to be processed
	 * @param to the commit revision <b>TO</b> where the diff delta has to be processed
	 * @return the searched entry or {@code null} if none's found 
	 * @throws ApplicationException thrown if any exeption occurs, most probably a GIT or IO exception
	 */
	DiffEntry retrieveDiffEntry(String pathname, Repository repository, RevCommit from, RevCommit to) throws ApplicationException;

}
