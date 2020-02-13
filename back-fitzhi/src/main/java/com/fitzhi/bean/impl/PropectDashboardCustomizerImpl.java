/**
 * 
 */
package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_CONTRIBUTOR_INVALID;
import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static com.fitzhi.Error.MESSAGE_CONTRIBUTOR_INVALID;
import static com.fitzhi.Global.INTERNAL_FILE_SEPARATORCHAR;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.data.source.Operation;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.RepoScanner;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This bean is in charge of the sun-burst chart customization.<br/>
 * Operations like excluding inert library paths or on-boarding a collaborator in the project.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service
public class PropectDashboardCustomizerImpl implements ProjectDashboardCustomizer {

	/**
	 * These directories will be removed from the full path of class files<br/>
	 * e.g. <code>/src/main/java/java/util/List.java</code> will be treated
	 * like <code>java/util/List.java</code>
	 */
	@Value("${patternsCleanup}")
	private String patternsCleanup;

	/**
	 * Cleanup patterns list.
	 */
	private List<Pattern> patternsCleanupList;

	/**
	 * For retrieving data from the persistent repository.
	 */
	@Autowired
	public DataHandler dataSaver;
	
	/**
	 * For retrieving the repository from the file system.
	 */
	@Autowired
	@Qualifier("GIT")
	public RepoScanner repoScanner;
	
	/**
	 * Declared here to operate  {@link StaffHandler#lookup(String)}.
	 */
	@Autowired
	public StaffHandler staffHandler;
	
	
	/**
	 * Declared here to operate {@link CacheDataHandler#saveRepository}.
	 */
	@Autowired
	CacheDataHandler cacheDataHandler;
	
	/**
	 * Cache which contains the content of the directories paths 
	 */
	Map<Integer,List<String>> cachePaths = new WeakHashMap<>();

	@PostConstruct
	public void init() {

		patternsCleanupList = Arrays.asList(patternsCleanup.split(";")).stream().map(Pattern::compile)
				.collect(Collectors.toList());

		if (log.isDebugEnabled()) {
			log.debug("Pattern CLEANUP loaded from the file application.properties : ");
			patternsCleanupList.stream().forEach(p -> log.debug(p.pattern()));
		}
	}
	
	@Override
	public String cleanupPath(final String path) {

		String cleanupPath = "";

		for (Pattern pattern : patternsCleanupList) {
			Matcher matcher = pattern.matcher(path);
			if (matcher.find() && (cleanupPath.length() == 0)) {
				cleanupPath = path.substring(0, matcher.start() + 1) + path.substring(matcher.end());
			}
		}
		return (cleanupPath.length() == 0) ? path : cleanupPath;
	}

	
	@Override
	public List<String> lookupPathRepository(Project project, String criteria) throws SkillerException {
				
		final List<String > pathsList;
		if (!this.cachePaths.containsKey(project.getId())) {
			pathsList = dataSaver.loadRepositoryDirectories(project);
			this.cachePaths.put(project.getId(), pathsList);
			if (log.isDebugEnabled()) {
				log.debug(String.format("The repository dir paths for the project %d %s is successfully loaded", project.getId(), project.getName()));
			}
		} else {
			pathsList = this.cachePaths.get(project.getId());
		}
		
		return lookupPathRepository(pathsList, criteria);
	}
	
	@Override
	public synchronized void takeInAccountNewStaff(Project project, Staff staff) throws SkillerException {

		try {
			if (cacheDataHandler.hasCommitRepositoryAvailable(project)) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("Using cache file for project %s", project.getName()));
				}

				CommitRepository repository = cacheDataHandler.getRepository(project);
			
				// 
				if (repository != null) {
					List<String> candidates = repository.extractMatchingUnknownContributors(staffHandler, staff);
					for (String candidate : candidates) {
						if (log.isDebugEnabled()) {
							log.debug ("Registering the candidate "  + candidate);
						}
						repository.onBoardStaff(staffHandler, staff);
						repository.removeGhost(candidate);
					}
					//
					// Saving the repository :
					//  - Unknown contributors who are identifier now, should disappear from the ghosts list
					//  - The new staff identifier should have been propagated to the associated operations
					//
					cacheDataHandler.saveRepository(project, repository);
					
					if (log.isDebugEnabled()) {
						log.debug("Involving the staff " + staff.fullName() + " inside the project " + project.getName());
					}
					Contributor contributor = repository.extractContribution(staff);
					if (contributor != null) {
						staffHandler.involve(project, contributor);
					} else { 
						throw new SkillerException (
							CODE_CONTRIBUTOR_INVALID,
							MessageFormat.format(MESSAGE_CONTRIBUTOR_INVALID, staff.fullName(), project.getName()));
					}
				}
			}
		} catch (final IOException ioe) {
			throw new SkillerException(CODE_IO_EXCEPTION, ioe.getLocalizedMessage(), ioe);
		}
	}
	
	/**
	 * <p>
	 * Remove duplicate entries from the collection of operations.<br/>
	 * Duplicate entries are entries with the same staff identifier and the same date of commit.
	 * </p>
	 * @param operations the collection of operations to proceed
	 */
	public static void removeDuplicateEntries(List<Operation> operations) {
		
		
		Map<String, Long> counted = operations.stream()
	            .collect(Collectors.groupingBy(Operation::generateIdDataKey, Collectors.counting()));

		for (String key : counted.keySet()) {
		
			int count = counted.get(key).intValue();
			if (count == 1) {
				continue;
			}
			
			int posSeparator = key.indexOf("@");
			int idStaff = Integer.valueOf(key.substring(0, posSeparator));
			LocalDate localDate = LocalDate.parse(key.substring(posSeparator+1));
			removeDuplicateEntries(operations, idStaff, localDate, count-1);
		}
	}

	/**
	 * Removing duplicate operations with the same identifier and date of commit.
	 * @param operations the operations to be cleanup.
	 * @param idStaff the given staff identifier
	 * @param dateCommit the given date of commit.
	 * @param deletionsNumberToDo the number of deletion to do.
	 */
	public static void removeDuplicateEntries(List<Operation> operations, int idStaff, LocalDate dateCommit, int deletionsNumberToDo) {
		int deletionExecuted = 0;
		Iterator<Operation> iteOperations = operations.iterator();
		while (iteOperations.hasNext()) {
			Operation operation = iteOperations.next();
			if ((operation.getIdStaff() == idStaff) && (operation.getDateCommit().equals(dateCommit))) {
				iteOperations.remove();
				if (++deletionExecuted == deletionsNumberToDo) {
					break;
				}
			}
		}
	}
	
	/**
	 * <p>Lookup paths matching the passed criteria.</p>
	 * @param pathsList the complete pathsList of the repository
	 * @param criteria the search criteria
	 * @return he list of pathnames matching the criteria
	 */
	List<String> lookupPathRepository(List<String> pathsList, String criteria)  {

		return pathsList.stream()
			.map(this::cleanupPath)
			.map(String::toLowerCase)
			.filter(s -> s.startsWith(criteria.toLowerCase()))
			.map (s -> this.extractPath(s, criteria.length()))
			.distinct()
			.collect(Collectors.toList());
	}
	
	/**
	 * Extract the end of path starting from the length of the criteria.
	 * @param path the given path
	 * @param criteriaLength the length
	 * @return the extracted patch 
	 */
	private String extractPath (String path, int criteriaLength) {
		int nextSeparatorChar = path.indexOf(INTERNAL_FILE_SEPARATORCHAR, criteriaLength);
		if (nextSeparatorChar == -1) {
			return path;
		} else {
			return path.substring(0, nextSeparatorChar);
		}
	}
	
}
