/**
 * 
 */
package fr.skiller.data.source;

import static fr.skiller.Global.LN;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Repository containing time-stamped commits for the given project.
 * <br/>
 * <i>This is the first & basic implementation for the Commit repository</i>.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class BasicCommitRepository implements CommitRepository {

	Map<String, CommitHistory> repo = new HashMap<>();
	
	/**
	 * This set contains the developers/contributors retrieved in the repository 
	 * but unrecognized during the parsing process.
	 */
	Set<String> unknownContributors = new HashSet<>();
	
	@Override
	public void addCommit(String sourceCodePath, int idStaff, LocalDate dateCommit, long importance) {
		
		if (repo.containsKey(sourceCodePath)) {
			final CommitHistory history = repo.get(sourceCodePath);
			history.handle(idStaff, dateCommit);
		} else {
			CommitHistory fileLogs = new CommitHistory(sourceCodePath, importance);
			fileLogs.addOperation(new Operation(idStaff, dateCommit));
			repo.put(sourceCodePath, fileLogs);
		}
	}
	
	@Override
	public void addCommit(String sourceCodePath, int idStaff, Date dateCommit, long importance) {
		addCommit(sourceCodePath, idStaff, dateCommit.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), importance);
	}
	
	@Override
	public boolean containsSourceCode(String sourceCodePath) {
		return repo.containsKey(sourceCodePath);
	}

	@Override
	public LocalDate getLastDateCommit(final String sourceCodePath, final int author) {
		if (repo.containsKey(sourceCodePath)) {
			final CommitHistory history = repo.get(sourceCodePath);
			return history.getDateCommit(author);
		} else {
			return null;
		}
	}

	@Override
	public String extractCSV() {
		final StringBuilder sb = new StringBuilder();
		repo.values().stream().forEach(history -> {
			history.operations.stream().forEach(
					operation -> sb.append(history.sourcePath)
					.append(";")
					.append(operation.idStaff)
					.append(";")
					.append(operation.getDateCommit())
					.append(LN)
					);
		});
		return sb.toString();
	}

	@Override
	public int size() {
		return repo.size();
	}

	@Override
	public Map<String, CommitHistory> getRepository() {
		return this.repo;
	}

	/**
	 * Working variable used by method lastCommit
	 */
	private LocalDate lastCommit;
	
	@Override
	public LocalDate lastCommit (final int idStaff) {
		lastCommit = LocalDate.MIN; 
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.filter(ope -> (ope.idStaff == idStaff)).forEach(ope -> {
				if (ope.getDateCommit().isAfter (lastCommit)) {
					lastCommit = ope.getDateCommit();
				}
			}));
		return lastCommit;
	}

	@Override
	public LocalDate firstCommit (final int idStaff) {
		lastCommit = LocalDate.MAX; 
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.filter(ope -> (ope.idStaff == idStaff)).forEach(ope -> {
				if (ope.getDateCommit().isBefore(lastCommit)) {
					lastCommit = ope.getDateCommit();
				}
			}));
		return lastCommit;
	}
	
	@Override
	public int numberOfFileCommits (final int idStaff) {
		return (int) this.repo.values().stream()
				.mapToLong( 
					 history -> history.operations.stream()
					.filter(ope -> (ope.idStaff == idStaff))
					.count())
				.asDoubleStream()
				.sum();
	}
	
	
	@Override
	public int numberOfFiles (final int idStaff) {
		return (int) this.repo.values()
				.stream()
				.filter(history -> history.hasWorkedOnThisFile(idStaff))
				.count();
	}
	
	@Override
	public Set<String> unknownContributors() {
		return this.unknownContributors;
	}

	@Override
	public void setUnknownContributors(Set<String> unknowns) {
		this.unknownContributors = unknowns;
	}
}
