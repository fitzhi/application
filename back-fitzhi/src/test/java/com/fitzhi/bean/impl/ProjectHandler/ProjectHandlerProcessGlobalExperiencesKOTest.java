package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#processGlobalExperiences()}
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerProcessGlobalExperiencesKOTest {
	
	@Autowired
	ProjectHandler projectHandler;

	@MockBean
	EcosystemAnalyzer ecosystemAnalyzer;

	@MockBean
	StaffHandler staffHandler;

	@MockBean
	DataHandler dataHandler;

	/**
	 * If loadProjects() throw an ApplicationException
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void processGlobalExperiencesKO() throws ApplicationException {
		when(dataHandler.loadProjects()).thenThrow(ApplicationException.class);
		projectHandler.processGlobalExperiences();
	}
	
}
