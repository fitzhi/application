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
	 
	 /**
	  * @param project the passed project
	  * @return the newly created project
	  */
	 Project addNewProject(Project project) throws SkillerException;
	 
	 /**
	  * @param idProject the passed project identifier
	  * @return {@code true} if the project identifier exists, {@code false} otherwise
	  */
	 boolean containsProject(int idProject) throws SkillerException;

	 /**
	  * @param project save a new project
	  * @return {@code true} if the project identifier exists, {@code false} otherwise
	  */
	 void saveProject(Project project) throws SkillerException;
	 
	 /**
	  * @return the locker to avoid any conflict between the saving process and all updates on the projects collection
	  */
	 Object getLocker();

	 /**
	  * @return {@code true} if the collection has been updated, {@code false} otherwise
	  */
	 boolean isDataUpdated();
	 
	 /**
	  * Inform the handler that the collection has been saved.
	  */
	 void dataAreSaved();
}
