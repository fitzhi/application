package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the method {@link ProjectHandler#init()}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ProjectHandlerInitTest {
	
	@Autowired
	ProjectHandler projectHandler;

	@MockBean
	DataHandler dataHandler;

	@Test
	public void init() throws ApplicationException {

		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1789, new Project(1789, "The Revolution"));
		when(dataHandler.loadProjects()).thenReturn(projects);
		
		// If init has been processed, then the method projectHandler.getProjects() should call twice the dataloader
		Assert.assertFalse (this.projectHandler.getProjects().isEmpty());
		projectHandler.init();
		Assert.assertFalse (this.projectHandler.getProjects().isEmpty());

		verify(dataHandler, times(2)).loadProjects();
	}

}
