package com.tixhi.bean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.tixhi.data.internal.FilesStats;
import com.tixhi.data.internal.Ghost;
import com.tixhi.data.internal.Library;
import com.tixhi.data.internal.Project;
import com.tixhi.data.internal.ProjectSonarMetricValue;
import com.tixhi.data.internal.Skill;
import com.tixhi.data.internal.SonarEvaluation;
import com.tixhi.data.internal.SonarProject;
import com.tixhi.data.source.Contributor;
import com.tixhi.exception.SkillerException;

public interface ProjectHandler extends DataSaverLifeCycle {

	/**
	 * @return the complete collection of projects.
	 * @throws SkillerException
	 *             thrown most probably if an IO exception occurs
	 */
	Map<Integer, Project> getProjects() throws SkillerException;

	/**
	 * Search for a project associated to the passed name.
	 * 
	 * @param projectName
	 * @throws SkillerException
	 *             thrown most probably if an IO exception occurs
	 * @return
	 */
	Optional<Project> lookup(String projectName) throws SkillerException;

	/**
	 * Retrieve a project.
	 * 
	 * @param idProject
	 *            project identifier
	 * @return a project present in the projects repository or <code>NULL</code>
	 *         if none exists for this id
	 * @throws SkillerException
	 *             thrown most probably if an IO exception occurs
	 */
	Project get(int idProject) throws SkillerException;

	/**
	 * <p>
	 * Initialize the content of the in-memory memories.
	 * </p>
	 * <i>This method exists only for testing purpose</i>
	 */
	void init();

	/**
	 * @param idProject
	 *            the project identifier.
	 * @return the list of contributor for the given project.
	 */
	List<Contributor> contributors(int idProject);

	/**
	 * Add a new project inside the projects referential.<br/>
	 * If project is not identified (e.g. the {@link Project#getId()} is less than 1), we will generate an ID.
	 * @param project the passed project
	 * @return the newly created project
	 */
	Project addNewProject(Project project) throws SkillerException;

	/**
	 * @param idProject
	 *            the passed project identifier
	 * @return {@code true} if the project identifier exists, {@code false}
	 *         otherwise
	 */
	boolean containsProject(int idProject) throws SkillerException;

	/**
	 * @param project the project to save.
	 * @throws SkillerException thrown if any saving or encryption error occurs
	 */
	void saveProject(Project project) throws SkillerException;

	/**
	 * Save the list of library detected or declared inside the project.
	 * @param idProject the identifier of the project
	 * @param libraries the list of library
	 * @return the former list (if any) 
	 * @exception SkillerException thrown if any exception occurs (such as project does not exist)
	 */
	List<Library> saveLibraries (int idProject, List<Library> libraries) throws SkillerException;
	
	/**
	 * Retrieve a ghost in the project, if any.
	 * 
	 * @param project
	 *            the current project
	 * @param pseudo
	 *            the searched pseudo
	 * @param the
	 *            corresponding ghost entry in the project, if any, with the
	 *            same pseudo, otherwise, this method will return {@code null}
	 */
	Ghost getGhost(Project project, String pseudo);

	/**
	 * <p>Save the path location where the remote branch(master) has been cloned/pulled</p>
	 * @param idProject the project identifier
	 * @param pathLocation the path location
	 * @throws SkillerException thrown if any problem, 
	 * most probably either an {@link IOException} or the <i>project does not exist</i>.
	 */
	void saveLocationRepository (int idProject, String location) throws SkillerException;
	
	/**
	 * <p>Save the staff evaluation processed for this project.</p>
	 * @param project the given project.
	 * @param staffEvaluation the calculated risk to set.
	 */
	void saveRisk(Project project, int staffEvaluation);

	/**
	 * <p>Add the passed Skill inside the project.</p>
	 * @param project the given project.
	 * @param skill the passed new skill to add in the project.
	 */
	void addSkill(Project project, Skill skill);

	/**
	 * <p>Remove a skill from the scope of a project.</p>
	 * @param project the given project.
	 * @param idSkill the passed skill identifier
	 */
	void removeSkill(Project project, int idSkill);
	
	/**
	 * <p>
	 * Associate the given staff to the pseudo.
	 * </p>
	 * @param project the given project
	 * @param pseudo the given pseudo
	 * @param idAssociatedStaff the associated staff
	 */
	void associateStaffToGhost(Project project, String pseudo, int idAssociatedStaff);

	/**
	 * <p>
	 * Reset the content of the {@link com.tixhi.data.internal.Ghost ghost} for this project.
	 * </p>
	 * @param project the given project
	 * @param pseudo the given pseudo
	 */
	void resetGhost(Project project, String pseudo);

	/**
	 * <p>
	 * Set the technical status of a {@link com.tixhi.data.internal.Ghost ghost}.
	 * </p>
	 * @param project the given project
	 * @param pseudo the given pseudo
	 * @param technical the given technical status
	 */
	void setGhostTechnicalStatus(Project project, String pseudo, boolean technical);

	/**
	 * <p>
	 * Integrate the last list of unknowns pseudos into the ghosts list.
	 * </p>
	 * @param idProject idProject project identifiers 
	 * @param pseudos the given list of pseudos retrieved from the repository
	 * @throws SkillerException thrown if any problem occurs (most probably if the project identifier does not exist)
	 */
	void integrateGhosts(int idProject, Set<String> pseudos) throws SkillerException;

	/**
	 * <p>
	 * Remove a ghost from the ghosts list.
	 * </p>
	 * @param project the given project
	 * @param pseudo the given pseudo
	 */
	void removeGhost(Project project, String pseudo);

	/**
	 * <p>
	 * Add a sonar entry
	 * </p>
	 * @param project the given project
	 * @param sonarEntry the given sonar entry
	 * @throws SkillerException thrown if any exception occurs during the adding process.
	 */
	void addSonarEntry(Project project, SonarProject sonarEntry) throws SkillerException;

	/**
	 * <p>
	 * Remove a sonar entry from the entries collection.
	 * </p>
	 * @param project the given project
	 * @param sonarEntry the given sonar entry
	 */
	void removeSonarEntry(Project project, SonarProject sonarEntry);

	/**
	 * <p>
	 * Test the presence of a sonar entry declared inside the project.
	 * </p>
	 * @param project the given project.
	 * @param key the key of a project declared in Sonar.
	 * @return {@code true} if the key exists in the collection
	 */
	boolean containsSonarEntry(Project project, String key);
	
	/**
	 * Save the files statistics retrieved from the Sonar project.
	 * @param project the given project.
	 * @param sonarProjectKey the key of the Sonar project.
	 * @param filesStats stats retrieved from the file system.
	 */
	void saveFilesStats (Project project, String sonarProjectKey, List<FilesStats> filesStats);
	
	/**
	 * Save the metric values & weights for this Sonar project.
	 * @param project the given project.
	 * @param sonarKey the key of the Sonar project.
	 * @param metricValues The Sonar metric values/weights for this Sonar project
	 * @throws SkillerException thrown if any problem occurs.
	 */
	void saveSonarMetricValues (Project project, String sonarProjectKey, List<ProjectSonarMetricValue> metricValues)
		throws SkillerException;


	/**
	 * Save the evaluation and the total number of lines of code for this Sonar project.
	 * @param project the given project.
	 * @param sonarKey the key of the Sonar project.
	 * @param sonarEvaluation the evaluation processed for the Sonar project
	 * @throws SkillerException thrown if any problem occurs.
	 */
	void saveSonarEvaluation (Project project, String sonarProjectKey, SonarEvaluation sonarEvaluation)
		throws SkillerException;

	/**
	 * Saved the new/first URL of Sonar server to be linked to the given project.
	 * @param project the given project
	 * @param newUrlSonarServer the URL of the Sonar server to be taken in account
	 */
	void saveUrlSonarServer(Project project, String newUrlSonarServer);
	
	/**
	 * Save the ecosystems detected inside the project
	 * @param project the given project
	 * @param ecosystems the list of ecosystem-identifiers
	 */
	void saveEcosystems(Project project, List<Integer> ecosystems);
	
	
}
