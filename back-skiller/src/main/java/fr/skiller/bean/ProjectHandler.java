package fr.skiller.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.skiller.data.internal.Project;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

public interface ProjectHandler {

	/**
	 * @return the complete collection of projects.
	 * @throws SkillerException thrown most probably if an IO exception occurs
	 */
	Map<Integer, Project> getProjects() throws SkillerException;
	
	/**
	 * Search for a project associated to the passed name. 
	 * @param projectName 
	 * @throws SkillerException thrown most probably if an IO exception occurs
	 * @return
	 */
	Optional<Project> lookup(String projectName) throws SkillerException;

	/**
	 * Retrieve a project. 
	 * @param idProject project identifier 
	 * @return a project present in the projects repository or <code>NULL</code> if none exists for this id
	 * @throws SkillerException thrown most probably if an IO exception occurs
	 */
	Project get(int idProject) throws SkillerException;
	
	/**
	 * <p>Initialize the content of the in-memory memories.</p>
	 * <i>This method exists only for testing purpose</i>
	 */
	 void init();

	 /**
	  * @param idProject the project identifier.
	  * @return the list of contributor for the given project.
	  */
	 List<Contributor> contributors(int idProject);
}
