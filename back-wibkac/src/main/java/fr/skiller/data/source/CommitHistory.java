/**
 * 
 */
package fr.skiller.data.source;

import static fr.skiller.Global.UNKNOWN;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.StaffHandler;

/**
 * History of operations of a source element.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class CommitHistory {
	
	/**
	 * complete source path.
	 */
	public final String sourcePath;
	
	/**
	 * Level of risk on this source file. <br/>
	 * This risk is evaluate by {@link fr.skiller.source.crawler.RepoScanner#evaluateTheRisk(CommitRepository)}
	 */
	int riskLevel;
	
	/**
	 * A numeric value representing the importance of the source file impacted by the change.
	 */
	private long importance;
	
	/**
	 * All operations that occur on the source file.
	 */
	public final List<Operation> operations = new ArrayList<>();

	/**
	 * Constructor of commit history.
	 * @param sourcePath the source path which history has to be kept
	 * @param importance the importance for this file with the project
	 */
	public CommitHistory(String sourcePath, long importance) {
		this.sourcePath = sourcePath;
		this.importance = importance;
	}

	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @return the importance
	 */
	public long getImportance() {
		return importance;
	}

	/**
	 * @param importance the importance to set
	 */
	public void setImportance(long importance) {
		this.importance = importance;
	}

	/**
	 * Add a new operation inside the collection
	 * @param operation the new operation to be added
	 * @return the updated collection
	 */
	public List<Operation> addOperation (final Operation operation) {
		operations.add(operation);
		return operations;
	}

	/**
	 * Take in account a new commit in the history.
	 * @param idStaff Staff member's identifier
	 * @param authorName the author's name
	 * @param timestamp the time-stamp of this operation
	 * @return the updated collection
	 */
	public List<Operation> handle(final int idStaff, String authorName, final LocalDate timestamp) {
		return addOperation (new Operation(idStaff, authorName, timestamp));
	}
	
	/**
	 * Returns the registered date of commit for an author.
	 * @param idStaff Staff member's identifier
	 * @return the date of commit, or <code>Null</code> if none exists.
	 */
	public LocalDate getDateCommit(final int idStaff) {
		Optional<Operation> opt = operations.stream().filter(ope -> ope.idStaff == idStaff).findFirst();
		if (opt.isPresent()) {
			return opt.get().getDateCommit();
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
	public LocalDate evaluateDateLastestCommit() {
		return operations.stream()
				.map(Operation::getDateCommit)
				.max( Comparator.comparing( LocalDate::toEpochDay ))
				.orElse(null);
	}

	/**
	 * return all staff identifiers involved in the edition of this location
	 * @return the array of staff identifiers who collaborate on this source file
	 */
	public int[] committers() {
		return operations.stream()
				.map(operation->operation.idStaff)
				.distinct()
				.mapToInt(Number::intValue)
			    .toArray();
	}

	/**
	 * Count the total number of commits submitted for this file.
	 * @return the count involved in this file
	 */
	public long countCommits() {
		return operations.size();
	}
	
	/**
	 * Count the total number of submissions delivered by active developers for this file.
	 * @param staffHandler handler to get information on staff member, such as active, or inactive.
	 * @return submission made by active developers 
	 */
	public long countCommitsByActiveDevelopers(final StaffHandler staffHandler) {
		return operations
				.stream()
				.filter(ope -> ope.idStaff != UNKNOWN)
				.mapToInt(ope->ope.idStaff)
				.filter (staffHandler::isActive)
				.count();
	}
	
	/**
	 * Count the total number of different developers who have work on this file
	 * @return developers count involved in this file
	 */
	public long countDistinctDevelopers() {
		return operations
			.stream()
			.filter(ope -> ope.idStaff != UNKNOWN)
			.mapToInt(ope->ope.idStaff)
			.distinct()
			.count();
	}
	
	/**
	 * Count the total number of different <u><b>ACTIVE</b></u> developers who have work on this file
	 * @param staffHandler handler to request information on staff member, such as active, or inactive.
	 * @return the count number in a {@code long} format
	 */
	public long countDistinctActiveDevelopers(final StaffHandler staffHandler) {
		return operations
			.stream()
			.filter(ope -> ope.idStaff != UNKNOWN)
			.mapToInt(ope->ope.idStaff)
			.distinct()
			.filter (staffHandler::isActive)
			.count();
	}	
	
	/**
	 * Ultimate contributor having worked on this file.
	 * @return if staff's identifier of the last contributor
	 */
	public int ultimateContributor() {
		final Optional<Operation> lastOpe = operations.stream()
		.sorted((ope1, ope2) -> ope2.getDateCommit().compareTo(ope1.getDateCommit()))
		.findFirst();

		if (!lastOpe.isPresent()) {
			throw new SkillerRuntimeException("SEVERE INTERNAL ERROR : Should not pass here!");
		}
		return lastOpe.get().idStaff;
	}
	
	/**
	 * @param idStaff the developer identifier
	 * @return {@code true} if the passed developer has worked on this file, {@code false} otherwise
	 */
	public boolean hasWorkedOnThisFile (int idStaff) {
		return operations.stream().filter(ope -> ope.idStaff == idStaff).findAny().isPresent();
	}
	
}
