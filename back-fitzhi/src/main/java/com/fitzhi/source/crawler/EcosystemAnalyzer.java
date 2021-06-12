package com.fitzhi.source.crawler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.Project;
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
	 * Load the ecosystems declared inside the application.
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
	 * Update the experience of the developers who are involved in a project.
	 *
	 * @param project the given project to be evaluated
	 * @param 
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	void updateStaffExperience(Project project, ExperienceParser ...detectors) throws ApplicationException;

}
