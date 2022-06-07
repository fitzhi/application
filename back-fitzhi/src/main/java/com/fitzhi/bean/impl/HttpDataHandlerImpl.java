package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.CODE_METHOD_NOT_FOUND_EXCEPTION;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_METHOD_NOT_FOUND_EXCEPTION;
import static com.fitzhi.Error.getStackTrace;
import static com.fitzhi.Global.INTERNAL_FILE_SEPARATORCHAR;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.Global;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.HttpAccessHandler;
import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectAnalysis;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceCodeDiffChange;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;
import com.fitzhi.source.crawler.git.SourceChange;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * Implementation of DataHandler based on HTTP interaction. 
 * This implementation is planned to be used bythe slave mode.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Slf4j
@Profile("slave")
public class HttpDataHandlerImpl<T> implements DataHandler {

	@Autowired
	HttpConnectionHandler httpConnectionHandler;

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

	/**
	 * Login used by the slave for the connection to the main Fitzhi applciation.
	 */
	@Value("${login}")
	private String login;

	/**
	 * Password used by the slave for the connection to the main Fitzhi application.
	 */
	@Value("${pass}")
	private String pass;

	@Autowired
	HttpAccessHandler<Staff> httpAccessStaff; 

	@Autowired
	HttpAccessHandler<Project> httpAccessProject; 

	@Autowired
	HttpAccessHandler<Skill> httpAccessSkill; 

	@Autowired
	HttpAccessHandler<String> httpAccess; 

	@Autowired
	HttpAccessHandler<ProjectLayer> httpAccessProjectLayer;

	private static String NOT_IMPLEMENTED_YET = "Not implemented yet";

	@Override
	public void saveProjects(Map<Integer, Project> projects) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public Map<Integer, Project> loadProjects() throws ApplicationException {
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/project";
		List<Project> projects = httpAccessProject.loadList(url, new TypeReference<List<Project>>(){});
		Map<Integer, Project> map = new HashMap<>();
		projects.forEach(p -> map.put(p.getId(), p));
		return map;
	}
	@Override
	public void saveStaff(Map<Integer, Staff> staff) throws ApplicationException {
		throw new ApplicationRuntimeException("SHOULD NOT PASS HERE : Saving the staff collection from the slave requires a PROJECT.");
	}

	@Override
	public void saveStaff(Project project, Map<Integer, Staff> staff) throws ApplicationException {
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		List<Staff> list = new ArrayList<>(staff.values());
		if (log.isInfoEnabled()) {
			log.info(String.format("Send %d eligibile staff members top the main application", list.size()));
		}
		String url = applicationUrl + "/api/project/" + project.getId() + "/staff";
		httpAccessStaff.putList(url, list);
	}

	@Override
	public Map<Integer, Staff> loadStaff() throws ApplicationException {
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/staff";
		List<Staff> staff = httpAccessStaff.loadList(url, new TypeReference<List<Staff>>(){});
		Map<Integer, Staff> map = new HashMap<>();
		staff.forEach(s -> map.put(s.getIdStaff(), s));
		if (log.isInfoEnabled()) {
			log.info (String.format("%d staff have been loaded from the main application", map.size()));
		}
		return map;
	}

	@Override
	public void saveSkills(Map<Integer, Skill> staff) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public void saveRepositoryAnalysis(Project project, RepositoryAnalysis analysis) throws ApplicationException {
		saveChanges(project, analysis.getChanges());
		saveRepositoryDirectories(project, analysis.getChanges());
		savePaths(project, new ArrayList<>(analysis.getPathsAdded()), PathsType.PATHS_ADDED);
		savePaths(project, new ArrayList<>(analysis.getPathsModified()), PathsType.PATHS_MODIFIED);
		savePaths(project, new ArrayList<>(analysis.getPathsCandidate()), PathsType.PATHS_CANDIDATE);
	}

	@Override
	public RepositoryAnalysis loadRepositoryAnalysis(Project project) throws ApplicationException {
		throw new NotFoundException(CODE_METHOD_NOT_FOUND_EXCEPTION, MESSAGE_METHOD_NOT_FOUND_EXCEPTION);
	}

	@Override
	public void saveChanges(Project project, SourceControlChanges changes) throws ApplicationException {
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/project/" + project.getId() + "/changes";
		String body = serializeChanges(changes);
		if (log.isDebugEnabled()) {
			log.debug ("sending to url");
			log.debug (body);
		}
		httpAccess.put(url, body, new TypeReference<String>(){});
	}

	/**
	 * Serialize into a CSV String the collection of changes extracted from the GIT repository.
	 * @param changes the commit-changes from the repository
	 * @return the resulting String
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 */
	public static String serializeChanges(SourceControlChanges changes) throws ApplicationException {

		try (Writer writer = new StringWriter()) {
			try (CSVWriter csvWriter = new CSVWriter(writer, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER, Global.LN)) {
				csvWriter.writeNext(new String[] { "Commit", "Path", "Date", "Author", "Email", "diff" });
	
				for (String path : changes.keySet()) {
					changes.getSourceFileHistory(path).getChanges().forEach(change -> csvWriter.writeNext(new String[] {
							change.getCommitId(), 
							path, 
							change.getDateCommit().toString(), 
							change.getAuthor().getName(),
							change.getAuthor().getEmail(),
							String.valueOf(change.getDiff().getLinesAdded() - change.getDiff().getLinesDeleted()) }));
				}
			}

			writer.flush();
			return ((StringWriter) writer).getBuffer().toString();

		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, "changes.csv"), ioe);
		}
	}

	/**
	 * Deserialize from a CSV String to a collection of changes.
	 * @param changes the changes from the repository
	 * @return the resulting {@link SourceControlChanges changes}
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 */
	public static SourceControlChanges deserializeChanges(String changes) throws ApplicationException {

		try (Reader filereader = new StringReader(changes)) {

			SourceControlChanges result = new SourceControlChanges();

			CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

			try (CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).withSkipLines(1)
					.build()) {

				String[] nextRecord;

				while ((nextRecord = csvReader.readNext()) != null) {
					String commitId = nextRecord[0];
					String sourceFilename = nextRecord[1];
					String date = nextRecord[2];
					String authorName = nextRecord[3];
					String authorEmail = nextRecord[4];
					String lines = nextRecord[5];

					result.addChange(sourceFilename, new SourceChange(commitId, LocalDate.parse(date), authorName,
							authorEmail, -1, new SourceCodeDiffChange(sourceFilename, 0, Integer.valueOf(lines))));
				}

				return result;
			}
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, "changes.csv"), ioe);
		}
	}

	@Override
	public SourceControlChanges loadChanges(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public void saveDetectedExperiences(Project project, ProjectDetectedExperiences experiences)
			throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public ProjectDetectedExperiences loadDetectedExperiences(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public void savePaths(Project project, List<String> paths, PathsType pathsType) throws ApplicationException {
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/project/" + project.getId() + "/" + pathsType.getTypeOfPath();

		httpAccess.putList(url, paths);
	}

	@Override
	public List<String> loadPaths(Project project, PathsType pathsType) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public void saveSkylineLayers(Project project, ProjectLayers layers) throws ApplicationException {

		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/project/" + project.getId() + "/projectLayers";

		httpAccessProjectLayer.putList(url, layers.getLayers());

	}

	@Override
	public ProjectLayers loadSkylineLayers(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public boolean hasSavedSkylineLayers(Project project) {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public void saveProjectBuilding(Project project, ProjectBuilding building) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public ProjectBuilding loadProjectBuilding(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public Map<Integer, Skill> loadSkills() throws ApplicationException {
		if (!httpConnectionHandler.isConnected()) {
			httpConnectionHandler.connect(login, pass);
		}
		String url = applicationUrl + "/api/skill";
		List<Skill> skills = httpAccessSkill.loadList(url, new TypeReference<List<Skill>>(){});
		Map<Integer, Skill> map = new HashMap<>();
		skills.forEach(s -> map.put(s.getId(), s));
		return map;
	}

	@Override
	public void saveRepositoryDirectories(Project project, SourceControlChanges changes) throws ApplicationException {
		List<String> directories = changes.keySet().stream()
				.map(this::extractDirectory)
				.distinct()
				.filter(path -> isDirectory(project, path))
				.sorted()
				.collect(Collectors.toList());

		savePaths(project, directories, PathsType.PATHS_ALL);
	}

	@Override
	public List<String> loadRepositoryDirectories(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public String generatePathnamesFile(Project project, com.fitzhi.bean.impl.FileDataHandlerImpl.PathsType pathsType)
			throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}
		
	@Override
	public void removeCrawlerFiles(Project project) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public boolean hasAlreadySavedSkillsConstellations(LocalDate month) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public void saveSkillsConstellations(LocalDate month, List<Constellation> constellations)
			throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}
		
	@Override
	public List<Constellation> loadSkillsConstellations(LocalDate month) throws ApplicationException {
		throw new ApplicationRuntimeException (NOT_IMPLEMENTED_YET);
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public void saveProjectAnalysis(ProjectAnalysis projectAnalysis) throws ApplicationException {
	}

	/**
	 * Extract the directory path from the file path.
	 * 
	 * @param pathFilename path filename
	 * @return the path of the directory
	 */
	private String extractDirectory(String pathFilename) {
		int lastIndexOf = pathFilename.lastIndexOf('/');
		if (lastIndexOf == -1) {
			return pathFilename;
		} else {
			return pathFilename.substring(0, lastIndexOf);
		}
	}

	/**
	 * <p>
	 * Test <b>on the file system</b> if the given pathname is a directory in the
	 * repository.
	 * </p>
	 * <p>
	 * We use java IO API to validate that the given path is
	 * effectively a directory.
	 * </p>
	 * 
	 * @param project  the current project whose repository is analyzed.
	 * <em>We use this parameter to retrieve the location of the GIT local repository.</em>
	 * @param pathname the given pathname
	 * @return {@code true} if the pathname is a directory, {@code false} otherwise 
	 */
	private boolean isDirectory(final Project project, final String pathname)  {

		if (pathname.indexOf(INTERNAL_FILE_SEPARATORCHAR) != -1) {
			return true;
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("Examining if %s is a directory",
					project.getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname));
		}
		try {
			return Paths.get(project.getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname)
					.toFile()
					.isDirectory();
		} catch (final Exception e) {
			log.error(getStackTrace(e));
			return false;
		}
	}

}
