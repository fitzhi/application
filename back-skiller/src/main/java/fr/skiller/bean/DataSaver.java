/**
 * 
 */
package fr.skiller.bean;

import java.util.Map;

import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

/**
 * Interface in charge of saving & loading data.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface DataSaver {

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
	 * Load the skills <i>(probably for this first release)</i> from the file system
	 * @return the skills collection, retrieved from the file system
	 * @throws SkillerException thrown if an exception occurs during the saving process
	 */
	Map<Integer, Skill> loadSkills() throws SkillerException;
}
