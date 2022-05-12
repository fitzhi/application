package com.fitzhi.source.crawler.git;

import static com.fitzhi.service.ConnectionSettingsType.DIRECT_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.NO_LOGIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
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
		// connectionSettings > 1 and project active, so we do not skip this project
		Project p = projectHandler.lookup(1);
		p.setConnectionSettings(DIRECT_LOGIN);
		p.setActive(true);
		
		p = projectHandler.lookup(2);
		p.setConnectionSettings(DIRECT_LOGIN);
		p.setActive(false);

		log.debug ("The complete generation in an asynchronous mode has been launched.");
		doNothing().when(crawler).generateAsync(any(), any());
		batchCrawler.completeGeneration();
		verify(crawler, timeout(100).times(1)).generateAsync(any(), any());
	}

	@After
	public void after() throws ApplicationException {
		// We reset the data to their initial state.
		Project p = projectHandler.lookup(1);
		p.setConnectionSettings(NO_LOGIN);
		p.setActive(true);
	}
}