/**
 * 
 */
package fr.skiller.data.source;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * repository containing timestamped commits for the given project.
 * <br/><i>This is the first & basic implementation for the Commit repository</i>.
 */
public class BasicCommitRepository implements CommitRepository {

	Map<String, CommitHistory> repo = new HashMap<String, CommitHistory>();
	
	final String LN = System.getProperty("line.separator");
	
	@Override
	public void addCommit(String sourceCodePath, String user, String email, Date dateCommit) {
		if (repo.containsKey(sourceCodePath)) {
			final CommitHistory history = repo.get(sourceCodePath);
			history.handle(user, email, dateCommit);
		} else {
			CommitHistory fileLogs = new CommitHistory(sourceCodePath);
			fileLogs.addOperation(new Operation(user, email, dateCommit));
			repo.put(sourceCodePath, fileLogs);
		}
	}

	@Override
	public boolean containsSourceCode(String sourceCodePath) {
		return repo.containsKey(sourceCodePath);
	}

	@Override
	public Date getLastDateCommit(String sourceCodePath, String author) {
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
			history.lastestOperations.stream().forEach(
					operation -> sb.append(history.sourcePath)
					.append(";")
					.append(operation.login)
					.append(";")
					.append(operation.email)
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
	
}
