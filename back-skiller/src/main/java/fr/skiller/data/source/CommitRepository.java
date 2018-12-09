package fr.skiller.data.source;

import java.util.Date;

public interface CommitRepository {

	/**
	 * add a new commit log into the repository.
	 * @param sourceCodePath the complete source code path 
	 * @param author the login of the committer
	 * @param timestamp date of the operation
	 */
	void addCommit(String sourceCodePath, String author, Date timestamp);
	
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
}
