package com.fitzhi.bean.impl.data_handler;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.impl.RepositoryState;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.source.BasicCommitRepository;
import com.fitzhi.exception.ApplicationException;

/**
 * Testing the class CacheDataHandlerImpl
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("slave")
public class HttpCacheDataHandlerImplTest {
	
	@Autowired
	CacheDataHandler cacheDataHandler;

	@Test
	public void retrieveRepositoryState () throws IOException {
		Assert.assertEquals(RepositoryState.REPOSITORY_NOT_FOUND, cacheDataHandler.retrieveRepositoryState(new Project()));
	}
	
	@Test (expected = ApplicationRuntimeException.class)
	public void getRepository () throws IOException {
		cacheDataHandler.getRepository(new Project());
	}
	
	@Test (expected = ApplicationRuntimeException.class)
	public void saveRepository () throws IOException {
		cacheDataHandler.saveRepository(new Project(), new BasicCommitRepository());
	}
	
	@Test (expected = ApplicationRuntimeException.class)
	public void removeRepository () throws ApplicationException {
		cacheDataHandler.removeRepository(new Project());
	}

}
