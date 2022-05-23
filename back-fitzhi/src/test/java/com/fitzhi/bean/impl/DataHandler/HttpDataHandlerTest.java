package com.fitzhi.bean.impl.DataHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType;
import com.fitzhi.bean.impl.HttpDataHandlerImpl;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.exception.ApplicationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of some methods of {@link HttpDataHandlerImpl}.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@ActiveProfiles("slave")
public class HttpDataHandlerTest {
 
	@Autowired
	DataHandler dataHandler;

	@MockBean
	ShuffleService shuffleService;

	@Value("${applicationOutDirectory}")
	private String saveDir;

	@Test (expected = ApplicationRuntimeException.class)
	public void saveProjects() throws Exception {
		dataHandler.saveProjects(new HashMap<>());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadProjects() throws ApplicationException {
		dataHandler.loadProjects();
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void saveStaff() throws ApplicationException {
		dataHandler.saveStaff(new HashMap<>());
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void loadStaff() throws ApplicationException {
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

	@Test (expected = ApplicationRuntimeException.class)
	public void loadSkills() throws ApplicationException {
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
