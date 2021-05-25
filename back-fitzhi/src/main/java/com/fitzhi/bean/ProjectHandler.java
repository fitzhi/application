package com.fitzhi.bean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fitzhi.data.internal.FilesStats;
import com.fitzhi.data.internal.Ghost;
import com.fitzhi.data.internal.Library;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectSkill;
import com.fitzhi.data.internal.ProjectSonarMetricValue;
import com.fitzhi.data.internal.SonarEvaluation;
import com.fitzhi.data.internal.SonarProject;
import com.fitzhi.data.source.CommitHistory;
import com.fitzhi.data.source.Contributor;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

public interface ProjectHandler extends DataSaverLifeCycle {

	/**
	 * @return the complete collection of projects.
	 * @throws ApplicationException
	 *             thrown most probably if an IO exception occurs
	 */
	Map<Integer, Project> getProjects() throws ApplicationException;

	/**
	 * Search for a project associated to the passed name.
	 * 
	 * @param projectName
	 * @throws ApplicationException
	 *             thrown most probably if an IO exception occurs
	 * @return
	 */
	Optional<Project> lookup(String projectName) throws ApplicationException;

	/**
	 * Retrieve a project from the portfolio on the given ID.
	 * 
	 * @param idProject
	 *            project identifier
	 * @return a project present in the projects repository or <code>NULL</code>
	 *         if none exists for this id
	 * @throws ApplicationException
	 *             thrown most probably if an IO exception occurs
	 */
	Project lookup(int idProject) throws ApplicationException;

	/**
	 * Find a <b>NON NULLABLE</b> project on a given ID. A result is mandatory for this method.
	 * 
	 * @param idProject the given project identifier.
	 * @return a non-nullable project retrieved in the projects repository.
	 * @throws ApplicationException thrown if an error occurs during the search, most probably an {@link IOException}
	 * @throws NotFoundException thrown if the project identifier is not retrieved in the application portfolio
	 */
	@NotNull Project getProject(int idProject) throws ApplicationException, NotFoundException;

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
	 * 
	 */
	Project addNewProject(Project project) throws ApplicationException;

	/**
	 * Generate the next project identifier.
	 * 
	 * @return the next available {@code idProject}
	 * @throws ApplicationException thrown if an error occurs during the treatment, (most probably due to an {@link IOException})
	 */
	int nextIdProject() throws ApplicationException;

	/**
	 * @param idProject
	 *            the passed project identifier
	 * @return {@code true} if the project identifier exists, {@code false}
	 *         otherwise
	 */
	boolean containsProject(int idProject) throws ApplicationException;

	/**
	 * @param project the project to save.
	 * @throws ApplicationException thrown if any saving or encryption error occurs
	 */
	void saveProject(Project project) throws ApplicationException;

	/**
	 * Remove the given project from the collection.
	 * @param project the project identifier
	 * @throws ApplicationException exception thrown if any problem occurs, most probably an {@link IOException}. 
	 */
	void removeProject(int idProject) throws ApplicationException;

	/**
	 * <strong>In-activate</strong> the given project in the collection.
	 * @param project the project identifier
	 */
	void inactivateProject(Project project);

	/**
	 * <strong>Re-activate</strong> the given project in the collection.
	 * @param project the project identifier
	 */
	void reactivateProject(Project project);
	
	/**
	 * Save the list of library detected or declared inside the project.
	 * @param idProject the identifier of the project
	 * @param libraries the list of library
	 * @return the former list (if any) 
	 * @exception ApplicationException thrown if any exception occurs (such as project does not exist)
	 */
	List<Library> saveLibraries (int idProject, List<Library> libraries) throws ApplicationException;
	
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
	 * <p>
	 * Save the path location where the remote branch(master) has been cloned/pulled
	 * </p>
	 * @param idProject the project identifier
	 * @param location the path location
	 * @throws ApplicationException thrown if any problem, most probably either an {@link IOException} or the <i>project does not exist</i>.
	 */
	void saveLocationRepository (int idProject, String location) throws ApplicationException;
	
	/**
	 * <p>
	 * Initialize the path location.
	 * </p>
	 * @param project project which path location has be initialized
	 */
	void initLocationRepository (Project project) throws ApplicationException;
	
	/**
	 * <p>
	 * Initialize the path location.
	 * </p>
	 * @param idProject the project identifier
	 * @throws ApplicationException thrown if any problem, most probably either an {@link IOException} or the <i>project does not exist</i>.
	 */
	void initLocationRepository (int idProject) throws ApplicationException;
	
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
	void addSkill(Project project, ProjectSkill skill);

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
	 * Reset the content of the {@link com.fitzhi.data.internal.Ghost ghost} for this project.
	 * </p>
	 * @param project the given project
	 * @param pseudo the given pseudo
	 */
	void resetGhost(Project project, String pseudo);

	/**
	 * <p>
	 * When deleting a staff member, we have to detach him (if necessary) from ALL ghosts present in ALL projects.
	 * </p>
	 * @param staff identifier
	 * @throws ApplicationException thrown if problem occurs
	 */
	void detachStaffMemberFromGhostsOfAllProjects(int idStaff) throws ApplicationException;
	
	/**
	 * <p>
	 * Set the technical status of a {@link com.fitzhi.data.internal.Ghost ghost}.
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
	 * @throws ApplicationException thrown if any problem occurs (most probably if the project identifier does not exist)
	 */
	void integrateGhosts(int idProject, Set<String> pseudos) throws ApplicationException;

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
	 * @throws ApplicationException thrown if any exception occurs during the adding process.
	 */
	void addSonarEntry(Project project, SonarProject sonarEntry) throws ApplicationException;

	/**
	 * <p>
	 * Remove a sonar entry from the entries collection.
	 * </p>
	 * @param project the given project
	 * @param sonarKey the given sonar key
	 */
	void removeSonarEntry(Project project, String sonarKey);

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
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	void saveSonarMetricValues (Project project, String sonarProjectKey, List<ProjectSonarMetricValue> metricValues)
		throws ApplicationException;


	/**
	 * Save the evaluation and the total number of lines of code for this Sonar project.
	 * @param project the given project.
	 * @param sonarKey the key of the Sonar project.
	 * @param sonarEvaluation the evaluation processed for the Sonar project
	 * @throws ApplicationException thrown if any problem occurs.
	 */
	void saveSonarEvaluation (Project project, String sonarProjectKey, SonarEvaluation sonarEvaluation)
		throws ApplicationException;

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
	
	/**
	 * Update the skills of the project based on the commit detected on the repository 
	 * @param project the given project the given project
	 * @param entries the list of entries of {@link CommitHistory commits}
	 * @throws ApplicationException thrown if any exception occurs during the treatment
	 */
	void updateSkills(Project project, List<CommitHistory> entries) throws ApplicationException;
	
	/**
	 * <p>
	 * Reset the metrics attached to the skills of a given project
	 * </p>
	 * <p><i>
	 * This method is most probably used  at the beginning of {@link #updateSkills(Project, List)}.
	 * </i></p>
	 * @param project the given project the given project
	 */
	void resetProjectSkillsMetrics(Project project);
	
	/**
	 * <p>
	 * Test if the path location declared in the project is still valid.
	 * </p>
	 * @return {@code true} if the path location declared inside the project is valid.
	 */
	boolean hasValidRepository(Project project);
}
