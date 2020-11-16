/**
 * 
 */
package com.fitzhi.bean.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitzhi.SkillerRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
@Service
public class CacheDataHandlerImpl implements CacheDataHandler {
	
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
	
	@Override
	public boolean hasCommitRepositoryAvailable(Project project) throws IOException {
		Path savedProject = Paths.get(getCacheFilename(project));
		if (log.isDebugEnabled()) {
			log.debug(String.format("Examining %s", savedProject.toFile().getAbsolutePath()));
		}
		if (savedProject.toFile().exists()) {
			FileTime lastModified = Files.getLastModifiedTime(savedProject);
			if ( lastModified.toMillis() + (cacheDuration * NUMBER_OF_MS_PER_DAY) < System.currentTimeMillis() ) {
				cleanUp(savedProject);
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Delete the passed path or throw a runtime exception is the deletion fails.
	 * @param path the passed path
	 */
	private void cleanUp(Path path){
		try {
		  Files.delete(path);
		} catch (final Exception e) {
			throw new SkillerRuntimeException(e);
		}
	}
	
	@Override
	public CommitRepository getRepository(Project project) throws IOException {
		
		final FileReader fr = new FileReader(new File(getCacheFilename(project)));

		CommitRepository repository = new BasicCommitRepository();
		repository = gson.fromJson(fr, repository.getClass());
		if (log.isDebugEnabled()) {
			log.debug("repository of project " 
					+ project.getName() 
					+ " retrieved from cache. It contains " 
					+ repository.size() 
					+ " entries.");
		}
		fr.close();
		return repository;
	}

	@Override
	public void saveRepository(Project project, CommitRepository repository) throws IOException {

		//Get the repository path
		Path path = Paths.get(getCacheFilename(project));
		
		//Use try-with-resource to get auto-closeable buffered writer instance close
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
		    gson.toJson(repository, writer);
		}
	}
	
	@Override
	public boolean removeRepository(final Project project) throws IOException {
		Path cacheFile = Paths.get(getCacheFilename(project));
		try {
			Files.delete(cacheFile);
			return true;
		} catch (final Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/**
	 * @return the cache filename for the passed project
	 */
	private String getCacheFilename(final Project project) {
		Path rootLocation = Paths.get(cacheDirRepository);
		final File destination = rootLocation.resolve("").toFile();
		// If the destination directory does not exist, we create it
		if (!destination.exists()) {
			try {
				destination.mkdir();
			} catch (final SecurityException se) {
				throw new SkillerRuntimeException(se);
			}
		}
		return rootLocation.resolve(project.getId()+"-"+project.getName()+".json").toFile().getAbsolutePath();
	}
	
}
