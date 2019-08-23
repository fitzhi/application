/**
 *
 */
package fr.skiller.bean.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectDashboardCustomizer;
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;
import static fr.skiller.Global.INTERNAL_FILE_SEPARATORCHAR;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
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
	 * logger
	 */
	Logger logger = LoggerFactory.getLogger(PropectDashboardCustomizerImpl.class.getCanonicalName());

	/**
	 * For retrieving data from the persistent repository.
	 */
	@Autowired
	public DataSaver dataSaver;

	/**
	 * Cache which contains the content of the directories paths
	 */
	Map<Integer,List<String>> cachePaths = new WeakHashMap<>();

	@PostConstruct
	public void init() {

		patternsCleanupList = Arrays.asList(patternsCleanup.split(";")).stream().map(Pattern::compile)
				.collect(Collectors.toList());

		if (logger.isDebugEnabled()) {
			logger.debug("Pattern CLEANUP loaded from the file application.properties : ");
			patternsCleanupList.stream().forEach(p -> logger.debug(p.pattern()));
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
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("The repository dir paths for the project %d %s is successfully loaded", project.getId(), project.getName()));
			}
		} else {
			pathsList = this.cachePaths.get(project.getId());
		}

		return lookupPathRepository(pathsList, criteria);
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

	private String extractPath (String path, int criteriaLength) {
		int nextSeparatorChar = path.indexOf(INTERNAL_FILE_SEPARATORCHAR, criteriaLength);
		if (nextSeparatorChar == -1) {
			return path;
		} else {
			return path.substring(0, nextSeparatorChar);
		}
	}

}
