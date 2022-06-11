package com.fitzhi.bean.impl.data_handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.bean.impl.HttpCacheDataHandlerImpl;
import com.fitzhi.bean.impl.RepositoryState;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.data.source.CommitRepository;
import com.fitzhi.exception.ApplicationException;

/**
 * Testing the class {@link HttpCacheDataHandlerImpl}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("slave")
public class HttpCacheDataHandlerImplTest {
	
	@Autowired
	CacheDataHandler cacheDataHandler;

	@MockBean
	HttpConnectionHandler httpConnectionHandler;

	@MockBean
	HttpAccessHandler<Void> httpAccessHandler;

	@Test
	public void retrieveRepositoryState () throws ApplicationException {
		Assert.assertEquals(RepositoryState.REPOSITORY_NOT_FOUND, cacheDataHandler.retrieveRepositoryState(new Project()));
	}
	
	@Test (expected = ApplicationRuntimeException.class)
	public void getRepository () throws ApplicationException {
		cacheDataHandler.getRepository(new Project());
	}
	
	@Test
	public void saveRepositoryNotConnected () throws ApplicationException {
		when(httpConnectionHandler.isConnected()).thenReturn(false);
		doNothing().when(httpConnectionHandler).connect(anyString(), anyString());
		when(httpAccessHandler.put(anyString(), any(), any())).thenReturn(null);

		Project p = new Project(1789, "The French revolution");
		CommitRepository commitRepository = commitRepository();
		cacheDataHandler.saveRepository(p, commitRepository);

		verify(httpAccessHandler, times(1)).put(
			"http://localhost:8080/api/project/1789/commit-repository", 
			commitRepository.getData(), 
			HttpCacheDataHandlerImpl.typeReference);
		verify(httpConnectionHandler, times(1)).connect(anyString(), anyString());
	}

	@Test
	public void saveRepositoryConnected () throws ApplicationException {
		when(httpConnectionHandler.isConnected()).thenReturn(true);
		when(httpAccessHandler.put(anyString(), any(), any())).thenReturn(null);

		Project p = new Project(1789, "The French revolution");
		CommitRepository commitRepository = commitRepository();
		cacheDataHandler.saveRepository(p, commitRepository);

		verify(httpAccessHandler, times(1)).put(
			"http://localhost:8080/api/project/1789/commit-repository", 
			commitRepository.getData(), 
			HttpCacheDataHandlerImpl.typeReference);
		verify(httpConnectionHandler, never()).connect(anyString(), anyString());
	}
	
	private CommitRepository commitRepository() {
		CommitRepository cr = new BasicCommitRepository();
		cr.setRepository(new HashMap<>());
		Set<String> unknowns = new HashSet<>();
		unknowns.add("one");
		cr.setUnknownContributors(unknowns);
		return cr;
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void removeRepository () throws ApplicationException {
		cacheDataHandler.removeRepository(new Project());
	}

}
