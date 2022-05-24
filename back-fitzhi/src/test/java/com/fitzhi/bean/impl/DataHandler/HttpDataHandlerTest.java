package com.fitzhi.bean.impl.DataHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
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
public class HttpDataHandlerTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	HttpAccessHandler<Staff> httpAccessHandlerStaff;

	@MockBean
	HttpAccessHandler<Project> httpAccessHandlerProject;

	@MockBean
	HttpAccessHandler<Skill> httpAccessHandlerSkill;

	@MockBean
	ShuffleService shuffleService;

	@Value("${applicationOutDirectory}")
	private String saveDir;
	
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
		Map<Integer, Staff> theStaff = new HashMap<>();
		theStaff.put(1, new Staff(1, "firstName", "lastName", "nickName", "login", "email", "level"));
		when(httpAccessHandlerStaff.loadMap(anyString(), any())).thenReturn(theStaff);
		
		Map<Integer, Staff> res = dataHandler.loadStaff();
		Assert.assertEquals(1, res.size());
	}

	@Test (expected = ApplicationException.class)
	public void loadStaffInError() throws IOException, ApplicationException {
		when(httpAccessHandlerStaff.loadMap(anyString(), any())).thenThrow(new ApplicationException());
		dataHandler.loadStaff();
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveSkills() throws ApplicationException {
		dataHandler.saveSkills(new HashMap<>());		
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveRepositoryAnalysis() throws ApplicationException {
		Project p = new Project();
		dataHandler.saveRepositoryAnalysis(p, new RepositoryAnalysis(p));		
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadRepositoryAnalysis() throws ApplicationException {
		dataHandler.loadRepositoryAnalysis(new Project());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveChanges() throws ApplicationException {
		dataHandler.saveChanges(new Project(), new SourceControlChanges());
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
	public void loadSkills() throws ApplicationException {
		Map<Integer, Skill> skills = new HashMap<>();
		skills.put(1, new Skill(1, "one"));
		skills.put(2, new Skill(2, "two"));
		when(httpAccessHandlerSkill.loadMap(anyString(), any())).thenReturn(skills);
		
		Map<Integer, Skill> res = dataHandler.loadSkills();
		Assert.assertEquals(2, res.size());
		Assert.assertEquals("two", res.get(2).getTitle());
	}

	@Test (expected = ApplicationException.class)
	public void loadSkillsInError() throws ApplicationException {
		when(httpAccessHandlerSkill.loadMap(anyString(), any())).thenThrow(new ApplicationException());
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
