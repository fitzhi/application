/**
 * 
 */
package fr.skiller.bean;

import java.util.List;

import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * <p>Interface in charge of customizing the project dashboard.</p>
 *   
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface ProjectDashboardCustomizer {

 	/**
 	 * <p>Cleanup the filename path from non relevant directory (such as {@code /src/main/java})</p>
	 * <p>
	 * e.g. <code>/src/main/java/java/util/List.java</code> will be treated like <code>java/util/List.java</code>
	 * </p>
 	 * @param path the given path
 	 * @return the cleanup path
 	 */
	String cleanupPath (String path);

	/**
	 * <p>Lookup repository pathnames for the given criteria</p>
	 * @param project the project in which repository pathnames should be evaluated for the passed search criteria
	 * @param criteria the search criteria
	 * @return the list of pathnames matching the criteria
	 * @throws SkillerException thrown most probably if an IO Exception occurs when loading the paths.
	 */
	List<String> lookupPathRepository(Project project, String criteria) throws SkillerException;

}
