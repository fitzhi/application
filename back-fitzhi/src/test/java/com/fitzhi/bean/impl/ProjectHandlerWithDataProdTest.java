package com.fitzhi.bean.impl;

import java.util.ArrayList;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This class tests the class {@link ProjectHandler} with some data of production
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "applicationOutDirectory=src/test/resources/data-prod/" }) 
public class ProjectHandlerWithDataProdTest {

	@Autowired
	ProjectHandler projectHandler;
	
	@Autowired
	DataHandler dataHandler;

	@Test
	public void testLoadData() throws ApplicationException, Exception {
		Map<Integer, Project> map = projectHandler.getProjects();
		Assert.assertNotNull("Projects are loaded", map);
		map.values().forEach(e -> {
			e.setEcosystems(null);
			e.setLocationRepository(null);
			e.setLibraries(null);
			e.setGhosts(null);
			e.setSkills(null);
			e.setAuditEvaluation(0);
			e.setStaffEvaluation(0);			
		});
		dataHandler.saveProjects(map);
	}
	
}
