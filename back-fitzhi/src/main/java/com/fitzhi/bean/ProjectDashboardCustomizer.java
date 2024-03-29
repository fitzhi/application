/**
 * 
 */
package com.fitzhi.bean;

import java.util.List;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.source.Operation;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Interface in charge of customizing the project dashboard.
 * </p>
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
	 * @throws ApplicationException thrown most probably if an IO Exception occurs when loading the paths.
	 */
	List<String> lookupPathRepository(Project project, String criteria) throws ApplicationException;

	/**
	 * <p>
	 * Take in account a new created staff into the repository collection of a given project.
	 * This new user should have been detected before as an unknown contributor.
	 * The goal of this method is to propagate his creation, if necessary, 
	 * and to affect his definitive identifier {@code idStaff} 
	 * into the source code item {@link Operation#idStaff operations}.
	 * </p>
	 * <p>
	 * The default implementation is {@code synchronized} 
	 * to avoid conflicts when multiple staff member are taken in account simultaneously.
	 * 
	 * </p>
	 * @param project the given project
	 * @param staff the new staff member created
	 * @throws ApplicationException thrown if any problem occurs (such as IOException when reading and parsing the saved repository)
	 */
	void takeInAccountNewStaff(Project project, Staff staff) throws ApplicationException;
	

}
