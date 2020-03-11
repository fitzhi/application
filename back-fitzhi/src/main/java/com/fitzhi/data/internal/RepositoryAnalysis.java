package com.fitzhi.data.internal;

import static com.fitzhi.Error.SHOULD_NOT_PASS_HERE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;

/**
 * <p> 
 * The class hosts the intermediate data gathered from the analysis of the repository.<br/>
 * Multiple sets are updated and available in this container :
 * <ul>
 * <li>{@link SourceControlChanges list of changes} retrieved from the repository.</i>
 * <li>list of paths detected as having been modified in the history of the repository.</i>
 * <li>list of paths detected as having been ONLY ADDED in the history of the repository.</li>
 * </ul>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class RepositoryAnalysis {

	final Project project;
	
	/**
	 * list of changes retrieved from the repository.
	 */
	private SourceControlChanges changes = new SourceControlChanges();

	/**
	 * List of paths detected as having been modified in the history of the repository.<br/>
	 * This list is used to retrieve, by subtraction, i.e. the files never modified, the list of files just <b>added</b>, maybe <u>external files include.</u> 
	 * These files might be external dependencies, non relevant for audit.  
	 */
	private final Set<String> pathsModified = new HashSet<>();
	
	/**
	 * List of paths detected as having been ONLY ADDED in the history of the repository.<br/>
	 * These files might be external dependencies files, i.e. non relevant for audit.  
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
	 * @return the changes loaded by the analysis.
	 */
	public SourceControlChanges getChanges() {
		return changes;
	}
	
	/**
	 * Set the new changes collection for this repository.
	 * @param changes the new changes collection
	 */
	public void setChanges(SourceControlChanges changes) {
		this.changes = changes;
	}
	
	/**
	 * Get the number of files revealed by the repository scan.
	 * @return the number of file
	 */
	public int numberOfFiles() {
		return changes.keySet().size();
	}
	
	/**
	 * Get the total number of changes on all files revealed by the repository scan.
	 * @return the number of file
	 */
	public int numberOfChanges() {
		return changes.mapChanges.values()
			.stream()
			.mapToInt(history -> history.getChanges().size())
			.sum();
	}
	
	/**
	 * Test if the given file is present in the repository history.
	 * @param filePath the given file path
	 * @return {@code true} is the file is present, {@code false} otherwise.
	 */
	public boolean containsFile(String filePath) {
		return this.changes.containsFilePath(filePath);
	}

	/**
	 * @return all paths with activity discovered during the analysis. 
	 */
	public Set<String> getPathsAll() {
		return this.changes.keySet();
	}
	
	/**
	 * <p><big><b>Why do we keep a set of paths modified ?</b></big></p>
	 * We keep the paths which have been modified in order to retrieve by subtraction from the complete list, 
	 * the paths which only have be added without modification. <br/>
	 * The resulting list will contain good candidates for external dependencies, irrelevant for the analysis.
	 * @return set of paths modified
	 */
	public Set<String> getPathsModified() {
		return pathsModified;
	}

	/**
	 * Save the given path as a modified path.
	 * @see {@link #getPathsModified()}
	 */
	public void keepPathModified(String path) {
		pathsModified.add(path);
	}
	
	/**
	 * <p>
	 * This method generate a set of file paths never modified in the history of the repository<br/>
	 * They might be good candidates for being external dependencies.
	 * </p>
	 */
	public void extractCandidateForDependencies() {
		changes.keySet().stream()
			.filter(s -> !pathsModified.contains(s))
			.forEach(pathsAdded::add);
	}
	
	/**
	 * Add a change in the collection.
	 * @param fullPath the complete path of the source file
	 * @param change the activity change .
	 * @return <code>boolean</code> if this collection changed as a result of the call
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addChange(String fullPath, SourceChange change) {
		return changes.addChange(fullPath, change);
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
	 * @return an iterator scanning the set of file paths which is indexing the repository history.
	 */
	public Iterator<String> iteratorFilePath() {
		return changes.mapChanges.keySet().iterator();
	}
	
	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * <p>
	 * Take in account the fact that a file has been renamed.<br/>
	 * All records with the old path will be renamed to the new one.
	 * </p>
	 * 
	 * @param newPath    the new file path
	 * @param oldPath    the old file path
	 */
	public void renameFilePath(String newPath, String oldPath) {
		SourceFileHistory history =  this.changes.mapChanges.get(oldPath);

		//
		// We do not throw an exception if the oldPath does not exist.
		// It might have been created by a 'COPY' operation
		//
		// Previous error :
		//    String.format("The pass %s is supposed to already exist, to be replaced by %s", oldPath, newPath)
		// 
		
		if (history != null) {
			this.changes.mapChanges.remove(oldPath);
			this.changes.mapChanges.put(newPath, history);
		}
	}

	/**
	 * <p>
	 * A file has been deleted on the repository.
	 * We remove it.
	 * </p>
	 * 
	 * @param path    the file path to delete
	 */
	public void removeFilePath(String path) {
		this.changes.mapChanges.remove(path);
	}
	
	/**
	 * <p>
	 * Transfer the changes collection into the commitRepository
	 * </p>
	 * <P>
	 * The changes map is saving the commits activity into a flat mode, 
	 * when the resulting commitRepository is saving it into a hierarchical way (in a file system manner).
	 * </p>
	 * @param commitRepository the analysis data
	 */
	
	public void transferRepository (CommitRepository commitRepository) {
		// We iterate on the file recorded
		changes.mapChanges.keySet().stream().forEach(path -> {
			// We iterate on changes detected on each file.
			changes.mapChanges.get(path).getChanges().stream().forEach(change -> {
				commitRepository.addCommit(
						path,
						change.isIdentified() ? change.getIdStaff() : com.fitzhi.Global.UNKNOWN, 
						change.getAuthorName(),
						change.getDateCommit(),
						changes.mapChanges.get(path).getImportance());
			});
		});
	}
	
	/**
	 * Gather a distinct list of <u>identified</u> contributors.<br/>
	 * The returned list contains <b>staff identifier</b>.
	 * @return the list of registered contributors involved in the project
	 */
	public Set<Integer> contributors() {
		return changes.mapChanges.values()
			.stream()
			.flatMap(history -> history.getChanges().stream())
			.map(SourceChange::getIdStaff)
			.filter(idStaff -> idStaff != 0)
			.distinct()
			.collect(Collectors.toSet());
	}
	
	/**
	 * Gather a distinct list of authors.<br/>
	 * The returned list contains distinct contributor login (identified or not inside techxhì)
	 * @return the list of contributors involved in the project
	 */
	public List<String> authors() {
		return changes.mapChanges.values()
			.stream()
			.flatMap(history -> history.getChanges().stream())
			.filter(SourceChange::isAuthorIdentified)
			.map(SourceChange::getAuthorName)
			.distinct()
			.collect(Collectors.toList());
	}
	
	/**
	 * Update the staff identifier corresponding to the author name.
	 * @param authorName the author name
	 * @param idStaff the staff identifier
	 */
	public void updateStaff(String authorName, int idStaff) {
		changes.mapChanges.values()
			.stream()
			.flatMap(history -> history.getChanges().stream())
			.filter(sc -> authorName.equals(sc.getAuthorName()))
			.forEach(sc ->  sc.setIdStaff(idStaff));
	}
	
	/**
	 * Crawl within the history the retrieve the <b>FIRST</b> commit of a staff member
	 * @param idStaff the staff member identifier
	 * @return
	 */
	public LocalDate retrieveFirstCommit(int idStaff) {
		return changes.mapChanges.values()
				.stream()
				.flatMap(history -> history.getChanges().stream())				
				.filter(change -> idStaff == change.getIdStaff())
				.map(SourceChange::getDateCommit)
				.min(Comparator.comparing(LocalDate::toEpochDay))
				.orElseThrow(() -> new SkillerRuntimeException(SHOULD_NOT_PASS_HERE));
		
	}

	/**
	 * Crawl within the history the retrieve the <b>LAST</b> commit of a staff member
	 * @param idStaff the staff member identifier
	 * @return
	 */
	public LocalDate retrieveLastCommit(int idStaff) {
		return changes.mapChanges.values()
				.stream()
				.flatMap(history -> history.getChanges().stream())				
				.filter(change -> idStaff == change.getIdStaff())
				.map(SourceChange::getDateCommit)
				.max(Comparator.comparing(LocalDate::toEpochDay))
				.orElseThrow(() -> new SkillerRuntimeException(SHOULD_NOT_PASS_HERE));
	}


	/**
	 * @param idStaff the staff identifier
	 * @return the number of commits submitted by a staff member
	 */
	public int numberOfCommits(int idStaff) {
		return (int) changes.mapChanges.values()
				.stream()
				.flatMap(history -> history.getChanges().stream())				
				.filter(change -> idStaff == change.getIdStaff())
				.map(SourceChange::getCommitId)
				.distinct()
				.count();
	}
	
	/**
	 * @param idStaff the staff identifier
	 * @return the number of files modified by a staff member
	 */
	public int numberOfFiles(int idStaff) {
		return (int) changes.mapChanges.keySet()
				.stream()
				.filter(path -> changes.getSourceFileHistory(path).isInvolved(idStaff))	
				.count();
	}
	
	/**
	 * Set the importance of a file
	 * @param path the pathname of the file
	 * @param importance the new file to be set
	 */
	public void setFileImportance(String path, int importance) {
		if (!this.changes.mapChanges.containsKey(path)) {
			throw new SkillerRuntimeException("SHOULD NOT PASS HERE : an entry should exist for key " + path);
		}
		this.changes.mapChanges.get(path).setImportance(importance);
	}

	
	/**
	 * Gather the identified contributors with their personal statistics, such as
	 * <ul>
	 * <li>the date of their first & last commit</li>
	 * <li>the number of files impacted</li>
	 * <li>the number of commits submitted</li>
	 * </ul>
	 * @return the list of contributors involved in the repository
	 */
	public List<Contributor> gatherContributors() {
		
		//
		// We gather all staff identifiers in a set.
		//
		Set<Integer> idContributors = this.contributors();

		List<Contributor> contributors = new ArrayList<>();
		for (int idStaff : idContributors) {

			//
			// We process the date of the FIRST commit submitted by this staff member
			//
			LocalDate firstCommit = retrieveFirstCommit(idStaff);
			
			//
			// We process the date of the LAST commit submitted by this staff member
			//
			LocalDate lastCommit = retrieveLastCommit(idStaff);

			//
			// Number of commits submitted by this given staff member
			//
			int numberOfCommits = numberOfCommits(idStaff);

			//
			// Number of files touched by this given staff member
			//
			int numberOfFiles = numberOfFiles(idStaff);
			

			contributors
					.add(new Contributor(idStaff, firstCommit, lastCommit, numberOfCommits, numberOfFiles));
		}

		return contributors;
		
	}

	
	/**
	 * <p>
	 * Cleanup the pathnames of the changes collection.<br/>
	 * e.g. <code>/src/main/java/java/util/List.java</code> will be treated like <code>java/util/List.java</code>
	 * </p>
	 * @param analysis the repository analysis.
	 */
	public void cleanupPaths(ProjectDashboardCustomizer projectDashboardCustomizer) {
		
		Map<String, SourceFileHistory> cleanPathChanges = new HashMap<>();
		
		this
			.getChanges()
			.entrySet()
			.stream()
			.forEach(entry -> cleanPathChanges.put(
				projectDashboardCustomizer.cleanupPath(entry.getKey()), 
				entry.getValue())
			);
		
		this.setChanges(new SourceControlChanges(cleanPathChanges));
	}

}
