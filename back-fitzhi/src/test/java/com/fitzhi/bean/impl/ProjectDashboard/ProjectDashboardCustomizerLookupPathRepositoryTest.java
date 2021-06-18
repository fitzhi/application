/**
 * 
 */
package com.fitzhi.bean.impl.ProjectDashboard;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectDashboardCustomizer;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>Testing the method {@link com.fitzhi.bean.ProjectDashboardCustomizer#lookupPathRepository(Project, String)}</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectDashboardCustomizerLookupPathRepositoryTest {

	@Autowired
	ProjectDashboardCustomizer projectDashboardCustomizer;
	
	@Autowired
	DataHandler dataSaver;
	
	List<String> paths;
	
	Project project;
	@Before
	public void before() throws ApplicationException {
		project = new Project(9999, "Project 9999");
		project.setBranch("master");
		paths = dataSaver.loadRepositoryDirectories(project);
	}
	
	@Test
	public void testA() throws ApplicationException {
		List<String> resultingPaths = projectDashboardCustomizer.lookupPathRepository(project, "a");
		Assert.assertEquals("4 continents begin with a", 4, resultingPaths.size());
		String continents[] = {"africa","america", "antartic","asia"}; 
		Assert.assertArrayEquals (continents, resultingPaths.toArray());
	}

	@Test
	public void testAf() throws ApplicationException {
		List<String> resultingPaths = projectDashboardCustomizer.lookupPathRepository(project, "af");
		Assert.assertEquals("4 continents begin with af", 1, resultingPaths.size());
		String continents[] = {"africa"}; 
		Assert.assertArrayEquals (continents, resultingPaths.toArray());
	}

	@Test
	public void testEurope() throws ApplicationException {
		List<String> resultingPaths = projectDashboardCustomizer.lookupPathRepository(project, "europe/");
		Assert.assertEquals("2 'contries' begin with europe/", 2, resultingPaths.size());
	}

	@Test
	public void testbackwibkac() throws ApplicationException {
		List<String> resultingPaths = projectDashboardCustomizer.lookupPathRepository(project, "europe/back-wibkac/");
		Assert.assertEquals("4 contries begin in europe/back-wibkac/", 4, resultingPaths.size());
	}

	@Test
	public void testcountriesWithIInEurope() throws ApplicationException {
		List<String> resultingPaths = projectDashboardCustomizer.lookupPathRepository(project, "europe/back-wibkac/i");
		resultingPaths.stream().forEach(System.out::println);
		Assert.assertEquals("2 contries begin in europe/back-wibkac/", 2, resultingPaths.size());
	}
	
}
