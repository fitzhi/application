/**
 * 
 */
package fr.skiller.data.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class CommitsHistory {
	
	final String sourcePath;
	
	final List<Operation> lastestOperations = new ArrayList<Operation>();

	/**
	 * @param sourcePath
	 * @param lastestOperations
	 */
	public CommitsHistory(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * Add a new operation inside the collection
	 * @param operation the new operation to be added
	 * @return the updated collection
	 */
	public List<Operation> addOperation (final Operation operation) {
		lastestOperations.add(operation);
		return lastestOperations;
	}

	/**
	 * Take in account a new commit in the history.
	 * @param author the login of the committer
	 * @param email the email of the committer
	 * @param timestamp the timestamp of this operation
	 * @return the updated collection
	 */
	public List<Operation> handle(final String author, final String email, final Date timestamp) {
		Optional<Operation> opt = lastestOperations.stream().filter(ope -> ope.login.equals(author)).findFirst();
		if (opt.isPresent()) {
			if (opt.get().dateCommit.before(timestamp)) {
				opt.get().dateCommit = timestamp;
			}
			return lastestOperations;
		} else {
			return addOperation (new Operation(author, email, timestamp));
		}
	}
	
	/**
	 * Returns the registered date of commit for an author.
	 * @param author the given author
	 * @return the date of commit, or <code>Null</code> if none exists.
	 */
	public Date getDateCommit(final String author) {
		Optional<Operation> opt = lastestOperations.stream().filter(ope -> ope.login.equals(author)).findFirst();
		if (opt.isPresent()) {
			return opt.get().dateCommit;
		} else {
			return null;
		}
			
		
	}
}
