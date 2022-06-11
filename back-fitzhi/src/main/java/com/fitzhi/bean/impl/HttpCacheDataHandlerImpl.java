/**
 * 
 */
package com.fitzhi.bean.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the <strong>SLAVE</strong> implementation of the interface {@link CacheDataHandler}.
 * Data are sent to the main application through a REST end-point.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service
@Profile("slave")
public class HttpCacheDataHandlerImpl implements CacheDataHandler {
	
	
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public RepositoryState retrieveRepositoryState(Project project) throws IOException {
		return RepositoryState.REPOSITORY_NOT_FOUND;
	}
	
	@Override
	public CommitRepository getRepository(Project project) throws IOException {
		// You do not load the repository from the slave instance.
		throw new ApplicationRuntimeException("Should not pass here");
	}

	@Override
	public void saveRepository(Project project, CommitRepository repository) throws IOException {
		if (log.isInfoEnabled()) {
			log.info(String.format("saving repository for project %d %s", project.getId(), project.getName()));
		}
//		throw new ApplicationRuntimeException("Not implemented yet");
	}
	
	@Override
	public boolean removeRepository(final Project project) throws ApplicationException {
		// You do not remove repositories from the slave instance.
		throw new ApplicationRuntimeException("Should not pass here");
	}
	
}
