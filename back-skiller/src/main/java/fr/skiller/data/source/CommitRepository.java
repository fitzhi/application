package fr.skiller.data.source;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.skiller.data.internal.RiskChartData;

/**
 * Interface in charge of handling the history of commits for a repository.<br/>
 * <code>BasicCommitRepository</code> based on a GIT repository is the actual implementation.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface CommitRepository {

	/**
	 * add a new commit log into the repository.
	 * @param sourceCodePath the complete source code path 
	 * @param idStaff staff member's identifier
	 * @param timestamp date of the operation
	 */
	void addCommit(String sourceCodePath, int idStaff, Date timestamp);
	
	/**
	 * Test the presence of a record for the given source code file
	 * @param sourceCodePath the source code path
	 * @return <code>TRUE</code> if the given source code exists in the repository
	 */
	boolean containsSourceCode(String sourceCodePath);
	
	/**
	 * Search for the last commit, if any, registered for this author and this code file.
	 * @param sourceCodePath the code file 
	 * @param idStaff commit author, represented by a staff member's identifier
	 * @return date of last commit, or <code>Null</code> if none exists.
	 */
	Date getLastDateCommit(String sourceCodePath, int idStaff);
	
	/**
	 * Extract the repository in a CSV format.
	 * @return a String containing the repository.
	 */
	String extractCSV();
	
	/**
	 * @return the size of the collection.
	 */
	int size();
	
	/**
	 * @return the complete repository extracted as a Map, indexed by the complete source filename 
	 * associated with its history of {@link fr.skiller.data.source.CommitHistory CommitHistory}.
	 */
	Map<String, CommitHistory> getRepository();
	
	/**
	 * @return all contributors having worked on this repository and their statistics of use
	 */
	List<Contributor> contributors();

	/**
	 * @param idStaff developer's identifier
	 * @return the date/time of the <b>LAST</b> submission for the passed developer 
	 */
	Date lastCommit(int idStaff);

	/**
	 * @param idStaff developer' identifier
	 * @return the date/time of the <b>FIRST</b> submission for the passed developer 
	 */
	Date firstCommit(int idStaff);
	
	/**
	 * @param idStaff developer identifier
	 * @return the number of commits submitted by the passed developer
	 */
	int numberOfCommits(int idStaff);
	
	/**
	 * @param idStaff developer identifier
	 * @return the number of files submitted by the passed developer
	 */
	int numberOfFiles(int idStaff);
	
	/**
	 * @return A set which contains the developers/contributors retrieved in the version control but unrecognized during the parsing process.<br/>
	 * <p>This set contains : 
	 * <ul>
	 * <li>either unknown developers, who must be declared inside the application (staff Form)</li>
	 * <li>or particular nicknames setup by current developers.</li>
	 * </ul>
	 * </p> 
	 */
	Set<String> unknownContributors();

	/**
	 * Set a new contributors set in this repository.
	 * @param unknowns a set of committers pseudos retrieved in the version control but unrecognized in the staff member.<br/>
	 */
	void setUnknownContributors(Set<String> unknowns);
	
}
