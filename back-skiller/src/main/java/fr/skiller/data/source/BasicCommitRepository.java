/**
 * 
 */
package fr.skiller.data.source;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.skiller.Global.LN;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * repository containing timestamped commits for the given project.
 * <br/><i>This is the first & basic implementation for the Commit repository</i>.
 */
public class BasicCommitRepository implements CommitRepository {

	Map<String, CommitHistory> repo = new HashMap<String, CommitHistory>();
	
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

	
	@Override
	public Set<Integer> contributors() {
		Set<Integer> contributors = new HashSet<Integer>();
		this.repo.values().stream().forEach(history -> 
			history.operations.stream()
			.map(ope -> ope.idStaff).distinct().forEach(contributors::add));
		return contributors;
	}

}
