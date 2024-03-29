package com.fitzhi.bean;

import java.io.IOException;

import com.fitzhi.bean.impl.RepositoryState;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;

/**
 * Interface in charge of saving the JSON data into the file system<br/>
 * This interface and its underlining implementation are there, only for cache purpose, to speed up the treatment. 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface CacheDataHandler {

	/**
	 * <p>
	 * Test if previous analysis has already been made for this project, and saved on the file system.
	 * </p>
	 * This method can return 3 possible values :
	 * </p>
	 * <ul>
	 * <li> {@link RepositoryState#REPOSITORY_NOT_FOUND} if no repository has been found,</li>
	 * <li> {@link RepositoryState#REPOSITORY_OUT_OF_DATE} if the repository has been found, but is too old,</li>
	 * <li> {@link RepositoryState#REPOSITORY_READY} if the repository is ready to be used.</li>
	 * </ul>
	 * @param project the current project
	 * @return the state of the Fitzhi internal repository.
	 * @throws IOException if an IOException occurs
	 */
	RepositoryState retrieveRepositoryState (Project project) throws IOException;
	
	/**
	 * <p>
	 * Retrieve and parse the project commit repository from the file system.
	 * <p>
	 * @param project the current project
	 * @return the repository associated to the project
	 * @throws IOException if an IOException occurs
	 */
	CommitRepository getRepository (Project project) throws IOException;
	
	/**
	 * <p>
	 * Save the commit-repository on the file system
	 * </p>
	 * @param project the current project
	 * @param repository the repository issued from this project
	 * @throws IOException if an IOException occurs
	 */
	void saveRepository (Project project, CommitRepository repository) throws IOException;
	
	/**
	 * Remove the repository on the file system, for this project.<br/>
	 * @param project the current project
	 * @return {@code true} if the deletion is successful, {@code false} otherwise
	 * @throws ApplicationException if an exception occurs during the removal, most probably an {@link IOException}
	 */
	boolean removeRepository (Project project) throws ApplicationException;
		
}
