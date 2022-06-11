/**
 * 
 */
package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static com.fitzhi.bean.impl.RepositoryState.REPOSITORY_NOT_FOUND;
import static com.fitzhi.bean.impl.RepositoryState.REPOSITORY_OUT_OF_DATE;
import static com.fitzhi.bean.impl.RepositoryState.REPOSITORY_READY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.data.source.DataCommitRepository;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the <strong>APPLICATION</strong> implementation of the interface {@link CacheDataHandler}.
 * Data are saved on the file system
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service
@Profile("application")
public class FileCacheDataHandlerImpl implements CacheDataHandler {
	
	/**
	 * cache directory for intermediate files representing the repositories.
	 */
	@Value("${cache.working.dir}")
	private String cacheDirRepository;

	/**
	 * Number of days : duration of entries in the cache.
	 */
	@Value("${cache_duration}")
	private int cacheDuration;
	
	/**
	 * Number of milliseconds per day.
	 */
	private static final long NUMBER_OF_MS_PER_DAY = 1000l*3600*24; 
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();
	
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public RepositoryState retrieveRepositoryState(Project project) throws IOException {
		Path savedProject = Paths.get(generateCacheFilename(project));
		if (log.isDebugEnabled()) {
			log.debug(String.format("Examining %s", savedProject.toFile().getAbsolutePath()));
		}
		if (savedProject.toFile().exists()) {
			FileTime lastModified = Files.getLastModifiedTime(savedProject);
			if ( lastModified.toMillis() + (cacheDuration * NUMBER_OF_MS_PER_DAY) < System.currentTimeMillis() ) {
				cleanUp(savedProject);
				return REPOSITORY_OUT_OF_DATE;
			} else {
				return REPOSITORY_READY;
			}
		}
		return REPOSITORY_NOT_FOUND;
	}
	
	/**
	 * Delete the passed path or throw a runtime exception is the deletion fails.
	 * @param path the passed path
	 */
	private void cleanUp(Path path){
		try {
		  Files.delete(path);
		} catch (final Exception e) {
			throw new ApplicationRuntimeException(e);
		}
	}
	
	@Override
	public CommitRepository getRepository(Project project) throws IOException {
		
		DataCommitRepository data = objectMapper.readValue(new File(generateCacheFilename(project)), DataCommitRepository.class);
		
		CommitRepository repository = new BasicCommitRepository();
		repository.setUnknownContributors(data.getUnknownContributors());
		repository.setRepository(data.getRepo());

		if (log.isDebugEnabled()) {
			log.debug(String.format(
				"Repository of project %s retrieved from the cache. It contains %d entries.", 
				project.getName(), 
				repository.size())); 
		}

		return repository;
	}

	@Override
	public void saveRepository(Project project, CommitRepository repository) throws IOException {

		//Get the repository path
		Path path = Paths.get(generateCacheFilename(project));

		//Use try-with-resource to get auto-closeable buffered writer instance close
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(objectMapper.writeValueAsString(repository.getData()));
		}
	}
	
	@Override
	public boolean removeRepository(final Project project) throws ApplicationException {
		Path cacheFile = Paths.get(generateCacheFilename(project));

		if (log.isInfoEnabled()) {
			log.info(String.format("Removing the file %s if this file exists", cacheFile.getFileName()));
		}

		if (!Files.exists(cacheFile)) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("File %s does not exist", cacheFile.getFileName()));
			}	
			return false;
		}

		try {
			Files.delete(cacheFile);
			return true;
		} catch (final IOException ioe) {
			log.error(ioe.getMessage());
			throw new ApplicationException (CODE_IO_EXCEPTION, ioe.getMessage());
		}
	}

	/**
	 * <p>
	 * Generate the cache filename.
	 * </p>
	 * @param project the current project
	 * @return the cache filename for the passed project
	 */
	private String generateCacheFilename(final Project project) {
		Path rootLocation = Paths.get(cacheDirRepository);
		final File destination = rootLocation.resolve("").toFile();
		// If the destination directory does not exist, we create it
		if (!destination.exists()) {
			try {
				destination.mkdir();
			} catch (final SecurityException se) {
				throw new ApplicationRuntimeException(se);
			}
		}
		return rootLocation.resolve(String.format("%d.json", project.getId())).toFile().getAbsolutePath();
	}
	
}
