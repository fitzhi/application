package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;

import fr.skiller.source.crawler.git.SCMChange;

/**
 * <p> 
 * 	Analysis of the repository
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class RepositoryAnalysis {

	private final List<SCMChange> changes = new ArrayList<>();

	
	/**
	 * Add a change in the collection.
	 * @param change the active change .
	 * @return <code>boolean</code> if this collection changed as a result of the call
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(SCMChange e) {
		return changes.add(e);
	}


	/**
	 * @return the number of changes recorded in the analysis
	 * @see java.util.List#size()
	 */
	public int size() {
		return changes.size();
	}


	/**
	 * @return the changes collection
	 */
	public List<SCMChange> getChanges() {
		return changes;
	}
	
}
