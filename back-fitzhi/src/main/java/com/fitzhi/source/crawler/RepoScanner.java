/**
 * 
 */
package com.fitzhi.source.crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import com.fitzhi.controller.in.SettingsGeneration;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.RiskDashboard;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.StaffActivitySkill;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.ConnectionSettings;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.git.GitCrawler;

/**
 * <p>
 * Source repository scanner.
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
	 * @throws SkillerException thrown most probably, if the project ghosts list update failed.
	 */
	CommitRepository loadRepositoryFromCacheIfAny(Project project) throws IOException, SkillerException;

	/**
	 * Test if the connection to the SCM will succeed with the actual settings
	 * @param project the current project
	 * @return {@code true} if the connection has succeeded, {@code false} otherwise
	 */
	boolean testConnection(Project project);
	
	
	/**
	 * <p>
	 * Create a directory which will be the destination of the clone process.
	 * <br/>
	 * <font color="darkGreen" size="4">
	 * The first implementation in {@link GitCrawler} will use the {@code temp directory}  of the filesystem.
	 * </font>
	 * </p>
	 * 
	 * @param project  the actual project
	 * @param settings the connection settings <i>(these settings are given for
	 *                 trace only support)</i>
	 * @throws IOException an IO oops ! occurs. Too bad!
	 * @return the resulting path
	 */
	Path createDirectoryAsCloneDestination(Project project, ConnectionSettings settings) throws IOException;
	
	
	/**
	 * <p>
	 * <b>Clone</b> or <b>Pull</b> the source code from the remote repository
	 * </p>
	 * @param project Project whose source code files should be scan in the repository
	 * @param settings connection settings
	 * @throws IOException thrown if any application or network error occurs.
	 * @throws GitAPIException thrown if any application or network error occurs.
	 * @throws GitAPIException thrown if any application or network error occurs.
	 * @throws SkillerException will be thrown by only
	 * {@link com.fitzhi.bean.ProjectHandler#saveLocationRepository } 
	 */
	void clone(Project project, ConnectionSettings settings) throws IOException, GitAPIException, SkillerException;

	/**
	 * <p>Extract all changes from the repository and generate the commits collection.
	 * <p>
	 * <b><font color="red">BE CAUTIOUS : This method has an unsatisfying adherence with GIT</font></b>
	 * </p>
	 * @param repository the <b><font color="red">GIT</font></b> repository.
	 * @return the analysis extracted from the repository. This analysis contains the  collection with all changed detected on the passed repository.
	 * @throws SkillerException thrown by the crawling operation.
	 */
	RepositoryAnalysis loadChanges(Project project, Repository repository) throws SkillerException;

	/**
	 * <p>
	 * Finalize the loading of the changes.<br/>
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
	 * @throws SkillerException thrown if any exceptions occurs.
	 */
	void updateImportance(Project project, RepositoryAnalysis analysis) throws SkillerException;	
	
	/**
	 * <p>
	 * Parse the repository <u>already</u> cloned on the file system.<br/>
	 * <b>PREREQUESIT = The repository must have be cloned before</b>
	 * </p>
	 * @param project Project whose source code files should be scan in the repository
	 * @return the parsed repository 
	 * @throws IOException thrown if any application or network error occurs.
	 * @throws SkillerException thrown if any application or network error occurs.
	 */
	CommitRepository parseRepository(Project project) throws IOException, SkillerException;

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
	 * @throws SkillerException thrown if any application or network error occurs during the treatment.
	 * @throws GitAPIException thrown if any application or network error occurs during the treatment.
	 */
	RiskDashboard generate(Project project, SettingsGeneration settings) throws IOException, SkillerException, GitAPIException;

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
	 * @throws SkillerException thrown if any problem occurs
	 */
	void gatherContributorsActivitySkill(List<Contributor> contributors, SourceControlChanges changes, Set<String> pathSourceFileNames) throws SkillerException;
	
	/**
	 * @return the list of markers of dependencies such as {@code jquery}, {@code bootstrap}...
	 */
	List<String> dependenciesMarker();

	/**
	 * Display the configuration.
	 */
	void displayConfiguration();
}
