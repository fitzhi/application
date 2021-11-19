package com.fitzhi.data.source;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Staff;


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
	 * @param authorName the author's name
	 * @param timestamp date of the operation
	 * @param importance the importance of this source file in the project in a numeric value format
	 */
	void addCommit(String sourceCodePath, int idStaff, String authorName, LocalDate timestamp, long importance);
	
	/**
	 * <p>
	 * Add a new commit log into the repository.<br/>
	 * This method calls addCommit with the date only.
	 * </p>
	 * @param sourceCodePath the complete source code path 
	 * @param idStaff staff member's identifier
	 * @param authorName the author's name
	 * @param timestamp date of the operation (without time offset)
	 * @param importance the importance of this source file in the project in a numeric value format
	 */
	void addCommit(String sourceCodePath, int idStaff, String authorName, Date timestamp, long importance);
	
	/**
	 * On-board a staff member into the repository to replace unknown contributors.
	 * @param staffHandler the bean injected to manage the staff.
	 * @param staff the actual staff member
	 */
	void onBoardStaff(StaffHandler staffHandler, Staff staff);
	
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
	LocalDate getLastDateCommit(String sourceCodePath, int idStaff);
	
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
	 * associated with its history of {@link com.fitzhi.data.source.CommitHistory CommitHistory}.
	 */
	Map<String, CommitHistory> getRepository();
	
	/**
	 * @param idStaff developer's identifier
	 * @return the date/time of the <b>LAST</b> submission for the passed developer 
	 */
	LocalDate lastCommit(int idStaff);

	/**
	 * @param idStaff developer' identifier
	 * @return the date/time of the <b>FIRST</b> submission for the passed developer 
	 */
	LocalDate firstCommit(int idStaff);
	
	/**
	 * <p>
	 * This function does not return the number of commits.
	 * this is this the sum of ( the sum of files per commit ) for all commits  
	 * </p>
	 * @param idStaff developer's identifier
	 * @return the number of unit of commits submitted by the passed developer for each file.
	 */
	int numberOfFileCommits(int idStaff);
	
	/**
	 * @param idStaff developer identifier
	 * @return the number of files submitted by the passed developer
	 */
	int numberOfFiles(int idStaff);
	
	/**
	 * This set contains : 
	 * <ul>
	 * <li>either unknown developers, who must be declared inside the application (staff Form)</li>
	 * <li>or particular nicknames setup by current developers.</li>
	 * </ul>
	 * @return A set which contains the developers/contributors retrieved in the version control but unrecognized during the parsing process.<br/>
	 */
	Set<String> unknownContributors();

	/**
	 * Set a new contributors set in this repository.
	 * @param unknowns a set of committers pseudos retrieved in the version control but unrecognized in the staff member.<br/>
	 */
	void setUnknownContributors(Set<String> unknowns);
	
	/**
	 * <p>
	 * Extract the unknown contributors who match the staff member record.
	 * </p>
	 * @param staffHandler interface in charge of the Staff management, bean passed by the caller service
	 * @param staff the given staff
	 * @return a list of unknown contributors with whom the staff record is matching
	 */
	List<String> extractMatchingUnknownContributors(StaffHandler staffHandler, Staff staff);
	
	/**
	 * <p>
	 * Removing the ghost from the unknown contributors list.
	 * </p>
	 * @param unknownContributor the unknown contributor
	 */
	void removeGhost(String unknownContributor);

	/**
	 * <p>
	 * Parse and extract the contributor's data for the given staff member.
	 * </p>
	 * @param staff the given staff member.
	 * @return the contributor object summarizing his participation in the project, 
	 * {@code null} if not data was found for the given staff member.<br/>
	 * 
	 * @see BasicCommitRepository#extract(Object, java.util.function.BiFunction, java.util.function.Function)
	 */
	Contributor extractStaffMetrics(Staff staff);
	
	/**
	 * <p>
	 * Parse and extract the contributor's data for the given ghost.
	 * </p>
	 * @param author the given author.
	 * @return the contributor object summarizing his participation in the project, 
	 * {@code null} if no data was found for the given author.<br/>
	 * 
	 * @see BasicCommitRepository#extract(Object, java.util.function.BiFunction, java.util.function.Function)
	 */
	Ghost extractGhostMetrics(Author author);

	/**
	 * Dump the content of the repository.
	 */
	public void dump();

}
		
