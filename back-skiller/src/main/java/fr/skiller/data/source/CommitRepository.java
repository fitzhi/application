package fr.skiller.data.source;

import java.util.Date;
import java.util.Map;

/**
 * Interface in charge of handling the history of commits for a repository.<br/>
 * <code>BasicCommitRepository</code> based on a GIT repository is the actual implementation.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface CommitRepository {

	/**
	 * add a new commit log into the repository.
	 * @param sourceCodePath the complete source code path 
	 * @param author the login of the committer
	 * @param email the email of the committer
	 * @param timestamp date of the operation
	 */
	void addCommit(String sourceCodePath, String author, String email, Date timestamp);
	
	/**
	 * Test the presence of a record for the given source code file
	 * @param sourceCodePath the source code path
	 * @return <code>TRUE</code> if the given source code exists in the repository
	 */
	boolean containsSourceCode(String sourceCodePath);
	
	/**
	 * Search for the last commit, if any, registered for this author and this code file.
	 * @param sourceCodePath the code file 
	 * @param author the author, responsible of the commit
	 * @return date of last commit, or <code>Null</code> if none exixts.
	 */
	Date getLastDateCommit(String sourceCodePath, String author);
	
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
}
