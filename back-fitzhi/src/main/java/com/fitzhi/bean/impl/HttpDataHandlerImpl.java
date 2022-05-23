package com.fitzhi.bean.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.DataHandler;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * Implementation of DataSaver based on HTTP interaction. 
 * This implementation is planned to be used bythe slave mode.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Profile("slave")
@Service
public class HttpDataHandlerImpl implements DataHandler {

	/**
	 * <p>Type of path</p>
	 * <p>
	 * Application stores different types of paths on filesystem in order to re-gerenerate a consistent {@link RepositoryAnalysis}
	 * </p>
	 */
	public enum PathsType {    
		PATHS_ALL("pathsAll"), PATHS_MODIFIED("pathsModified"), PATHS_CANDIDATE("pathsCandidate"), PATHS_ADDED("pathsAdded");

		String typeOfPath;
		
		private PathsType(String typeOfPath) {  
			this.typeOfPath = typeOfPath ;  
		}
		
		public String getTypeOfPath() {
			return this.typeOfPath;
		}		
	}

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	@Override
	public void saveProjects(Map<Integer, Project> projects) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public Map<Integer, Project> loadProjects() throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public void saveStaff(Map<Integer, Staff> staff) throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
	}

	@Override
	public Map<Integer, Staff> loadStaff() throws ApplicationException {
		throw new ApplicationRuntimeException("Not implemented yet");
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
		throw new ApplicationRuntimeException("Not implemented yet");
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
