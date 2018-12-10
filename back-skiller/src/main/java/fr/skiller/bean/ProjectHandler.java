package fr.skiller.bean;

import java.util.Map;
import java.util.Optional;

import fr.skiller.data.internal.Project;

public interface ProjectHandler {

	Map<Integer, Project> getProjects();
	
	/**
	 * Search for a project associated to the passed name. 
	 * @param projectName 
	 * @return
	 */
	Optional<Project> lookup(final String projectName);

	/**
	 * Retrieve a project. 
	 * @param idProject project identifier 
	 * @return a project present in the projects repository or <code>NULL</code> if none exists for this id
	 */
	Project get(final int idProject);
	
	/**
	 * <p>Initialize the content of the in-memory memories.</p>
	 * <i>This method exists only for testing purpose</i>
	 */
	 void init();
	
}
