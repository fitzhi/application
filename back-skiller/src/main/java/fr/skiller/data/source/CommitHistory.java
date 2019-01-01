/**
 * 
 */
package fr.skiller.data.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.config.YamlProcessor.MatchStatus;

/**
 * History of operations of a source element.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class CommitHistory {
	
	/**
	 * complete source path.
	 */
	public final String sourcePath;
	
	
	
	final List<Operation> lastestOperations = new ArrayList<Operation>();

	/**
	 * @param sourcePath
	 * @param lastestOperations
	 */
	public CommitHistory(String sourcePath) {
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
	 * @param idStaff Staff member's identifier
	 * @param timestamp the timestamp of this operation
	 * @return the updated collection
	 */
	public List<Operation> handle(final int idStaff, final Date timestamp) {
		Optional<Operation> opt = lastestOperations.stream().filter(ope -> ope.idStaff == idStaff).findFirst();
		if (opt.isPresent()) {
			if (opt.get().dateCommit.before(timestamp)) {
				opt.get().dateCommit = timestamp;
			}
			return lastestOperations;
		} else {
			return addOperation (new Operation(idStaff, timestamp));
		}
	}
	
	/**
	 * Returns the registered date of commit for an author.
	 * @param idStaff Staff member's identifier
	 * @return the date of commit, or <code>Null</code> if none exists.
	 */
	public Date getDateCommit(final int idStaff) {
		Optional<Operation> opt = lastestOperations.stream().filter(ope -> ope.idStaff == idStaff).findFirst();
		if (opt.isPresent()) {
			return opt.get().dateCommit;
		} else {
			return null;
		}
	}
	
	/**
	 * Calculate and return the date for the last modification of this source file.<br/>
	 * This date is evaluated by parsing the list of operations on this element.
	 * @return the date of the latest commit
	 */
	//TODO Filter this date on the active staff members.
	public Date evaluateDateLastestCommit() {
		return lastestOperations.stream().map(operation->operation.dateCommit).max(Date::compareTo).get();
	}
	
}
