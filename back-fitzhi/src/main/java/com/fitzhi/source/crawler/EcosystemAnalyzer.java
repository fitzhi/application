package com.fitzhi.source.crawler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.javaparser.ExperienceParser;

/**
 * <p>
 * Interface in charge of analysis the repository in order 
 * <ul>
 * <li>to update the ecosystem associated with the project,</li>
 * <li>to update the experiences of developers involed in the project</li>
 * </ul>
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface EcosystemAnalyzer {

	/**
	 * Load the ecosystems declared in the application.
	 * @return the list of ecosystems
	 * @throws ApplicationException thrown if any problem occurs
	 */
	Map<Integer, Ecosystem> loadEcosystems() throws ApplicationException;
		
	/**
	 * Scan the repository and detect the ecosystems .
	 * @param pathnames the set of pathnames retrieved in the repository
	 * @return the list of detected ecosystem in the repository
	 * @throws ApplicationException thrown if any problem occurs
	 */
	List<Ecosystem> detectEcosystems(Set<String> pathnames) throws ApplicationException;

	/**
	 * Load the experience (skill/level) detection templates declared in the application.
	 * @return the list of detection templates
	 * @throws ApplicationException thrown if any problem occurs
	 */
	Map<Integer, ExperienceDetectionTemplate> loadExperienceDetectionTemplates() throws ApplicationException;

	/**
	 * <p>
	 * Initialize the array of parsers for this file pattern (such as {@code .java$}).
	 * </p>
	 * @param project the given working project
	 * @param filePattern the file pattern from which we retrieve the available parsers
	 * @return the resulting array of {@link ExperienceParser parsers}
	 * @throws ApplicationException thrown if any problem occurs
	 */
	ExperienceParser[] loadExperienceParsers(Project project, String filePattern) throws ApplicationException;

	/**
	 * Update the experience of the developers who are involved in a project.
	 *
	 * @param project the given project to be evaluated
	 * @param detectors List of detectors to be processed on this project
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	void updateStaffExperience(Project project, ExperienceParser ...detectors) throws ApplicationException;

}
