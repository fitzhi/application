package com.fitzhi.source.crawler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fitzhi.data.internal.Ecosystem;
import com.fitzhi.data.internal.ExperienceAbacus;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.TypeCode;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
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
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 */
	Map<Integer, Ecosystem> loadEcosystems() throws ApplicationException;
		
	/**
	 * Load the experiences abacus declared in the application.
	 * @return the abacus of experiences
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 */
	List<ExperienceAbacus> loadExperienceAbacus() throws ApplicationException;

	/**
	 * Scan the repository and detect the ecosystems .
	 * @param pathnames the set of pathnames retrieved in the repository
	 * @return the list of detected ecosystem in the repository
	 * @throws ApplicationException thrown if any problem occurs
	 */
	List<Ecosystem> detectEcosystems(Set<String> pathnames) throws ApplicationException;

	/**
	 * Load the experience (skill/level) detection templates declared in the application.
	 * @return the map of detection templates
	 * @throws ApplicationException thrown if any problem occurs
	 */
	Map<Integer, ExperienceDetectionTemplate> loadExperienceDetectionTemplates() throws ApplicationException;

	/**
	 * Load the experience (skill/level) detection templates declared in the application
	 * filtered by a {@link TypeCode type of code} and (facultative) a list of skills.
	 * @param typeCode the given type of code to filter the detection templates.
	 * @param skills the list of skills to filter the result.
	 * If this list is{@code null}all records will be returned.
	 * @return the filtered map of detection templates. This map is empty if all templates are evicted.
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
	 * @param projectDetectedExperiences the {@link ProjectDetectedExperiences container} of all the experiences detected in the portfolio
	 * @param parsers List of detectors to be processed on this project
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	void loadDetectedExperiences(Project project, ProjectDetectedExperiences projectDetectedExperiences, ExperienceParser ...parsers) throws ApplicationException;

	/**
	 * Update a collection of detected experiences of detected authors 
	 * @param project the given project
	 * @param skills the skills detectable with their filename patterns <em>(such as '.java$')</em>
	 * @param changes the changes loaded for this project
	 * @param experiences the experiences collection to be loaded with this project.
	 * @throws ApplicationException throw if any proiblem occurs
	 */
	void calculateExperiences(Project project, List<Skill> skills, SourceControlChanges changes, ProjectDetectedExperiences experiences) throws ApplicationException;
}
