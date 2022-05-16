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
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the method {@link ProjectHandler#nextIdProject()}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ProjectHandlerNextIdProject2Test {
 
	@Autowired
	ProjectHandler projectHandler;

	@MockBean
	DataHandler dataHandler;

	@Test
	public void found() throws UnsupportedOperationException, ApplicationException {

		Map<Integer, Project> projects = new HashMap<>();
		projects.put(1, new Project(1, "one"));
		projects.put(1788, new Project(1788, "one year before the revolution"));
		when(dataHandler.loadProjects()).thenReturn(projects);

		Assert.assertEquals(1789, projectHandler.nextIdProject());
	}
}

