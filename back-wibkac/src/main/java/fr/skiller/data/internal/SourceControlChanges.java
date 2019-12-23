package fr.skiller.data.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.skiller.source.crawler.git.SourceChange;
import fr.skiller.source.crawler.git.SourceFileHistory;

/**
 * <p>
 * Object containing all source control changes detected in the Source repository.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class SourceControlChanges {

	/**
	 * Map of all changes detected on the repository, indexed by file path.
	 */
	Map<String, SourceFileHistory> mapChanges;
	
	
	/**
	 * Empty constructor without parameters.
	 */
	public SourceControlChanges() {
		this(new HashMap<>());
	}
	
	/**
	 * Simple constructor just with a collection already loaded.
	 * @param mapChanges the history of changes for all files retrieved from the repository.
	 */
	public SourceControlChanges(Map<String, SourceFileHistory> mapChanges) {
		this.mapChanges = mapChanges;
	}
	
	/**
	 * Add a change in the history of a file.
	 * @param fullPath the source file path
	 * @param change the new given change
	 * @return {@code true} (as specified by {@link Collection#add  Collection.add})
	 */
	public boolean addChange(final String fullPath, SourceChange change) {
		if (!mapChanges.containsKey(fullPath)) {
			mapChanges.put(fullPath, new SourceFileHistory());
		}
		return mapChanges.get(fullPath).getChanges().add(change);
	}

	/**
	 * @return the a set containing all file paths detected during the source crawl.
	 * @see {@link Map#keySet()}
	 */
	public Set<String> keySet() {
		return mapChanges.keySet();
	}

	/**
	 * Test the presence of a file path.
	 * @param filePath the file Path
	 * @return {@code true} id the given path is present in the changes collection
	 */
	public boolean containsFilePath(String filePath) {
		return mapChanges.containsKey(filePath);
	}

	/**
	 * @return the changes map available for streaming  operations.
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<String, SourceFileHistory>> entrySet() {
		return mapChanges.entrySet();
	}
	
	/**
	 * @param path the path of the source filename.
	 * @return the source file history associated of the given path.
	 */
	public SourceFileHistory  getSourceFileHistory(String path) {
		return mapChanges.get(path);
	}

	/**
	 * Remove a file path from history.
	 * @param key the path
	 * @return the previous {@link SourceFileHistory history} associated with this path, or {@code null} if there was no mapping for this path.
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public SourceFileHistory remove(Object key) {
		return mapChanges.remove(key);
	}
	
}
