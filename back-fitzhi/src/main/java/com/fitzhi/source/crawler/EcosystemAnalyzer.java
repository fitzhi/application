package com.fitzhi.source.crawler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.TypeCode;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.MapDetectedExperiences;
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
	 * Load the experience (skill/level) detection templates declared in the application
	 * filtered by a {@link TypeCode type of code} and (facultative) a list of skills.
	 * @param typeCode the given type of code to filter the detection templates.
	 * @param skills the list of skills to filter the result.
	 * If this list is{@code null}all records will be returned.
	 * @return the resulting list of detection templates
	 * @throws ApplicationException thrown if any problem occurs
	 */
	Map<Integer, ExperienceDetectionTemplate> loadExperienceDetectionTemplates(
		@NotNull TypeCode typeCode, List<Skill> skills) throws ApplicationException;

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
	 * Detect the experiences available in the Git repository.
	 *
	 * @param project the given project to be evaluated
	 * @param parsers List of detectors to be processed on this project
	 * @return The initialized {@Link MapDetectedExperiences map of detectedExperiences} loaded from the project
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	MapDetectedExperiences loadDetectedExperiences(Project project, ExperienceParser ...parsers) throws ApplicationException;

	/**
	 * Update a collection of detected experiences of detected authors 
	 * @param project the given project
	 * @param skills the skills detectable with their filename patterns <em>(such as '.java$')</em>
	 * @param changes the changes loaded for this project
	 * @param experiences the experiences collection to be loaded with this project.
	 * @throws ApplicationException throw if any proiblem occurs
	 */
	void calculateExperiences(Project project, List<Skill> skills, SourceControlChanges changes, MapDetectedExperiences experiences) throws ApplicationException;
}
