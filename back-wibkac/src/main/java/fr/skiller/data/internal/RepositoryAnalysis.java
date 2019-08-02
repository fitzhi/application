package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.skiller.source.crawler.git.SCMChange;

/**
 * <p> 
 * The class hosts the information gather from the analysis of the repository.<br/>
 * 
 * 
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class RepositoryAnalysis {

	final Project project;
	
	/**
	 * list of changes retrieved from the repository.
	 */
	private final List<SCMChange> changes = new ArrayList<>();

	/**
	 * List of paths detected as having been modified in the history of the repository.<br/>
	 * This list is used to retrieve, by subtraction, i.e. the files never modified, the list of files just <b>added</b>, maybe <u>external files include.</u> 
	 * These files might be external dependencies, non relevant for audit.  
	 */
	private final Set<String> pathsModified = new HashSet<>();
	
	/**
	 * List of paths detected as having been ONLY ADDED in the history of the repository.<br/>
	 * These files might be external dependencies files, non relevant for audit.  
	 */
	private final Set<String> pathsAdded = new HashSet<>();
	
	/**
	 * Path candidates possibly non relevant for the analysis.
	 */
	private final Set<String> pathsCandidate = new HashSet<>();
	
	/**
	 * @param project current project analyzed
	 */
	public RepositoryAnalysis(Project project) {
		super();
		this.project = project;
	}

	/**
	 * <p>
	 * Why do we keep a set of paths modified ?<br/>
	 * We keep the paths which have been modified in order to retrieve by subtraction from the complete list, 
	 * the paths which only have be added without modification. <br/>
	 * The resulting list will contain good candidates for external dependencies, irrelevant for the analysis.
	 * </p>
	 * @return set of paths modified
	 */
	public Set<String> getPathsModified() {
		return pathsModified;
	}

	/**
	 * <p>
	 * This method generate a set of file paths never modified in the history of the repository<br/>
	 * They might be good candidates for being external dependencies.
	 * </p>
	 */
	public void extractCandidateForDependencies() {
		changes.stream()
				.map(SCMChange::getPath)
				.distinct()
				.filter(s -> !pathsModified.contains(s))
				.forEach(pathsAdded::add);
	}
	
	/**
	 * Add a change in the collection.
	 * @param change the active change .
	 * @return <code>boolean</code> if this collection changed as a result of the call
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addChange(SCMChange e) {
		return changes.add(e);
	}

	/**
	 * @return the number of changes recorded in the analysis
	 * @see java.util.List#size()
	 */
	public int sizeChanges() {
		return changes.size();
	}

	/**
	 * @return the changes collection
	 */
	public List<SCMChange> getChanges() {
		return changes;
	}

	/**
	 * @return a set of paths without commit history of MODIFICATION. They only have been ADDED.
	 */
	public Set<String> getPathsAdded() {
		return this.pathsAdded;
	}
	
	/**
	 * Path candidates possibly non relevant for the analysis.
	 * @return the pathsCandidate
	 */
	public Set<String> getPathsCandidate() {
		return pathsCandidate;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param filename the given <b>relative</b> filename
	 * @return {@code true} if this file has been catched by the crawler.
	 */
	public boolean isCatchedFile (String filename) {
		return this.changes.stream()
				.map(SCMChange::getPath)
				.anyMatch(filename::equals);
	}
}
