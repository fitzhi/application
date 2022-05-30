package com.fitzhi.bean.impl.ProjectHandler;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the method {@link ProjectHandler#setProjects(java.util.Map)}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
@ActiveProfiles("slave")
public class ProjectHandlerSetProjectsTest {

	@Autowired
	ProjectHandler projectHandler;
		
	@MockBean
	DataHandler dataHandler;

	@Test (expected = ApplicationException.class)
	public void slaveIsMandatory() throws ApplicationException {
		when(dataHandler.isLocal()).thenReturn(true);
		Map<Integer, Project> projects = new HashMap<>();
		projectHandler.setProjects(projects);
	}

	@Test 
	public void nominal() throws ApplicationException {
		when(dataHandler.isLocal()).thenReturn(false);
		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1789, new Project(1789, "The french revolution"));
		projectHandler.setProjects(projects);
		Assert.assertEquals(1, projectHandler.getProjects().size());
	}

}
