/**
 * 
 */
package com.fitzhi.bean.impl;

import static com.fitzhi.Global.NO_USER_PASSWORD_ACCESS;
import static com.fitzhi.Global.REMOTE_FILE_ACCESS;
import static com.fitzhi.Global.USER_PASSWORD_ACCESS;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;

/**
 * <p>
 * Test the method {@link ProjectHandler#associateStaffToGhost(com.fitzhi.data.internal.Project, String, int) ProjectHandler.associateStaffToGhost}
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
		Project project = new Project (1789, "French revolution");
		projectHandler.addNewProject(project);
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
		project.setConnectionSettings(USER_PASSWORD_ACCESS);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setBranch("theBranchName");
		project.setUrlCodeFactorIO("https://url.ofCodeFactor.io");
		project.setConnectionSettings(USER_PASSWORD_ACCESS);
		project.setUsername("frvidal");
		project.setPassword("mypass");

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals("https://url.ofASonarServer", project.getUrlSonarServer());
		Assert.assertEquals("theBranchName", project.getBranch());
		Assert.assertEquals("https://url.ofCodeFactor.io", project.getUrlCodeFactorIO());
		Assert.assertEquals(USER_PASSWORD_ACCESS, project.getConnectionSettings());
		Assert.assertEquals("frvidal", project.getUsername());
		Assert.assertNotNull(project.getPassword());
		
		String encryptedPassword = DataEncryption.encryptMessage("mypass");
		Assert.assertEquals(encryptedPassword, project.getPassword());
		
		Assert.assertNull(project.getConnectionSettingsFile());
	}

	@Test
	public void testConnectionSettingsBackTo0() throws SkillerException {

		Project project = projectHandler.get(1789);
		project.setConnectionSettings(USER_PASSWORD_ACCESS);
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(0);

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(0, project.getConnectionSettings());
		Assert.assertNull(project.getUrlSonarServer());
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
		project.setConnectionSettings(REMOTE_FILE_ACCESS);
		project.setUsername("frvidal");
		project.setPassword("");
		project.setConnectionSettingsFile("myfile");

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(REMOTE_FILE_ACCESS, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertEquals("myfile", project.getConnectionSettingsFile());
	}	

	@Test
	public void testConnectionSettingsPublic() throws SkillerException {
		Project project = projectHandler.get(1789);
		project.setConnectionSettings(1);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(NO_USER_PASSWORD_ACCESS);

		projectHandler.saveProject(project);
		project = projectHandler.get(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(NO_USER_PASSWORD_ACCESS, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
	}	
	
	@Test
	public void testChangeUrlRepository() throws Exception {

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setLocationRepository("previous-clone-location");
		projectPrevious.setUrlRepository("previous-url");
		projectPrevious.setConnectionSettings(NO_USER_PASSWORD_ACCESS);
		projectHandler.saveProject(projectPrevious);

		Project project = projectHandler.get(1789);
		Assert.assertEquals("previous-url", project.getUrlRepository());

		Project projectNew = new Project (1789, "New French revolution");
		projectNew.setConnectionSettings(NO_USER_PASSWORD_ACCESS);
		projectNew.setUrlRepository("new-url");
		projectHandler.saveProject(projectNew);

		project = projectHandler.get(1789);
		Assert.assertEquals("New French revolution", project.getName());
		Assert.assertEquals("new-url", project.getUrlRepository());
		Assert.assertNull(project.getLocationRepository());
	}

	@After
	public void after() throws SkillerException {
		projectHandler.getProjects().remove(1789);
	}
	
}
