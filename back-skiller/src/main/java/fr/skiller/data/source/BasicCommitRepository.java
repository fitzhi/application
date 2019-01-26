/**
 * 
 */
package fr.skiller.data.source;

import static fr.skiller.Global.LN;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * repository containing timestamped commits for the given project.
 * <br/><i>This is the first & basic implementation for the Commit repository</i>.
 */
public class BasicCommitRepository implements CommitRepository {

	Map<String, CommitHistory> repo = new HashMap<String, CommitHistory>();
	
	/**
	 * This set contains the developers/contributors retrieved in the repository but unrecognized during the parsing process.
	 */
	Set<String> unknownContributors = new HashSet<String>();
	
	@Override
	public void addCommit(final String sourceCodePath, final int idStaff, final Date dateCommit) {
		
		if (repo.containsKey(sourceCodePath)) {
			final CommitHistory history = repo.get(sourceCodePath);
			history.handle(idStaff, dateCommit);
		} else {
			CommitHistory fileLogs = new CommitHistory(sourceCodePath);
			fileLogs.addOperation(new Operation(idStaff, dateCommit));
			repo.put(sourceCodePath, fileLogs);
		}
	}

	@Override
	public boolean containsSourceCode(String sourceCodePath) {
		return repo.containsKey(sourceCodePath);
	}

	@Override
	public Date getLastDateCommit(final String sourceCodePath, final int author) {
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
					.append(operation.dateCommit)
					.append(LN)
					);
		});;
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
	private Date lastCommit;
	
	@Override
	public Date lastCommit (final int idStaff) {
		lastCommit = new Date(Long.MIN_VALUE);
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.filter(ope -> (ope.idStaff == idStaff)).forEach(ope -> {
				if (ope.dateCommit.after(lastCommit)) {
					lastCommit = ope.dateCommit;
				}
			}));
		return lastCommit;
	}

	@Override
	public Date firstCommit (final int idStaff) {
		lastCommit = new Date(Long.MAX_VALUE);
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.filter(ope -> (ope.idStaff == idStaff)).forEach(ope -> {
				if (ope.dateCommit.before(lastCommit)) {
					lastCommit = ope.dateCommit;
				}
			}));
		return lastCommit;
	}
	
	@Override
	public int numberOfCommits (final int idStaff) {
		return (int) this.repo.values().stream().mapToLong( 
				history -> history.operations.stream().filter(ope -> (ope.idStaff == idStaff)).count()).asDoubleStream().sum();
	}
	
	
	@Override
	public int numberOfFiles (final int idStaff) {
		return (int) this.repo.values()
				.stream()
				.filter(history -> history.hasWorkedOnThisFile(idStaff))
				.count();
	}
	
	@Override
	public List<Contributor> contributors() {
		Set<Integer> idContributors = new HashSet<Integer>();
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.map(ope -> ope.idStaff).distinct().forEach(idContributors::add));
		
		List<Contributor> contributors = new ArrayList<Contributor>();
		for (int idStaff : idContributors) {
			contributors.add (new Contributor(idStaff, firstCommit (idStaff), lastCommit (idStaff), numberOfCommits(idStaff), numberOfFiles(idStaff)));
		}
		
		return contributors;
	}

	@Override
	public Set<String> unknownContributors() {
		return this.unknownContributors;
	}

	
}
