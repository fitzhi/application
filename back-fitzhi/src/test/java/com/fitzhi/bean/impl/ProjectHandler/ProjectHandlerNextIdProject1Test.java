package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test the method {@link ProjectHandler#nextIdProject()}
 * </p>setGhostTechnicalStatus
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerNextIdProject1Test {
 
	@Autowired
	ProjectHandler projectHandler;

	@Test
	public void empty() throws UnsupportedOperationException, ApplicationException {
		projectHandler.getProjects().clear();
		ProjectHandler spy = spy(projectHandler);
		when(spy.getProjects()).thenReturn(new HashMap<Integer, Project>());
		Assert.assertEquals(1, projectHandler.nextIdProject());
	}
}

