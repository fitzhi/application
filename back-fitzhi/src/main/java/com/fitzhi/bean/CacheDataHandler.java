package com.fitzhi.bean;

import java.io.IOException;

import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.CommitRepository;

/**
 * Interface in charge of saving the JSON data into the file system<br/>
 * This interface and its underlining implementation are there, only for cache purpose, to speed up the treatment. 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface CacheDataHandler {

	/**
	 * Test if previous extraction has already made for this project<br/>
	 * @param project the current project
	 * @return {@code true} if a previous extraction for this project is available on the file system, {@code false} otherwise
	 * @throws IOException if an IOException occurs
	 */
	boolean hasCommitRepositoryAvailable (Project project) throws IOException;
	
	/**
	 * Retrieve and parse the project from the file system<br/>
	 * @param project the current project
	 * @return the repository associated to the project
	 * @throws IOException if an IOException occurs
	 */
	CommitRepository getRepository (Project project) throws IOException;
	
	/**
	 * Save the repository on the file system<br/>
	 * @param project the current project
	 * @param repository the repository associated to the project
	 * @throws IOException if an IOException occurs
	 */
	void saveRepository (Project project, CommitRepository repository) throws IOException;
	
	/**
	 * Remove the repository on the file system, for this project.<br/>
	 * @param project the current project
	 * @return {@code true} if the deletion is successful, {@code false} otherwise
	 * @throws IOException if an IOException occurs
	 */
	boolean removeRepository (Project project) throws IOException;
		
}
