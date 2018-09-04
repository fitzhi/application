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
	
}
