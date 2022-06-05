package com.fitzhi.bean.impl.ProjectHandler;

import static com.fitzhi.service.ConnectionSettingsType.DIRECT_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.NO_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.PUBLIC_LOGIN;
import static com.fitzhi.service.ConnectionSettingsType.REMOTE_FILE_LOGIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.CacheDataHandler;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.encryption.DataEncryption;
import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

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
	
	@MockBean StaffHandler staffHandler;

	@MockBean DataHandler dataHandler;

	@MockBean CacheDataHandler cacheDataHandler;
	
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
		Assert.assertEquals(NO_LOGIN, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}

	@Test
	public void testUsernamePassword() throws ApplicationException {

		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(DIRECT_LOGIN);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setBranch("theBranchName");
		project.setUrlCodeFactorIO("https://url.ofCodeFactor.io");
		project.setConnectionSettings(DIRECT_LOGIN);
		project.setUsername("frvidal");
		project.setPassword("mypass");

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals("https://url.ofASonarServer", project.getUrlSonarServer());
		Assert.assertEquals("theBranchName", project.getBranch());
		Assert.assertEquals("https://url.ofCodeFactor.io", project.getUrlCodeFactorIO());
		Assert.assertEquals(DIRECT_LOGIN, project.getConnectionSettings());
		Assert.assertEquals("frvidal", project.getUsername());
		Assert.assertNotNull(project.getPassword());
		
		String encryptedPassword = DataEncryption.encryptMessage("mypass");
		Assert.assertEquals(encryptedPassword, project.getPassword());
		
		Assert.assertNull(project.getConnectionSettingsFile());
	}

	@Test
	public void testConnectionSettingsBackTo0() throws ApplicationException {

		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(DIRECT_LOGIN);
		project.setUrlSonarServer("https://url.ofASonarServer");
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(NO_LOGIN);

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(NO_LOGIN, project.getConnectionSettings());
		Assert.assertNull(project.getUrlSonarServer());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertNull(project.getConnectionSettingsFile());
	}
	
	@Test
	public void testConnectionSettingsFile() throws ApplicationException {
		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(DIRECT_LOGIN);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(REMOTE_FILE_LOGIN);
		project.setUsername("frvidal");
		project.setPassword("");
		project.setConnectionSettingsFile("myfile");

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(REMOTE_FILE_LOGIN, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
		Assert.assertEquals("myfile", project.getConnectionSettingsFile());
	}	

	@Test
	public void testConnectionSettingsPublic() throws ApplicationException {
		Project project = projectHandler.lookup(1789);
		project.setConnectionSettings(DIRECT_LOGIN);
		project.setUsername("user_nope");
		project.setPassword("pass_nope");
		
		project = new Project (1789, "French revolution");
		project.setConnectionSettings(PUBLIC_LOGIN);

		projectHandler.saveProject(project);
		project = projectHandler.lookup(1789);
		Assert.assertTrue("French revolution".equals(project.getName()));
		Assert.assertEquals(PUBLIC_LOGIN, project.getConnectionSettings());
		Assert.assertNull(project.getUsername());
		Assert.assertNull(project.getPassword());
	}	
	
	@Test
	public void testChangeUrlRepository() throws Exception {

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setLocationRepository("previous-clone-location");
		projectPrevious.setUrlRepository("previous-url");
		projectPrevious.setConnectionSettings(PUBLIC_LOGIN);
		projectHandler.saveProject(projectPrevious);

		Project project = projectHandler.lookup(1789);
		Assert.assertEquals("previous-url", project.getUrlRepository());

		Project projectNew = new Project (1789, "New French revolution");
		projectNew.setConnectionSettings(PUBLIC_LOGIN);
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
		projectPrevious.setConnectionSettings(PUBLIC_LOGIN);
		projectPrevious.setBranch("old-branch");
		projectHandler.saveProject(projectPrevious);

		Project project = projectHandler.lookup(1789);
		Assert.assertEquals("old-branch", project.getBranch());

		Project projectNew = new Project (1789, "New French revolution");
		projectNew.setConnectionSettings(PUBLIC_LOGIN);
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
	public void removeMissionsWhenChangingBranchName() throws Exception {

		doNothing().when(staffHandler).removeProject(any(Integer.class));

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setBranch("old-branch");
		projectHandler.saveProject(projectPrevious);
		
		Project projectNew = new Project (1789, "French revolution");
		projectNew.setBranch("new-branch");
		projectHandler.saveProject(projectNew);

		verify(staffHandler, times(1)).removeProject(any(Integer.class));

	}

	@Test
	public void removeCrawlerFilesWhenChangingBranchName() throws Exception {

		doNothing().when(dataHandler).removeCrawlerFiles(any(Project.class));

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setBranch("old-branch");
		projectHandler.saveProject(projectPrevious);
		
		Project projectNew = new Project (1789, "French revolution");
		projectNew.setBranch("new-branch");
		projectHandler.saveProject(projectNew);

		verify(dataHandler, times(1)).removeCrawlerFiles(any(Project.class));

	}

	@Test
	public void removeSunburstDataWhenChangingBranchName() throws Exception {

		when(cacheDataHandler.removeRepository(any(Project.class))).thenReturn(true);

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setBranch("old-branch");
		projectHandler.saveProject(projectPrevious);
		
		Project projectNew = new Project (1789, "French revolution");
		projectNew.setBranch("new-branch");
		projectHandler.saveProject(projectNew);

		verify(cacheDataHandler, times(1)).removeRepository(any(Project.class));

	}

	@Test
	public void removeSunburstDataWhenChangingRepositoryUrl() throws Exception {

		when(cacheDataHandler.removeRepository(any(Project.class))).thenReturn(true);

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setBranch("master");
		projectPrevious.setConnectionSettings(PUBLIC_LOGIN);
		projectPrevious.setUrlRepository("url");
		projectHandler.saveProject(projectPrevious);
		
		Project projectNew = new Project (1789, "French revolution");
		projectNew.setBranch("master");
		projectNew.setConnectionSettings(PUBLIC_LOGIN);
		projectNew.setUrlRepository("new url");
		projectHandler.saveProject(projectNew);

		verify(cacheDataHandler, times(1)).removeRepository(any(Project.class));

	}

	/**
	 * We cleaunp the crawler files only when the branch name is changed
	 * @throws Exception
	 */
	@Test
	public void doNotRemoveMissionsWhenChangingInertProjectData() throws Exception {

		doNothing().when(staffHandler).removeProject(any(Integer.class));

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setBranch("master");
		projectPrevious.setConnectionSettings(PUBLIC_LOGIN);
		projectPrevious.setUrlRepository("url");
		projectHandler.saveProject(projectPrevious);
		
		// We rename the french revolution.
		Project projectNew = new Project (1789, "NEW French revolution");
		projectNew.setBranch("master");
		projectNew.setConnectionSettings(PUBLIC_LOGIN);
		projectNew.setUrlRepository("url");
		projectHandler.saveProject(projectNew);

		verify(staffHandler, never()).removeProject(any(Integer.class));

	}

	/**
	 * We cleaunp the crawler files only when the branch name is changed
	 * @throws Exception
	 */
	@Test
	public void removeCrawlerFilesWhenChangingUrl() throws Exception {

		doNothing().when(dataHandler).removeCrawlerFiles(any(Project.class));

		Project projectPrevious = new Project (1789, "French revolution");
		projectPrevious.setBranch("master");
		projectPrevious.setConnectionSettings(PUBLIC_LOGIN);
		projectPrevious.setUrlRepository("old-url");
		projectHandler.saveProject(projectPrevious);
		
		Project projectNew = new Project (1789, "French revolution");
		projectNew.setBranch("master");
		projectNew.setConnectionSettings(PUBLIC_LOGIN);
		projectNew.setUrlRepository("new-url");
		projectHandler.saveProject(projectNew);

		verify(dataHandler, times(1)).removeCrawlerFiles(any(Project.class));

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

	@Test(expected = ApplicationRuntimeException.class)
	public void projectNotFound() throws ApplicationException {
		projectHandler.saveProject(new Project(17890000, "name"));
	}

	@Test(expected = ApplicationException.class)
	public void projectWithId0() throws ApplicationException {
		projectHandler.saveProject(new Project(0, "zero"));
	}

	@After
	public void after() throws ApplicationException {
		projectHandler.removeProject(1789);
	}
	
}
