package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.Mockito.when;

import java.util.Collections;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
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
public class ProjectHandlerNextIdProject1Test {
 
	@Autowired
	ProjectHandler projectHandler;

	@MockBean
	DataHandler dataHandler;

	@Test
	public void empty() throws UnsupportedOperationException, ApplicationException {
		when(dataHandler.loadProjects()).thenReturn(Collections.emptyMap());
		Assert.assertEquals(1, projectHandler.nextIdProject());
	}
}

