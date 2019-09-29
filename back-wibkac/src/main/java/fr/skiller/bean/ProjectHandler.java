package fr.skiller.bean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import fr.skiller.data.internal.FilesStats;
import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Library;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.SonarProject;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

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
	 * @param project
	 *            the passed project
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
	 * @param project
	 *            the new project to save.
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
	 * <p>Save the risk evaluated for this project.</p>
	 * @param project the given project.
	 * @param risk the calculated risk to set.
	 */
	void saveRisk(Project project, int risk);

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
	 * Reset the content of the {@link fr.skiller.data.internal.Ghost ghost} for this project.
	 * </p>
	 * @param project the given project
	 * @param pseudo the given pseudo
	 */
	void resetGhost(Project project, String pseudo);

	/**
	 * <p>
	 * Set the technical status of a {@link fr.skiller.data.internal.Ghost ghost}.
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
	 * Add or replace a sonar entry
	 * </p>
	 * @param project the given project
	 * @param sonarEntry the given sonar entry
	 */
	void saveSonarEntry(Project project, SonarProject sonarEntry);

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
}
