/**
 * 
 */
package com.tixhi.bean.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tixhi.bean.ProjectHandler;
import com.tixhi.data.internal.Project;
import com.tixhi.exception.SkillerException;

/**
 * <p>
 * Test the method {@link ProjectHandler#associateStaffToGhost(com.tixhi.data.internal.Project, String, int) ProjectHandler.associateStaffToGhost}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerSaveProjectTest {
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws SkillerException {
		projectHandler.addNewProject(new Project (1789, "French revolution"));
	}
	
	@Test
	public void testName() throws SkillerException {
		Project project = new Project (1789, "The great revolution");
		projectHandler.saveProject(new Project (1789, "The great revolution"));
		project = projectHandler.get(1789);
		Assert.assertTrue("The great revolution".equals(project.getName()));
		Assert.assertEquals(0, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}

	@Test
	public void testUsernamePassword() throws SkillerException {

		Project project = projectHandler.get(1789);
		project.setConnectionSettings(1);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setConnectionSettings(1);
		project.setUsername("frvidal");
		project.setPassword("mypass");

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals("https://url.ofASonarServer", project.getUrlSonarServer());
		Assert.assertEquals(1, project.getConnectionSettings());
		Assert.assertEquals("frvidal", project.getUsername());
		Assert.assertNotNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}

	@Test
	public void testConnectionSettingsBackTo0() throws SkillerException {

		Project project = projectHandler.get(1789);
		project.setConnectionSettings(1);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setConnectionSettings(0);
		project.setUsername("frvidal");
		project.setPassword("mypass");

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals("https://url.ofASonarServer", project.getUrlSonarServer());
		Assert.assertEquals(0, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}
	
	@Test
	public void testConnectionSettingsFile() throws SkillerException {
		Project project = projectHandler.get(1789);
		project.setConnectionSettings(1);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(2);
		project.setUsername("frvidal");
		project.setPassword("");
		project.setConnectionSettingsFile("myfile");

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(2, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertEquals("myfile", project.getConnectionSettingsFile());
	}	
	
	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(1789);
				
	}
	
}
