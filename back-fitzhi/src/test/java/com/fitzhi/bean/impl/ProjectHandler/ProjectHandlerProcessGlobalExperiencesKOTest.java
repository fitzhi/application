package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.when;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#processGlobalExperiences()}
 *
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ProjectHandlerProcessGlobalExperiencesKOTest {
	
	@Autowired
	ProjectHandler projectHandler;

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
