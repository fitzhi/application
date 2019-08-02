/**
 * 
 */
package fr.skiller.bean.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.SkillerRuntimeException;
import fr.skiller.bean.CacheDataHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
public class CacheDataHandlerImpl implements CacheDataHandler {
	
 	/**
 	 * The logger for the GitScanner.
 	 */
	private Logger logger = LoggerFactory.getLogger(CacheDataHandlerImpl.class.getCanonicalName());

	/**
	 * cache directory for intermediate files representing the repositories.
	 */
	@Value("${cacheDirRepository}")
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
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Examining %s", savedProject));
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
		if (logger.isDebugEnabled()) {
			logger.debug("repository of project " 
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
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
		    writer.write(gson.toJson(repository));
		}
	}
	
	
	@Override
	public boolean removeRepository(final Project project) throws IOException {
		Path cacheFile = Paths.get(getCacheFilename(project));
		try {
			Files.delete(cacheFile);
			return true;
		} catch (final Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	/**
	 * @return the cache filename for the passed project
	 */
	private String getCacheFilename(final Project project) {
		return cacheDirRepository+project.getId()+"-"+project.getName()+".json";
	}
	
}
