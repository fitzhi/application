/**
 * 
 */
package fr.skiller.bean;

import java.util.Map;

import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * Interface in charge of saving data.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface DataSaver {

	/**
	 * Save projects on a persistent media
	 * @param projects list of projects
	 * @throws SkillerException thrown if exception occurs during the saving process
	 */
	void save(Map<Integer, Project> projects) throws SkillerException;
	
	
	/**
	 * Load the projects from a persistent media
	 * @param projects list of projects
	 * @throws SkillerException thrown if exception occurs during the saving process
	 */
	Map<Integer, Project> load() throws SkillerException;
}
