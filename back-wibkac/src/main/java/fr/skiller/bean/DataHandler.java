/**
 * 
 */
package fr.skiller.bean;

import java.util.List;
import java.util.Map;

import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.SourceControlChanges;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.git.SCMChange;

/**
 * Interface in charge of saving & loading data.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface DataHandler {

	/**
	 * Save projects on a persistent media
	 * @param projects list of projects
	 * @throws SkillerException thrown if exception occurs during the saving process
	 */
	void saveProjects(Map<Integer, Project> projects) throws SkillerException;
	
	
	/**
	 * Load the projects from a persistent media
	 * @return the map of projects
	 * @throws SkillerException thrown if exception occurs during the saving process
	 */
	Map<Integer, Project> loadProjects() throws SkillerException;

	/**
	 * Save the staff on a persistent media
	 * @param staff list of staff
	 * @throws SkillerException thrown if exception occurs during the saving process
	 */
	void saveStaff(Map<Integer, Staff> staff) throws SkillerException;
	
	
	/**
	 * Load the staff members from a persistent media
	 * @return the staff
	 * @throws SkillerException thrown if exception occurs during the saving process
	 */
	Map<Integer, Staff> loadStaff() throws SkillerException;

	/**
	 * Save the skills <i>(probably for this first release)</i> on the file system
	 * @param staff list of staff
	 * @throws SkillerException thrown if an exception occurs during the saving process
	 */
	void saveSkills(Map<Integer, Skill> staff) throws SkillerException;
	
	/**
	 * <p>
	 * Save the source control changes loaded from the repository for cache & debug purpose.
	 * </p>
	 * <p><i>Due to the DEBUG purpose of this file, the output format will <b>CSV</b></i>.</p>
	 * @param project project whose changes have to be serialized in CSV
	 * @param changes changes retrieved from the repository
	 * @throws SkillerException thrown if an exception occurs during the saving process
	 */
	void saveChanges(Project project, SourceControlChanges changes) throws SkillerException;

	/**
	 * Load the skills <i>(probably for this first release)</i> from the file system
	 * @return the skills collection, retrieved from the file system
	 * @throws SkillerException thrown if an exception occurs during the saving process
	 */
	Map<Integer, Skill> loadSkills() throws SkillerException;
	
	/**
	 * <p>
	 * Save the repository directories.<br/>
	 * This file is a raw text file with all distinct directories.
	 * </p>
	 * <p>
	 * The resulting file will be used by the dependencies eviction form 
	 * <i>(table-dependencies.component in the Angular project)</i>.
	 * </p>
	 * @param project the current project
	 * @param changes the history of changes retrieved from the repository
	 * @throws SkillerException thrown if an exception occurs during the saving process.
	 */
	void saveRepositoryDirectories(Project project, SourceControlChanges changes) throws SkillerException;
	
	/**
	 * Load the list of all directory-paths for the repository of the given project.
	 * @param project the given project
	 * @return the resulting paths list
	 * @throws SkillerException thrown if an exception occurs during the loading process.
	 */
	List<String> loadRepositoryDirectories (Project project) throws SkillerException;
	
}
