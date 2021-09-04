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

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.source.crawler.git.SourceChange;
import com.fitzhi.source.crawler.git.SourceFileHistory;

import lombok.Data;

/**
 * <p> 
 * The class hosts the intermediate data gathered from the analysis of the repository.<br/>
 * Multiple sets are updated and available in this container :
 * <ul>
 * <li>{@link RepositoryAnalysis#changes} of {@link SourceControlChanges}, list of changes detected in the repository.</i>
 * <li>{@link RepositoryAnalysis#pathsModified }, list of paths detected as having been modified in the history of the repository.</i>
 * <li>{@link RepositoryAnalysis#pathsAdded }, list of paths detected as having been ONLY ADDED in the history of the repository.</li>
 * </ul>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Data
public class RepositoryAnalysis {

	final Project project;
	
	/**
	 * list of changes retrieved from the repository.
	 */
	private SourceControlChanges changes = new SourceControlChanges();

	/**
	 * <p>
	 * List of paths detected as having been modified in the history of the repository.
	 * </p>
	 * This list is used to retrieve, by subtraction, i.e. the files never modified, the list of files just <b>added</b>, maybe <u>external files include.</u> 
	 * These files might be external dependencies, non relevant for audit.  
	 */
	private Set<String> pathsModified = new HashSet<>();
	
	/**
	 * List of paths detected as having been ONLY ADDED in the history of the repository.<br/>
	 * These files might be external dependencies files, i.e. non relevant for audit.  
	 */
	private Set<String> pathsAdded = new HashSet<>();
	
	/**
	 * Path candidates possibly non relevant for the analysis.
	 */
	private Set<String> pathsCandidate = new HashSet<>();
	
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
		return changes.getChanges().values()
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
	 * Add a change into the collection.
	 * @param fullPath the complete path of the source file
	 * @param change the activity change .
	 * @return <code>boolean</code> if this collection changed as a result of the call
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean takeChangeInAccount(String fullPath, SourceChange change) {
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
		return changes.getChanges().keySet().iterator();
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

		SourceFileHistory history =  this.changes.getChanges().get(oldPath);

		//
		// We do not throw an exception if the oldPath does not exist.
		// It might have been created by a 'COPY' operation
		//
		// Previous error :
		//    String.format("The pass %s is supposed to already exist, to be replaced by %s", oldPath, newPath)
		// 
		
		if (history != null) {
			this.changes.getChanges().remove(oldPath);
			this.changes.getChanges().put(newPath, history);
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
		this.changes.getChanges().remove(path);
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
		changes.getChanges().keySet().stream().forEach(path -> {
			// We iterate on changes detected on each file.
			changes.getChanges().get(path).getChanges().stream().forEach(change -> {
				commitRepository.addCommit(
						path,
						change.isIdentified() ? change.getIdStaff() : com.fitzhi.Global.UNKNOWN, 
						change.getAuthor().getName(),
						change.getDateCommit(),
						changes.getChanges().get(path).getImportance());
			});
		});
	}
	
	/**
	 * <p>
	 * Gather a distinct list of <em>identified</em> contributors.
	 * </p>
	 * <p>
	 * The returned list contains <b>staff identifier</b>.
	 * </p>
	 * @return the list of registered contributors involved in the project
	 */
	public Set<Integer> contributors() {
		return changes.getChanges().values()
			.stream()
			.flatMap(history -> history.getChanges().stream())
			.map(SourceChange::getIdStaff)
			.filter(idStaff -> idStaff != 0)
			.distinct()
			.collect(Collectors.toSet());
	}
	
	/**
	 * <p>
	 * Gather a distinct list of authors.
	 * </p>
	 * <p>
	 * The returned list contains distinct contributor login (identified or not inside Fitzhì)
	 * </p>
	 * @return the list of contributors involved in the project
	 */
	public List<Author> authors() {
		return changes.getChanges()
			.values()
			.stream()
			.flatMap(history -> history.getChanges()
			.stream())
			.filter(SourceChange::isAuthorIdentified)
			.map(SourceChange::getAuthor)
			.distinct()
			.collect(Collectors.toList());
	}
	
	/**
	 * Update the staff identifier corresponding to the author name.
	 * @param authorName the author name
	 * @param idStaff the staff identifier
	 */
	public void updateStaff(String authorName, int idStaff) {
		changes.getChanges().values()
			.stream()
			.flatMap(history -> history.getChanges().stream())
			.filter(sc -> authorName.equals(sc.getAuthor().getName()))
			.forEach(sc ->  sc.setIdStaff(idStaff));
	}
	
	/**
	 * Collect and filter the project global changes for a given staff member
	 * @param idStaff the staff identifier
	 * @return the list of changes for a staff member
	 */
	public List<SourceChange> getPersonalChange(int idStaff) {
		return changes.getChanges().values()
				.stream()
				.flatMap(history -> history.getChanges().stream())				
				.filter(change -> idStaff == change.getIdStaff())
				.collect(Collectors.toList());
	}
	
	/**
	 * Crawl within the changes history in order the retrieve the <b>FIRST</b> commit of a staff member
 	 * @param changes a list of changes through which to collect the first commit.
	 * @return the first commit 
	 */
	public LocalDate retrieveFirstCommit(List<SourceChange> changes) {
		return changes.stream()
				.map(SourceChange::getDateCommit)
				.min(Comparator.comparing(LocalDate::toEpochDay))
				.orElseThrow(() -> new ApplicationRuntimeException(SHOULD_NOT_PASS_HERE));
		
	}

	/**
	 * Crawl within the changes history in order the retrieve the <b>LAST</b> commit of a staff member
	 * @param changes a list of changes through which to collect the <b>LAST</b> commit.
	 * @return the last commit 
	 */
	public LocalDate retrieveLastCommit(List<SourceChange> changes) {
		return changes.stream()
				.map(SourceChange::getDateCommit)
				.max(Comparator.comparing(LocalDate::toEpochDay))
				.orElseThrow(() -> new ApplicationRuntimeException(SHOULD_NOT_PASS_HERE));
	}


	/**
	 * @param changes a stream of changes through which to c.
	 * @return the number of commits submitted by a staff member
	 */
	public int numberOfCommits(List<SourceChange> changes) {
		return (int) changes.stream()
				.map(SourceChange::getCommitId)
				.distinct()
				.count();
	}
	
	/**
	 * @param idStaff the staff identifier
	 * @return the number of files <b>modified</b> by a staff member
	 */
	public int numberOfFiles(int idStaff) {
		// We crawl within the "mapChanges" and not within the changes because we want to aggregate the number of files MODIFIED.
		return (int) changes.getChanges().keySet()
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
		if (!this.changes.getChanges().containsKey(path)) {
			throw new ApplicationRuntimeException("SHOULD NOT PASS HERE : an entry should exist for key " + path);
		}
		this.changes.getChanges().get(path).setImportance(importance);
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
			// We filter the changes data for a given staff member
			//
			List<SourceChange> personalChanges = this.getPersonalChange(idStaff);
			
			//
			// We process the date of the FIRST commit submitted by this staff member
			//
			LocalDate firstCommit = retrieveFirstCommit(personalChanges);
			
			//
			// We process the date of the LAST commit submitted by this staff member
			//
			LocalDate lastCommit = retrieveLastCommit(personalChanges);

			//
			// Number of commits submitted by this given staff member
			//
			int numberOfCommits = numberOfCommits(personalChanges);

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
