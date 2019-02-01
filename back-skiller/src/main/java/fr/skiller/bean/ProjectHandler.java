package fr.skiller.bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.skiller.data.internal.Ghost;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Pseudo;
import fr.skiller.data.source.Contributor;
import fr.skiller.exception.SkillerException;

public interface ProjectHandler extends DataSaverLifeCycle {

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
	  * @param project the new project to save.
	  */
	 void saveProject(Project project) throws SkillerException;
	 
	 /**
	  * Take account the list of pseudos identified by the end-user in the ghosts dialog. 
	  * @param idProject identifier of this current project.
	  * @param pseudos list of pseudos with their additional information, is any.
	  * @return the result list of impacted pseudos in the {@link fr.skiller.data.internal.Project#ghosts ghosts list}  of the project 
	  * @exception if any exception occurs (such as project does not exist)
	  * {@link fr.skiller.data.internal.Ghost}
	  */
	 List<Pseudo> saveGhosts(int idProject, List<Pseudo> pseudos) throws SkillerException;
	 
}
