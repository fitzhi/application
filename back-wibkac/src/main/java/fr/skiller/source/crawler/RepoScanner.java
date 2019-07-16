/**
 * 
 */
package fr.skiller.source.crawler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import fr.skiller.controller.ProjectController.SettingsGeneration;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.RepositoryAnalysis;
import fr.skiller.data.internal.RiskDashboard;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.data.source.ConnectionSettings;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Source repository scanner.
 */
public interface RepoScanner {

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
	 * {@link fr.skiller.bean.ProjectHandler#saveLocationRepository } 
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
	public void filterEligible(RepositoryAnalysis analysis);

	/**
	 * <p>
	 * Cleanup the pathnames of the changes collection.<br/>
	 * For example : <code>/src/main/java/java/util/List.java</code> will be treated like <code>java/util/List.java</code>
	 * </p>
	 * @param analysis the repository analysis.
	 */
	void cleanupPaths(RepositoryAnalysis analysis);
	
	/**
	 * <p>
	 * This method remove the non relevant directories from the crawl.
	 * </p>
	 * @param project the current project
	 * @param analysis the repository analysis.
	 */
	public void removeNonRelevantDirectories(Project project, RepositoryAnalysis analysis);
	
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
	 * Gather the identified contributors with their personal stats.
	 * @param analysis the repository analysis.
	 * @return the list of contributors involved in the repository
	 */
	List<Contributor> gatherContributors(RepositoryAnalysis analysis);

	/**
	 * Parse the repository <u>already</u> cloned on the file system.<br/>
	 * <b>PREREQUESIT = The repository must have be cloned before</b>
	 * @param project Project whose source code files should be scan in the repository
	 * @param settings connection settings
	 * @return the parsed repository 
	 * @throws IOException thrown if any application or network error occurs.
	 * @throws SkillerException thrown if any application or network error occurs.
	 */
	CommitRepository parseRepository(Project project, ConnectionSettings settings) throws IOException, SkillerException;

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
	 * @throws SkillerException thrown if any application or network error occurs during the treatment.
	 * @throws GitAPIException thrown if any application or network error occurs during the treatment.
	 * @throws IOException thrown if any application or network error occurs during the treatment.
	 */
	RiskDashboard generateAsync(Project project, SettingsGeneration settings) throws SkillerException, GitAPIException, IOException;
	
	/**
	 * Personalize the commit repository on a particular staff member.
	 * @param globalRepo the global repository containing all the staff team
	 * @param settings settings for this personnalized repository
	 * @return a subset of the repository centralized on a particular developer or filtered from a starting date
	 */
	CommitRepository personalizeRepo(CommitRepository globalRepo, SettingsGeneration settings);
	
	/**
	 * Test if risks dashboard have been executed.
	 * @param project the selected project
	 * @return 	{@code true} if the intermediate data are available to complete the dashboard, 
	 * 			{@code false} if the complete operation is required (Therefore, this operation will be asynchronous).  
	 * @throws IOException
	 */
	boolean hasAvailableGeneration(Project project) throws IOException;
	
 	/**
 	 * Extract from the filename path the non relevant directory (such as {@code /src/main/java}) <br/>
 	 * <i>(This method is public for testing purpose.)</i>
 	 * @param path the given path
 	 * @return the cleanup path
 	 */
	String cleanupPath (String path);

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
	 * @return the list of markers of dependencies such as {@code jquery}, {@code bootstrap}...
	 */
	List<String> dependenciesMarker();

}
