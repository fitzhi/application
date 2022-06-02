package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.Global;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceCodeDiffChange;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.Token;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.git.SourceChange;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of some methods of {@link HttpDataHandlerImpl}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {"applicationUrl=http://mock-url", "organization=fitzhi" })
@ActiveProfiles("slave")
public class HttpDataHandlerSlaveTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	HttpAccessHandler<Staff> httpAccessHandlerStaff;

	@MockBean
	HttpAccessHandler<Project> httpAccessHandlerProject;

	@MockBean
	HttpAccessHandler<Skill> httpAccessHandlerSkill;

	@MockBean
	HttpAccessHandler<String> httpAccessHandler;

	@MockBean
	HttpConnectionHandler httpConnectionHandler;

	@MockBean
	ShuffleService shuffleService;

	@Value("${applicationOutDirectory}")
	private String saveDir;

	@Before
	public void before() {
		when(httpConnectionHandler.isConnected()).thenReturn(true);
		when(httpConnectionHandler.getToken()).thenReturn(new Token("access_token", "refresh_token", "token_type", 100, "scope"));
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveProjects() throws Exception {
		dataHandler.saveProjects(new HashMap<>());
	}

	@Test
	public void loadProjects() throws ApplicationException {
		List<Project> projects = new ArrayList<>();
		projects.add(new Project(1, "one"));
		projects.add(new Project(2, "two"));
		when(httpAccessHandlerProject.loadList(anyString(), any())).thenReturn(projects);
		
		Map<Integer, Project> res = dataHandler.loadProjects();
		Assert.assertEquals(2, res.size());
		Assert.assertEquals("one", res.get(1).getName());
		Assert.assertEquals("two", res.get(2).getName());
	}

	@Test (expected = ApplicationException.class)
	public void loadProjectsInError() throws ApplicationException {
		when(httpAccessHandlerProject.loadList(anyString(), any())).thenThrow(new ApplicationException());
		dataHandler.loadProjects();
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveStaff() throws ApplicationException {
		dataHandler.saveStaff(new HashMap<>());
	}

	@Test
	public void loadStaff() throws IOException, ApplicationException {
		List<Staff> theStaff = new ArrayList<>();
		theStaff.add(new Staff(1, "firstName", "lastName", "nickName", "login", "email", "level"));
		when(httpAccessHandlerStaff.loadList(anyString(), any())).thenReturn(theStaff);
		
		Map<Integer, Staff> res = dataHandler.loadStaff();
		Assert.assertEquals(1, res.size());
	}

	@Test (expected = ApplicationException.class)
	public void loadStaffInError() throws IOException, ApplicationException {
		when(httpAccessHandlerStaff.loadList(anyString(), any())).thenThrow(new ApplicationException());
		dataHandler.loadStaff();
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveSkills() throws ApplicationException {
		dataHandler.saveSkills(new HashMap<>());		
	}

	@Test
	public void saveRepositoryAnalysis() throws ApplicationException {
		Project p = new Project();
		RepositoryAnalysis analysis = new RepositoryAnalysis(p);

		DataHandler spyDataHandler = spy(dataHandler);
		doNothing().when(spyDataHandler).saveChanges(any(Project.class), any(SourceControlChanges.class));
		spyDataHandler.saveRepositoryAnalysis(p, analysis);
		verify(spyDataHandler, times(1)).saveChanges(any(Project.class), any(SourceControlChanges.class));
	}

	@Test (expected = NotFoundException.class)
	public void loadRepositoryAnalysis() throws ApplicationException {
		dataHandler.loadRepositoryAnalysis(new Project(1789, "Not found"));
	}

	@Test
	public void serializeEmptyChanges() throws ApplicationException {
		SourceControlChanges changes = new SourceControlChanges();
		String s = HttpDataHandlerImpl.serializeChanges(changes);
		Assert.assertEquals("\"Commit\";\"Path\";\"Date\";\"Author\";\"Email\";\"diff\"" + Global.LN, s);
	}

	@Test
	public void serializeChanges() throws ApplicationException {
		SourceControlChanges changes = new SourceControlChanges();
		changes.addChange("fullPath-one", 
			new SourceChange("cmt-one", LocalDate.of(2022, 1, 1), "frunknown", "frunknown@nope.com"));
		changes.addChange("fullPath-two", 
			new SourceChange("cmt-two", LocalDate.of(2022, 1, 1), "frunknown", "frunknown@nope.com", 1, new SourceCodeDiffChange("file-one", 2, 10)));
		String s = HttpDataHandlerImpl.serializeChanges(changes);

		final String expected = "\"Commit\";\"Path\";\"Date\";\"Author\";\"Email\";\"diff\"" + Global.LN +
			"\"cmt-two\";\"fullPath-two\";\"2022-01-01\";\"frunknown\";\"frunknown@nope.com\";\"8\"" + Global.LN +
			"\"cmt-one\";\"fullPath-one\";\"2022-01-01\";\"frunknown\";\"frunknown@nope.com\";\"0\"" + Global.LN ;

		Assert.assertEquals(expected, s);
	}

	@Test
	public void deserializeChanges() throws ApplicationException {

		final String input = "\"Commit\";\"Path\";\"Date\";\"Author\";\"Email\";\"diff\"" + Global.LN +
			"\"cmt-two\";\"fullPath-two\";\"2022-01-01\";\"frunknown\";\"frunknown@nope.com\";\"8\"" + Global.LN +
			"\"cmt-one\";\"fullPath-one\";\"2022-01-01\";\"frunknown\";\"frunknown@nope.com\";\"0\"" + Global.LN ;

		SourceControlChanges changes = HttpDataHandlerImpl.deserializeChanges(input);
		Assert.assertEquals(2, changes.entrySet().size());
		Assert.assertNotNull(changes.getChanges().get("fullPath-two"));

//			new SourceChange("cmt-two", LocalDate.of(2022, 1, 1), "frunknown", "frunknown@nope.com", 1, new SourceCodeDiffChange("file-one", 2, 10)));
	}
	

	@Test
	public void saveChangesOk() throws ApplicationException {
		when(httpAccessHandler.put(
			"/api/project/1789/changes", 
			"\"Commit\";\"Path\";\"Date\";\"Author\";\"Email\";\"diff\"" + Global.LN, 
			new TypeReference<String>(){})).thenReturn("the Server response");
		dataHandler.saveChanges(new Project(1789, "The French revolution"), new SourceControlChanges());
		verify(httpAccessHandler, times(1)).put(anyString(), anyString(), any());
	}

	@Test (expected = ApplicationException.class)
	public void saveChangesKo() throws ApplicationException {
		when(httpAccessHandler.put(anyString(), anyString(), any())).thenThrow(new ApplicationException());
		dataHandler.saveChanges(new Project(1789, "The French revolution"), new SourceControlChanges());
		verify(httpAccessHandler, times(1)).put(anyString(), anyString(), any());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadChanges() throws ApplicationException {
		dataHandler.loadChanges(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveDetectedExperiences() throws ApplicationException {
		dataHandler.saveDetectedExperiences(new Project(), new ProjectDetectedExperiences());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadDetectedExperiences() throws ApplicationException {
		dataHandler.loadDetectedExperiences(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void savePaths() throws ApplicationException {
		dataHandler.savePaths(new Project(), new ArrayList<>(), PathsType.PATHS_ALL);
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadPaths()
			throws ApplicationException {
		dataHandler.loadPaths(new Project(), PathsType.PATHS_ALL);
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveSkylineLayers() throws ApplicationException {
		Project p = new Project();
		dataHandler.saveSkylineLayers(p, new ProjectLayers(p));
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadSkylineLayers() throws ApplicationException {
		dataHandler.loadSkylineLayers(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void hasSavedSkylineLayers() {
		dataHandler.hasSavedSkylineLayers(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveProjectBuilding() throws ApplicationException {
		dataHandler.saveProjectBuilding(new Project(), new ProjectBuilding());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadProjectBuilding() throws ApplicationException {
		dataHandler.loadProjectBuilding(new Project());
	}

	@Test
	public void testIsLocal() {
		Assert.assertFalse(dataHandler.isLocal());
	}

	@Test
	public void loadSkills() throws ApplicationException {
		List<Skill> skills = new ArrayList<>();
		skills.add( new Skill(1, "one"));
		skills.add(new Skill(2, "two"));
		when(httpAccessHandlerSkill.loadList(anyString(), any())).thenReturn(skills);
		
		Map<Integer, Skill> res = dataHandler.loadSkills();
		Assert.assertEquals(2, res.size());
		Assert.assertEquals("two", res.get(2).getTitle());
	}

	@Test (expected = ApplicationException.class)
	public void loadSkillsInError() throws ApplicationException {
		when(httpAccessHandlerSkill.loadList(anyString(), any())).thenThrow(new ApplicationException());
		dataHandler.loadSkills();
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveRepositoryDirectories() throws ApplicationException {
		dataHandler.saveRepositoryDirectories(new Project(), new SourceControlChanges());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadRepositoryDirectories() throws ApplicationException {
		dataHandler.loadRepositoryDirectories(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void generatePathnamesFile() throws ApplicationException {
		dataHandler.generatePathnamesFile(new Project(), PathsType.PATHS_ALL);
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void removeCrawlerFiles() throws ApplicationException {
		dataHandler.removeCrawlerFiles(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void hasAlreadySavedSkillsConstellations() throws ApplicationException {
		dataHandler.hasAlreadySavedSkillsConstellations(LocalDate.now());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveSkillsConstellations() throws ApplicationException {
		dataHandler.saveSkillsConstellations(LocalDate.now(), new ArrayList<>());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadSkillsConstellations() throws ApplicationException {
		dataHandler.loadSkillsConstellations(LocalDate.now());
	}

}
