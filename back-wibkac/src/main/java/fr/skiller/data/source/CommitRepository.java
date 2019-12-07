package fr.skiller.data.source;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Staff;


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
	 * associated with its history of {@link fr.skiller.data.source.CommitHistory CommitHistory}.
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
	 * @return A set which contains the developers/contributors retrieved in the version control but unrecognized during the parsing process.<br/>
	 * <p>
	 * This set contains : 
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
	
	/**
	 * Extract the unknown contributors who match the staff member.
	 * @param staffHandler injected bean in charge of the Staff management 
	 * @param staff the given staff
	 * @return a list of unknown contributors whit whom the passed staff is matching
	 */
	List<String> extractMatchingUnknownContributors(StaffHandler staffHandler, Staff staff);
	

	/**
	 * Dump the content of the repository.
	 */
	public void dump();

}
		
