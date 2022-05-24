package com.fitzhi.bean.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Implementation of DataHandler based on HTTP interaction. 
 * This implementation is planned to be used bythe slave mode.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Profile("slave")
@Service
public class HttpDataHandlerImpl<T> implements DataHandler {

	/**
	 * URL of the backend which hosts the main application.
	 */
	@Value("${applicationUrl}")
	private String applicationUrl;

	/**
	 * Organization name. This name is unique and therefore can be considered as an ID.
	 */
	@Value("${organization}")
	private String organization;

	@Autowired
	HttpAccessHandler<Staff> httpAccessStaff; 

	@Autowired
	HttpAccessHandler<Project> httpAccessProject; 

	@Autowired
	HttpAccessHandler<Skill> httpAccessSkill; 

	@Override
	public void saveProjects(Map<Integer, Project> projects) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public Map<Integer, Project> loadProjects() throws ApplicationException {
		String url = applicationUrl + "/api/project";
		List<Project> projects = httpAccessProject.loadList(url, new TypeToken<List<Project>>() {});
		Map<Integer, Project> map = new HashMap<>();
		projects.forEach(p -> map.put(p.getId(), p));
		return map;
	}

	@Override
	public void saveStaff(Map<Integer, Staff> staff) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public Map<Integer, Staff> loadStaff() throws ApplicationException {
		String url = applicationUrl + "/api/staff";
		return httpAccessStaff.loadMap(url, new TypeToken<Map<Integer, Staff>>() {});
	}

	@Override
	public void saveSkills(Map<Integer, Skill> staff) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveRepositoryAnalysis(Project project, RepositoryAnalysis analysis) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public RepositoryAnalysis loadRepositoryAnalysis(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveChanges(Project project, SourceControlChanges changes) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public SourceControlChanges loadChanges(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveDetectedExperiences(Project project, ProjectDetectedExperiences experiences)
			throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public ProjectDetectedExperiences loadDetectedExperiences(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void savePaths(Project project, List<String> paths,
			com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType pathsType) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public List<String> loadPaths(Project project, com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType pathsType)
			throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveSkylineLayers(Project project, ProjectLayers layers) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public ProjectLayers loadSkylineLayers(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public boolean hasSavedSkylineLayers(Project project) {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveProjectBuilding(Project project, ProjectBuilding building) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public ProjectBuilding loadProjectBuilding(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public Map<Integer, Skill> loadSkills() throws ApplicationException {
		String url = applicationUrl + "/api/skill";
		return httpAccessSkill.loadMap(url, new TypeToken<Map<Integer, Skill>>() {});
	}

	@Override
	public void saveRepositoryDirectories(Project project, SourceControlChanges changes) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public List<String> loadRepositoryDirectories(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public String generatePathnamesFile(Project project, com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType pathsType)
			throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void removeCrawlerFiles(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public boolean hasAlreadySavedSkillsConstellations(LocalDate month) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveSkillsConstellations(LocalDate month, List<Constellation> constellations)
			throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public List<Constellation> loadSkillsConstellations(LocalDate month) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

}
