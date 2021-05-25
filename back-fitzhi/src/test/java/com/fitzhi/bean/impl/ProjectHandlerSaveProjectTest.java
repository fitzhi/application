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
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Testing the method {@link ProjectHandler#saveProject(Project)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectHandlerSaveProjectTest {
	
	@Autowired
	ProjectHandler projectHandler;
	
	@Before
	public void before() throws ApplicationException {
		Project project = new Project (1789, "French revolution");
		projectHandler.addNewProject(project);
	}
	
	@Test
	public void testName() throws ApplicationException {
		Project project = new Project (1789, "The great revolution");
		projectHandler.saveProject(new Project (1789, "The great revolution"));
		project = projectHandler.lookup(1789);
		Assert.assertTrue("The great revolution".equals(project.getName()));
		Assert.assertEquals(0, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}

	@Test
	public void testUsernamePassword() throws ApplicationException {

		Project project = projectHandler.lookup(1789);
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
		project = projectHandler.lookup(1789);
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
	public void testConnectionSettingsBackTo0() throws ApplicationException {

		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(USER_PASSWORD_ACCESS);
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(0);

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(0, project.getConnectionSettings());
		Assert.assertNull(project.getUrlSonarServer());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}
	
	@Test
	public void testConnectionSettingsFile() throws ApplicationException {
		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(1);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(REMOTE_FILE_ACCESS);
		project.setUsername("frvidal");
		project.setPassword("");
		project.setConnectionSettingsFile("myfile");

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(REMOTE_FILE_ACCESS, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertEquals("myfile", project.getConnectionSettingsFile());
	}	

	@Test
	public void testConnectionSettingsPublic() throws ApplicationException {
		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(1);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(NO_USER_PASSWORD_ACCESS);

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
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

		Project project = projectHandler.lookup(1789);
		Assert.assertEquals("previous-url", project.getUrlRepository());

		Project projectNew = new Project (1789, "New French revolution");
		projectNew.setConnectionSettings(NO_USER_PASSWORD_ACCESS);
		projectNew.setUrlRepository("new-url");
		projectHandler.saveProject(projectNew);

		project = projectHandler.lookup(1789);
		Assert.assertEquals("New French revolution", project.getName());
		Assert.assertEquals("new-url", project.getUrlRepository());
		Assert.assertNull(project.getLocationRepository());
	}

	@Test
	public void testChangeBranch() throws Exception {

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setLocationRepository("clone-location");
		projectPrevious.setUrlRepository("url");
		projectPrevious.setConnectionSettings(NO_USER_PASSWORD_ACCESS);
		projectPrevious.setBranch("old-branch");
		projectHandler.saveProject(projectPrevious);

		Project project = projectHandler.lookup(1789);
		Assert.assertEquals("old-branch", project.getBranch());

		Project projectNew = new Project (1789, "New French revolution");
		projectNew.setConnectionSettings(NO_USER_PASSWORD_ACCESS);
		projectNew.setUrlRepository("url");
		projectNew.setBranch("new-branch");
		projectHandler.saveProject(projectNew);

		project = projectHandler.lookup(1789);
		Assert.assertEquals("New French revolution", project.getName());
		Assert.assertEquals("url", project.getUrlRepository());
		Assert.assertEquals("new-branch", project.getBranch());
		Assert.assertNull(project.getLocationRepository());
	}

	@Test
	public void testBranchDefaultValueIsNullIfUrlRepositoryIsNull() throws ApplicationException {
		Project project = new Project (1789, "French revolution");
		project.setUrlRepository(null);
		projectHandler.saveProject(project);

		project = projectHandler.lookup(1789);
		Assert.assertNull("project.getBranch()", project.getBranch());
	}

	@Test
	public void testBranchDefaultValueIsMasterIfUrlRepositoryIsNotNull() throws ApplicationException {
		Project project = new Project (1789, "French revolution");
		project.setUrlRepository("url");
		projectHandler.saveProject(project);

		project = projectHandler.lookup(1789);
		Assert.assertEquals("project.getBranch()", "master", project.getBranch());
	}

	@Test
	public void testDoNotForceBranchNameIfUrlRepositoryIsNotNull() throws ApplicationException {
		Project project = new Project (1789, "French revolution");
		project.setUrlRepository("url");
		project.setBranch("branch");
		projectHandler.saveProject(project);

		project = projectHandler.lookup(1789);
		Assert.assertEquals("project.getBranch()", "branch", project.getBranch());
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.getProjects().remove(1789);
	}
	
}
