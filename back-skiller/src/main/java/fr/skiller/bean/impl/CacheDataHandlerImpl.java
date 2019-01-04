/**
 * 
 */
package fr.skiller.bean.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.bean.CacheDataHandler;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.source.BasicCommitRepository;
import fr.skiller.data.source.CommitRepository;
import fr.skiller.source.scanner.git.GitScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
public class CacheDataHandlerImpl implements CacheDataHandler {
 	/**
 	 * The logger for the GitScanner.
 	 */
	Logger logger = LoggerFactory.getLogger(CacheDataHandlerImpl.class.getCanonicalName());

	/**
	 * cache directory for intermediate files representing the repositories.
	 */
	@Value("${cacheDirRepository}")
	private String cacheDirRepository;

	/**
	 * Number of days : duration of entries in the cache.
	 */
	@Value("${cache_duration}")
	private int cache_duration;
	
	/**
	 * Number of milliseconds per day.
	 */
	private final long NUMBER_OF_MS_PER_DAY = 1000*3600*24; 
	
	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	
	@Override
	public boolean hasCommitRepositoryAvailable(Project project) throws IOException {
		File savedProject = new File(getCacheFilename(project));
		if (savedProject.exists()) {
			long lastModified = savedProject.lastModified();
			if (new Date().after(new Date(lastModified+cache_duration*NUMBER_OF_MS_PER_DAY))) {
				savedProject.delete();
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public CommitRepository getRepository(Project project) throws IOException {
		
		final FileReader fr = new FileReader(new File(getCacheFilename(project)));

		CommitRepository repository = new BasicCommitRepository();
		repository = gson.fromJson(fr, repository.getClass());
		if (logger.isDebugEnabled()) {
			logger.debug("repository of project " 
					+ project.name 
					+ " retrieved from cache. It contains " 
					+ repository.size() 
					+ " entries.");
		}
		return repository;
	}

	@Override
	public void saveRepository(Project project, CommitRepository repository) throws IOException {
		final FileWriter fw = new FileWriter(new File(getCacheFilename(project)));
		fw.write(gson.toJson(repository));
		fw.close();
	}
	
	/**
	 * @return the cache filename for the passed project
	 */
	private String getCacheFilename(final Project project) {
		return cacheDirRepository+project.id+"-"+project.name+".json";
	}
}
