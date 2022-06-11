/**
 * 
 */
package com.fitzhi.bean.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
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

	/**
	 * URL of the backend which hosts the main application.
	 */
	@Value("${applicationUrl}")
	private String applicationUrl;

	/**
	 * Organization name. This name is unique and therefore can be considered as an ID.
	 */
	@Value("${organization}")
	private String organization;

	/**
	 * Login used by the slave for the connection to the main Fitzhi applciation.
	 */
	@Value("${login}")
	private String login;

	/**
	 * Password used by the slave for the connection to the main Fitzhi application.
	 */
	@Value("${pass}")
	private String pass;

	@Autowired
	HttpConnectionHandler httpConnectionHandler;
	
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	HttpAccessHandler<Void> httpAccess;

	@Override
	public RepositoryState retrieveRepositoryState(Project project) throws ApplicationException {
		return RepositoryState.REPOSITORY_NOT_FOUND;
	}
	
	@Override
	public CommitRepository getRepository(Project project) throws ApplicationException {
		// You do not load the repository from the slave instance.
		throw new ApplicationRuntimeException("Should not pass here");
	}

	@Override
	public void saveRepository(Project project, CommitRepository repository) throws ApplicationException {
		if (log.isInfoEnabled()) {
			log.info(String.format("saving repository for project %d %s", project.getId(), project.getName()));
		}
/* 
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/project/" + project.getId() + "commit-repository";
		httpAccess.put(url, repository.getData(), new TypeReference<Void>(){});
*/
		//		throw new ApplicationRuntimeException("Not implemented yet");
	}
	
	@Override
	public boolean removeRepository(final Project project) throws ApplicationException {
		// You do not remove repositories from the slave instance.
		throw new ApplicationRuntimeException("Should not pass here");
	}
	
}
