package com.fitzhi.source.crawler.git;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.BatchRepositoryCrawler;
import com.fitzhi.source.crawler.RepoScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BatchGitCrawlerTest {

	@Autowired
	BatchRepositoryCrawler batchCrawler;

	@MockBean
	RepoScanner crawler;

	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
	}
	
	@Test
	public void testLaunchNoProjectEligible() throws Exception {
		// All projects do not have a connection string, and therefore are skipped
		batchCrawler.completeGeneration();
		log.debug ("The complete generation in an asynchronous mode has been launched.");
		verify(crawler, timeout(100).times(0)).generateAsync(any(), any());
		verify(crawler, timeout(100).times(0)).generate(any(), any());
	}

	@Test
	public void testOneActiveProjectWithSettings() throws Exception {
		// connectionSettings > 1, so we do not skip this project
		Project p = projectHandler.get(1);
		p.setConnectionSettings(1);
		when(crawler.generateAsync(any(), any())).thenReturn(null);
		log.debug ("The complete generation in an asynchronous mode has been launched.");
		batchCrawler.completeGeneration();
		verify(crawler, timeout(100).times(1)).generateAsync(any(), any());
		verify(crawler, timeout(100).times(0)).generate(any(), any());
	}

	@Test
	public void testOneInactiveProjectWithSettings() throws Exception {
		// connectionSettings > 1, so we do not skip this project
		Project p = projectHandler.get(1);
		p.setConnectionSettings(1);
		p.setActive(false);
		
		log.debug ("The complete generation in an asynchronous mode has been launched.");
		batchCrawler.completeGeneration();
		verify(crawler, timeout(100).times(0)).generateAsync(any(), any());
		verify(crawler, timeout(100).times(0)).generate(any(), any());
	}

	@After
	public void after() throws ApplicationException {

		// We reset the data to their initial state.
		Project p = projectHandler.get(1);
		p.setConnectionSettings(0);
		p.setActive(true);
	}
}