package com.fitzhi.bean.impl.ProjectHandler;

import com.fitzhi.bean.ProjectHandler;
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
		Assert.assertEquals(1, projectHandler.nextIdProject());
	}
}

