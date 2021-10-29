package com.fitzhi.bean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

/**
 * Interface in charge of saving & loading data.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface DataHandler {

	/**
	 * Save projects on a persistent media
	 * @param projects list of projects
	 * @throws ApplicationException thrown if exception occurs during the saving process
	 */
	void saveProjects(Map<Integer, Project> projects) throws ApplicationException;
	
	
	/**
	 * Load the projects from a persistent media
	 * @return the map of projects
	 * @throws ApplicationException thrown if exception occurs during the saving process
	 */
	Map<Integer, Project> loadProjects() throws ApplicationException;

	/**
	 * Save the staff on a persistent media
	 * @param staff list of staff
	 * @throws ApplicationException thrown if exception occurs during the saving process
	 */
	void saveStaff(Map<Integer, Staff> staff) throws ApplicationException;
	
	/**
	 * Load the staff members from a persistent media
	 * @return the staff
	 * @throws ApplicationException thrown if exception occurs during the saving process
	 */
	Map<Integer, Staff> loadStaff() throws ApplicationException;

	/**
	 * Save the skills <i>(probably for this first release)</i> on the file system
	 * @param staff list of staff
	 * @throws ApplicationException thrown if an exception occurs during the saving process
	 */
	void saveSkills(Map<Integer, Skill> staff) throws ApplicationException;
	
	/**
	 * <p>
	 * Save the analysis on file system in {@code CSV} format.
	 * </p>
	 * <p><i>Due to DEBUG purpose, the output format will be <b>{@code CSV}</b></i>.</p>
	 * @param project project whose repository analysis has to be serialized
	 * @param analysis the analysis to serialize on file system.
	 * @throws ApplicationException thrown if an exception occurs during the saving process, most probably an {@link IOException}.
	 */
	void saveRepositoryAnalysis(Project project, RepositoryAnalysis analysis) throws ApplicationException;

	/**
	 * <p>
	 * Load the analysis from file system.
	 * </p>
	 * @param project project whose changes have to be serialized in CSV
	 * @return the {@link RepositoryAnalysis analysis} if found, or {@code null} if none exists on file system.
	 * @throws ApplicationException thrown if an exception occurs during the loading process, most probably an {@link IOException}
	 */
	RepositoryAnalysis loadRepositoryAnalysis(Project project) throws ApplicationException;

	/**
	 * <p>
	 * Save the source control changes loaded from the repository for cache & debug purpose.
	 * </p>
	 * <p><i>Due to the DEBUG purpose of this file, the output format will <b>CSV</b></i>.</p>
	 * @param project project whose changes have to be serialized in CSV
	 * @param changes changes retrieved from the repository
	 * @throws ApplicationException thrown if an exception occurs during the saving process
	 */
	void saveChanges(Project project, SourceControlChanges changes) throws ApplicationException;

	/**
	 * <p>
	 * Load the changes stored in a CSV file.
	 * </p>
	 * @param project the current active project
	 * @return the container of all commits changes, or {@code null} if the changes file does not exist
	 * @throws ApplicationException thrown if an exception occurs during the loading process, most probably an {@link java.io.IOException}
	 */
	SourceControlChanges loadChanges(Project project) throws ApplicationException;

	/**
	 * <p>
	 * Save the detected experiences.
	 * </p>
	 * @param project project whose {@link DetectedExperience experiences} have to be saved.
	 * @param experiences the container of the {@link DetectedExperience detected experiences}
	 * @throws ApplicationException thrown if an exception occurs during the saving process, most probably an {@link IOException}
	 */
	void saveDetectedExperiences(Project project, ProjectDetectedExperiences experiences) throws ApplicationException;

	/**
	 * <p>
	 * Load the detected experiences.
	 * </p>
	 * @param project project whose {@link DetectedExperience experiences} have to be loaded.
	 * @return a container of {@link DetectedExperience detected experiences}, or {@code null} if the backup file does not exist.
	 * @throws ApplicationException thrown if an exception occurs during the saving process, most probably an {@link IOException}
	 */
	ProjectDetectedExperiences loadDetectedExperiences(Project project) throws ApplicationException;

	/**
	 * <p>
	 * Save a collection of paths on File System. The main goal for this method, is to store the states of the {@link RepositoryAnalysis analysis container}  on file system.
	 * </p>
	 * @param project the current projet for which these paths should be saved. 
	 * @param paths a list of paths to be saved. 
	 * @param pathsType the {@link com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType kind of path} .
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 * @see #loadPaths(Project, PathsType)
	 * @see PathsType
	 */
	void savePaths(Project project, List<String> paths, PathsType pathsType) throws ApplicationException;

	/**
	 * <p>
	 * Load the corresponding collection of paths saved previously on the File System. 
	 * The main goal for this method, is to store the states of the {@link RepositoryAnalysis analysis container}  on file system.
	 * </p>.
	 * @param project the current projet for which these paths should be saved. 
	 * @param pathsType the {@link com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType kind of path} .
	 * @return the loaded paths retrieved on file system, or {@code null} if none exists.
	 * @see PathsType
	 * @see #savePaths(Project, List, PathsType)
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 */
	List<String> loadPaths(Project project, PathsType pathsType) throws ApplicationException;

	/**
	 * <p>
	 * Save the project layers generated by {@link SkylineProcessor#generateProjectLayers(Project, SourceControlChanges)} in a {@code CSV} file. 
	 * These data will be used when generating the skyline.
	 * </p>
	 * @param project project whose skyline layers have to be serialized in CSV
	 * @param layers the container of skyline project layers to be saved on the file system.
	 * @throws ApplicationException thrown if an exception occurs during the loading process, mot probably an {@link java.io.IOException}
	 */
	 void saveSkylineLayers(Project project, ProjectLayers layers) throws ApplicationException;

	/**
	 * <p>
	 * Load the project layers previously generated by generated by {@link SkylineProcessor#generateProjectLayers(Project, SourceControlChanges)} 
	 * from the file system.
	 * These data will be used when generating the skyline.
	 * </p>
	 * @param project project whose skyline layers have to be serialized in CSV
	 * @return the list of skyline layers reader to be uploaded on the filesystem..
	 * @throws ApplicationException thrown if an exception occurs during the loading process, mot probably an {@link java.io.IOException}
	 */
	ProjectLayers loadSkylineLayers(Project project) throws ApplicationException;

	/**
	 * <p>
	 * Test if the given project has already saved its skyline.
	 * </p>
	 * @param project project whose skyline layers might have to be saved on the filesytem
	 * @return {@code true} if this project has already saved its {@code (n)-project-layers.json} file, {@code false} otherwise.
	 */
	boolean hasSavedSkylineLayers(Project project);

	/**
	 * <p>
	 * Save the project building previously generated by generated by {@link SkylineProcessor#generateProjectBuilding(Project)} 
	 * on the file system.
	 * This building will be part of the skyline.
	 * </p>
	 * @param project the project whose building have to be serialized in CSV and saved on the filesytem.
	 * @param ProjectBuilding the generated building to be saved
	 * @throws ApplicationException thrown if an exception occurs during the loading process, mot probably an {@link java.io.IOException}
	 */
	void saveProjectBuilding(Project project, ProjectBuilding building) throws ApplicationException;

	/**
	 * <p>
	 * Load the project building previously generated by generated by {@link SkylineProcessor#generateProjectBuilding(Project)}
	 * from the file system.
	 * The returned building will be part of the skyline.
	 * </p>
	 * @param project project whose building has to be serialized, and saved on the file system.
	 * @return the Project building
	 * @throws ApplicationException thrown if an exception occurs during the loading process, mot probably an {@link java.io.IOException}
	 */
	ProjectBuilding loadProjectBuilding(Project project) throws ApplicationException;

	/**
	 * Load the skills <i>(probably for this first release)</i> from the file system
	 * @return the skills collection, retrieved from the file system
	 * @throws ApplicationException thrown if an exception occurs during the saving process
	 */
	Map<Integer, Skill> loadSkills() throws ApplicationException;
	
	/**
	 * <p>
	 * Save the repository directories.<br/>
	 * This file is a raw text file with all distinct directories.
	 * </p>
	 * <p>
	 * The resulting file will be used by the dependencies eviction form 
	 * <i>(table-dependencies.component in the Angular project)</i>.
	 * </p>
	 * @param project the current project
	 * @param changes the history of changes retrieved from the repository
	 * @throws ApplicationException thrown if an exception occurs during the saving process.
	 */
	void saveRepositoryDirectories(Project project, SourceControlChanges changes) throws ApplicationException;
	
	/**
	 * <p>
	 * Extract the list with all directories loaded the local repository for the given project.
	 * <p>
	 * <p>
	 * Each of these directories might host external libraries, and therefore should be excluded from the analysis. 
	 * This list is used when selecting these external paths. 
	 * </p>
	 * @param project the given project
	 * @return the resulting paths list
	 * @throws ApplicationException thrown if an exception occurs during the loading process.
	 */
	List<String> loadRepositoryDirectories (Project project) throws ApplicationException;
	
	/**
	 * Generate the file path for pathnames of a given type of {@link PathType path}
	 * 
	 * @param project the current project
	 * @param pathsType type of paths which will be saved on file system  
	 * @return the generated pathname to be used to store the data
	 * @throws ApplicationException thrown if any problem occurs, most probably the branch name is empty. 
	 */
	String generatePathnamesFile(Project project, PathsType pathsType) throws ApplicationException;

	/**
	 * Remove all intermediate files generated during the analysis of the GIT repository for the given project
	 * @param project the given project
	 * @throws ApplicationException thrown if an exception occurs during the remove process, most probably an IOException.
	 */
	void removeCrawlerFiles(Project project) throws ApplicationException;

	/**
	 * Test if a {@link Constellation constellation} has been already saved for the given month.
	 * @param month the month
	 * @return {@code true} if the given month has aleady been saved, {@code false} otherwise.
	 * @throws ApplicationException thrown if any exception occurs during the test, most probably an {@link IOException}.
	 */
	boolean hasAlreadySavedSkillsConstellations(LocalDate month) throws ApplicationException;
	
	/**
	 * Save the skills constellations for the given month.
	 * @param month the saving month
	 * @param constellations the given list of {@link Constellation constellations}
	 * @throws ApplicationException thrown if any exception occurs during the saving process, most probably an {@link IOException}.
	 */
	void saveSkillsConstellations(LocalDate month, List<Constellation> constellations) throws ApplicationException;

	/**
	 * Load the skills constellations associated with the given month.
	 * @param month the month to retrieve in the history
	 * @return the retrieved list of {@link Constellation constellations}.
	 * @throws ApplicationException thrown if any exception occurs during the load process, most probably an {@link IOException}.
	 */
	List<Constellation> loadSkillsConstellations(LocalDate month) throws ApplicationException;

}
