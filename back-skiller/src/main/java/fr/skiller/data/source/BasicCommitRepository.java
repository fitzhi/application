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

	Map<String, CommitsHistory> repo = new HashMap<String, CommitsHistory>();
	
	@Override
	public void addCommit(String sourceCodePath, String user, Date dateCommit) {
		if (repo.containsKey(sourceCodePath)) {
			final CommitsHistory history = repo.get(sourceCodePath);
			history.handle(user, dateCommit);
		} else {
			CommitsHistory fileLogs = new CommitsHistory(sourceCodePath);
			fileLogs.addOperation(new Operation(user, dateCommit));
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
			final CommitsHistory history = repo.get(sourceCodePath);
			return history.getDateCommit(author);
		} else {
			return null;
		}
	}
	
	
}
