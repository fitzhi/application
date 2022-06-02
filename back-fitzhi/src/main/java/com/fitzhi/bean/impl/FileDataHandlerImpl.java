package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_BRANCH_IS_MISSING_IN_PROJECT;
import static com.fitzhi.Error.CODE_CANNOT_DELETE_FILE;
import static com.fitzhi.Error.CODE_FILE_DOES_NOT_EXIST;
import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_BRANCH_IS_MISSING_IN_PROJECT;
import static com.fitzhi.Error.MESSAGE_CANNOT_DELETE_FILE;
import static com.fitzhi.Error.MESSAGE_FILE_DOES_NOT_EXIST;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Error.getStackTrace;
import static com.fitzhi.Global.INTERNAL_FILE_SEPARATORCHAR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.fitzhi.Global;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.data.internal.Author;
import com.fitzhi.data.internal.Constellation;
import com.fitzhi.data.internal.DetectedExperience;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectDetectedExperiences;
import com.fitzhi.data.internal.ProjectFloor;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * Implementation of DataSaver on the file system.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Profile("application")
@Slf4j
@Service
public class FileDataHandlerImpl implements DataHandler {

	/**
	 * Are we in shuffle-mode? In that scenario, the saving process will be
	 * unplugged.
	 */
	@Autowired
	ShuffleService shuffleService;

	/**
	 * Directory where data will be saved.
	 */
	@Value("${applicationOutDirectory}")
	private String saveDir;
	
	/**
	 * Logging constant
	 */
	private static final String SAVING_FILE_S = "Saving file %s";

	/**
	 * Logging constant
	 */
	private static final String LOADING_FILE_S = "Loading file %s";


	/**
	 * Directory where the GIT change records are saved.
	 */
	private static final String SAVED_CHANGES = "changes-data";

	/**
	 * Directory where the constellations files are saved.
	 */
	private static final String CONSTELLATIONS_LOCATION = "constellations-data";

	/**
	 * Directory where the different pathnames file are stored. 
	 */
	private static final String PATHNAMES = "pathnames-data";

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	private static final String SKILLS_FILENAME = "skills.json";
	private static final String PROJECTS_FILENAME = "projects.json";
	private static final String STAFF_FILENAME = "staff.json";

	/**
	 * This internal class is used to deserialize the class {@link ProjectDetectedExperiences
	 */
	@Data
	class ClazzDetectedExperiences {
		private int idExperienceDetectionTemplate;
		private int idProject;
		private Author author;
		private int count;
		private int idStaff;
	}

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	ProjectHandler staffHandler;

	private Path rootLocation;

	@PostConstruct
	private void init() {
		this.rootLocation = Paths.get(saveDir);
	}

	@Override
	public void saveProjects(Map<Integer, Project> projects) throws ApplicationException {

		/**
		 * We do not save the projects in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving %d projects into file %s.", projects.size(), PROJECTS_FILENAME));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.getId()).append(" ").append(project.getName()).append(", "));
			log.debug(sb.toString());
		}

		Path path = rootLocation.resolve(PROJECTS_FILENAME);

		try {
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				writer.write(gson.toJson(projects));
			}
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, PROJECTS_FILENAME), e);
		}

	}

	@Override
	public Map<Integer, Project> loadProjects() throws ApplicationException {

		Map<Integer, Project> projects = new HashMap<>();

		try (FileReader fr = new FileReader(rootLocation.resolve(PROJECTS_FILENAME).toFile())) {
			Type listProjectsType = new TypeToken<HashMap<Integer, Project>>() {
			}.getType();
			projects = gson.fromJson(fr, listProjectsType);
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, PROJECTS_FILENAME), e);
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading %d projects into file %s.", projects.size(), PROJECTS_FILENAME));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.getId()).append(" ").append(project.getName()).append(", "));
			log.debug(sb.toString());
		}

		return projects;
	}

	@Override
	public void saveStaff(Map<Integer, Staff> company) throws ApplicationException {

		/**
		 * We do not save the staff set in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving %d staff members into file %s.", company.size(), STAFF_FILENAME));
			final StringBuilder sb = new StringBuilder();
			company.values().stream().forEach(
					staff -> sb.append(staff.getIdStaff()).append(" ").append(staff.getLastName()).append(", "));
			log.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(STAFF_FILENAME).toFile())) {
			fw.write(gson.toJson(company));
		} catch (final Exception e) {
			if (log.isWarnEnabled()) {
				log.warn(getStackTrace(e));
			}
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, STAFF_FILENAME), e);
		}
	}

	@Override
	public Map<Integer, Staff> loadStaff() throws ApplicationException {

		Map<Integer, Staff> theStaff = new HashMap<>();

		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(rootLocation.resolve(STAFF_FILENAME).toFile()), StandardCharsets.UTF_8)) {
			Type listStaffType = new TypeToken<HashMap<Integer, Staff>>() {
			}.getType();
			theStaff = gson.fromJson(isr, listStaffType);
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR,
					MessageFormat.format(MESSAGE_IO_ERROR, rootLocation.resolve(STAFF_FILENAME).toFile().getAbsoluteFile()),
					e);
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading %d staff members from file %s.", theStaff.size(), STAFF_FILENAME));
			final StringBuilder sb = new StringBuilder();
			theStaff.values().stream().forEach(
					staff -> sb.append(staff.getIdStaff()).append(" ").append(staff.getLastName()).append(", "));
			log.debug(sb.toString());
		}
		return theStaff;
	}

	@Override
	public void saveSkills(Map<Integer, Skill> skills) throws ApplicationException {

		/**
		 * We do not save the skills in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving %d skills into file %s.", skills.size(), SKILLS_FILENAME));
			final StringBuilder sb = new StringBuilder();
			skills.values().stream()
					.forEach(skill -> sb.append(skill.getId()).append(" ").append(skill.getTitle()).append(", "));
			log.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(SKILLS_FILENAME).toFile())) {
			fw.write(gson.toJson(skills));
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, SKILLS_FILENAME), e);
		}
	}

	private void createIfNeededDirectory(String dir) throws ApplicationException {

		Path path = rootLocation.resolve(dir);
		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path);
			} catch (Exception e) {
				throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, SAVED_CHANGES), e);
			}
		}

	}

	/**
	 * Generate the changes.csv filename for loading and saving the
	 * {@link SourceControlChanges} container.
	 * 
	 * @param project the given project
	 * @return the filename to be used.
	 */
	private String generateChangesCsvFilename(Project project) {
		return SAVED_CHANGES + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-changes.csv";
	}

	/**
	 * Generate the project-layers.json filename for loading and saving the
	 * {@link ProjectLayer Project layers} list.
	 * 
	 * @param project the given project
	 * @return the filename to be used.
	 */
	private String generateProjectLayersJsonFilename(Project project) {
		return SAVED_CHANGES + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-project-layers.json";
	}

	/**
	 * Generate the project-detected-experiences.json filename for loading and saving the
	 * {@link DetectedExperience detected experiences} list.
	 * 
	 * @param project the given project
	 * @return the filename to be used.
	 */
	private String generateProjectDetectedExperiencesJsonFilename(Project project) {
		return SAVED_CHANGES + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-project-detected-experiences.json";
	}

	/**
	 * Generate the constellations-year-month.json filename for loading and saving the
	 * {@link Constellation constellatons} list.
	 * 
	 * @param month the year/month associated with the constellations
	 * @return the filename to be used.
	 */
	private String generateConstellationsJsonFilename(LocalDate month) {
		return CONSTELLATIONS_LOCATION + INTERNAL_FILE_SEPARATORCHAR + String.format("constellations-%d-%d.json", month.getYear(), month.getMonthValue());
	}

	@Override
	public void saveChanges(Project project, SourceControlChanges changes) throws ApplicationException {

		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(SAVED_CHANGES);

		final String filename = generateChangesCsvFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}
		try (Writer writer = new FileWriter(rootLocation.resolve(filename).toFile())) {

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
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}
	}

	@Override
	public SourceControlChanges loadChanges(Project project) throws ApplicationException {

		SourceControlChanges result = new SourceControlChanges();

		final String filename = generateChangesCsvFilename(project);

		File file = rootLocation.resolve(filename).toFile();
		if (log.isDebugEnabled()) {
			log.debug(String.format(LOADING_FILE_S, file.getAbsolutePath()));
		}

		if (!file.exists()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("But, the file %s does not exist", file.getAbsolutePath()));
			}
			throw new NotFoundException(CODE_FILE_DOES_NOT_EXIST, MessageFormat.format(MESSAGE_FILE_DOES_NOT_EXIST, file.getAbsolutePath()));
		}

		try (Reader filereader = new FileReader(file)) {

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
			}
		} catch (IOException ioe) {
			log.error(String.format("Internal error for project %s", project.getName()) , ioe);
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}

		return result;
	}

	@Override
	public void saveDetectedExperiences (Project project, ProjectDetectedExperiences experiences) throws ApplicationException {

		final String filename = generateProjectDetectedExperiencesJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(experiences.content()));
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
		
	}

	@Override
	public ProjectDetectedExperiences loadDetectedExperiences (Project project) throws ApplicationException {

		final String filename = generateProjectDetectedExperiencesJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(LOADING_FILE_S, rootLocation.resolve(filename)));
		}

		File file = rootLocation.resolve(filename).toFile();
		if (log.isDebugEnabled()) {
			log.debug(String.format(LOADING_FILE_S, file.getAbsolutePath()));
		}

		if (!file.exists()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("But, the file %s does not exist", file.getAbsolutePath()));
			}
			return null;
		}

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {

			ProjectDetectedExperiences result = new ProjectDetectedExperiences();
			Type typeListDetectedExperience = new TypeToken<List<ClazzDetectedExperiences>>() {
			}.getType();
			List<ClazzDetectedExperiences> list = gson.fromJson(fr, typeListDetectedExperience);
			list.stream().forEach(entry -> result.add(
				DetectedExperience.of(
					entry.idExperienceDetectionTemplate, 
					entry.idProject, 
					entry.author, 
					entry.count)
			));
			return result;
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
		
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
	 * Reset or create a new file
	 * </p>
	 * 
	 * @param filename the current filename
	 * @return a generateed filename to be used
	 * @throws ApplicationException thrown if any problem occurs, most probably an {@link IOException}
	 */
	private File createResetOrCreateFile(String filename) throws ApplicationException {
		Path path = rootLocation.resolve(filename);
		try {
			if (path.toFile().exists()) {
				Files.delete(path);
			}
			Path newPath = Files.createFile(path);
			if (log.isDebugEnabled()) {
				log.debug(String.format("new path %s is created", newPath.toAbsolutePath()));
			}
			return newPath.toFile();
		} catch (IOException ioe) {
			throw new ApplicationException(CODE_IO_ERROR,
					MessageFormat.format(MESSAGE_IO_ERROR, path.toFile().getAbsolutePath()), ioe);
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

	@Override
	public Map<Integer, Skill> loadSkills() throws ApplicationException {

		final String filename = SKILLS_FILENAME;

		Map<Integer, Skill> skills = new HashMap<>();

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {
			Type listSkillType = new TypeToken<HashMap<Integer, Skill>>() {
			}.getType();
			skills = gson.fromJson(fr, listSkillType);
			if (skills == null) {
				// If this.skills is still null, without IOException, it means that the file is
				// empty
				// The first launch of the application is a use case for scenario
				skills = new HashMap<>();
			}
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading %d skills from file %s.", skills.size(), filename));
			final StringBuilder sb = new StringBuilder();
			skills.values().stream()
					.forEach(skill -> sb.append(skill.getId()).append(" ").append(skill.getTitle()).append(", "));
			log.debug(sb.toString());
		}
		return skills;
	}

	@Override
	public void saveRepositoryDirectories(Project project, SourceControlChanges changes) throws ApplicationException {

		//
		// As the method-name explains, we create the directory which will hoist the file.
		//
		createIfNeededDirectory(PATHNAMES);

		List<String> directories = changes.keySet().stream()
				.map(this::extractDirectory)
				.distinct()
				.filter(path -> isDirectory(project, path))
				.sorted()
				.collect(Collectors.toList());

		savePaths(project, directories, PathsType.PATHS_ALL);
	}

	@Override
	public void savePaths(Project project, List<String> paths, PathsType pathsType) throws ApplicationException {

		String filename = this.generatePathnamesFile(project, pathsType);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving paths file %s", rootLocation.resolve(filename).toAbsolutePath()));
		}

		saveTxtFile(filename, paths);
	}

	@Override
	public List<String> loadPaths(Project project, PathsType pathsType) throws ApplicationException {

		String filename = this.generatePathnamesFile(project, pathsType);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving paths file %s", rootLocation.resolve(filename).toAbsolutePath()));
		}

		return loadTxtFile(filename);
	}


	/**
	 * Save a list of String into the given filename
	 * @param filename the filename to save the TXT file.
	 * @param lines the lines to be store on filesystem 
	 * @throws ApplicationException
	 */
	private void saveTxtFile(String filename, List<String> lines) throws ApplicationException {

		final File file = createResetOrCreateFile(filename);

		try (Writer writer = new FileWriter(file)) {
			for (String line : lines) {
				writer.write(line);
				writer.write(Global.LN);
			}
		} catch (IOException ioe) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}
	}

	/**
	 * Load the content of the given filename in a {@code List} of {@code String}.
	 * @param filename the filename which content has to be loaded.
	 * @return the content of the file in {@code String} format
	 * @throws ApplicationException if any problems occurs, most probably a {@link NotFoundException} or an {@link IOException}
	 */
	private List<String> loadTxtFile(String filename) throws ApplicationException {

		Path path = rootLocation.resolve(filename);
		if (!path.toFile().exists()) {
			throw new NotFoundException(CODE_FILE_DOES_NOT_EXIST, MessageFormat.format(MESSAGE_FILE_DOES_NOT_EXIST, path.toFile().getAbsolutePath()));
		}

		try (Reader reader = new FileReader(path.toFile())) {
			BufferedReader br = new BufferedReader(reader);
			return br.lines().collect(Collectors.toList());
		} catch (IOException ioe) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}

	}

	@Override
	public List<String> loadRepositoryDirectories(Project project) throws ApplicationException {

		String filename = this.generatePathnamesFile(project, PathsType.PATHS_ALL);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading the paths file %s", rootLocation.resolve(filename).toAbsolutePath()));
		}

		File file = rootLocation.resolve(filename).toFile();
		if (!file.exists()) {
			log.error(MessageFormat.format(MESSAGE_FILE_DOES_NOT_EXIST, filename));
			throw new ApplicationException(CODE_FILE_DOES_NOT_EXIST, MessageFormat.format(MESSAGE_FILE_DOES_NOT_EXIST, filename));
		}
		try (Reader reader = new FileReader(file)) {
			BufferedReader br = new BufferedReader(reader);
			return br.lines().collect(Collectors.toList());
		} catch (IOException ioe) {
			log.error(getStackTrace(ioe));
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}
	}

	@Override
	public String generatePathnamesFile(Project project, PathsType pathsType) throws ApplicationException {

		// To prevent a NullException in generatePathnamesFile
		if (project.getBranch() == null) {
			throw new ApplicationException(CODE_BRANCH_IS_MISSING_IN_PROJECT, MessageFormat.format(MESSAGE_BRANCH_IS_MISSING_IN_PROJECT, project.getId(), project.getName()));
		}

		return String.format(
				"%s/%d-%s-%s.txt", PATHNAMES, 
				project.getId(), 
				project.getBranch().replace(" ", "_").replace("/", "_"),
				pathsType.getTypeOfPath());
	}

	@Override
	public ProjectLayers loadSkylineLayers(Project project) throws ApplicationException {

		final ProjectLayers containerLayers = new ProjectLayers(project);

		final String filename = generateProjectLayersJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(LOADING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {

			Type typeListProjectLayer = new TypeToken<List<ProjectLayer>>() {
			}.getType();
			containerLayers.setLayers(gson.fromJson(fr, typeListProjectLayer));
			if (containerLayers.getLayers() == null) {
				// If this layers list is still null, without IOException, it means that the
				// file empty
				containerLayers.setLayers(new ArrayList<>());
			}
			return containerLayers;
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public boolean hasAlreadySavedSkillsConstellations(LocalDate month) throws ApplicationException {
		final String filename = generateConstellationsJsonFilename(month);
		if (log.isDebugEnabled()) {
			log.debug(String.format("hasAlreadySavedSkillsConstellations(%d, %d)", month.getYear(), month.getMonthValue()));
			log.debug(String.format("filename %s", filename));
		}
		Path path = rootLocation.resolve(filename);
		return Files.exists(path);
	}

	@Override
	public List<Constellation> loadSkillsConstellations(LocalDate month) throws ApplicationException{
		final String filename = generateConstellationsJsonFilename(month);
		if (log.isDebugEnabled()) {
			log.debug(String.format(LOADING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {
			Type typeListConstellations = new TypeToken<List<Constellation>>() {}.getType();
			return gson.fromJson(fr, typeListConstellations);
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public void saveSkillsConstellations(LocalDate month, List<Constellation> constellations) throws ApplicationException {
		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(CONSTELLATIONS_LOCATION);

		final String filename = generateConstellationsJsonFilename(month);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(constellations));
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
		
	}

	
	@Override
	public void saveSkylineLayers(Project project, ProjectLayers layers) throws ApplicationException {
		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(SAVED_CHANGES);

		final String filename = generateProjectLayersJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(layers.getLayers()));
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	/**
	 * Generate the project-building.json filename for loading and saving the
	 * {@link ProjectBuilding Project building} container.
	 * 
	 * @param project the given project
	 * @return the filename to be used.
	 */
	private String generateProjectBuildingJsonFilename(Project project) {
		return SAVED_CHANGES + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-project-building.json";
	}

	@Override
	public ProjectBuilding loadProjectBuilding(Project project) throws ApplicationException {

		final ProjectBuilding building = new ProjectBuilding();

		final String filename = generateProjectBuildingJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(LOADING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {

			Type typeListProjectFloor = new TypeToken<List<ProjectFloor>>() {
			}.getType();
			List<ProjectFloor> floors = gson.fromJson(fr, typeListProjectFloor);
			if (floors == null) {
				// If this building list is still null, without IOException, it means that the
				// file is empty.
				building.setBuilding(new HashMap<>());
			} else {
				floors.stream().forEach(floor -> building.initWeek(floor.getIdProject(), floor.getYear(),
						floor.getWeek(), floor.getLinesActiveDevelopers(), floor.getLinesInactiveDevelopers()));
			}
			return building;
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public void saveProjectBuilding(Project project, ProjectBuilding building) throws ApplicationException {
		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(SAVED_CHANGES);

		final String filename = generateProjectBuildingJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(building.getBuilding().values()));
		} catch (final Exception e) {
			throw new ApplicationException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public boolean hasSavedSkylineLayers(Project project) {
		final String filename = generateProjectLayersJsonFilename(project);
		Path path = rootLocation.resolve(filename);
		return Files.exists(path);
	}

	@Override
	public void saveRepositoryAnalysis(Project project, RepositoryAnalysis analysis) throws ApplicationException {
		
		//
        // We save the directories of the repository. 
        // These directories will be used to help the end-user when editing the directories to be excluded from the analysis
        //
		this.saveChanges(project, analysis.getChanges());

        //
        // We save the directories list extracted from the changes.
        //
		this.saveRepositoryDirectories(project, analysis.getChanges());

		//
		// Saving the set attached to the ADDED paths in the analysis
		//
		this.savePaths(project, new ArrayList<>(analysis.getPathsAdded()), PathsType.PATHS_ADDED);

		//
		// Saving the set attached to the MODIFIED paths in the analysis
		//
		this.savePaths(project, new ArrayList<>(analysis.getPathsModified()), PathsType.PATHS_MODIFIED);

		//
		// Saving the set attached to the MODIFIED CANDIDATE in the analysis
		//
		this.savePaths(project, new ArrayList<>(analysis.getPathsCandidate()), PathsType.PATHS_CANDIDATE);
	}

	@Override
	public RepositoryAnalysis loadRepositoryAnalysis(Project project) throws ApplicationException {

		SourceControlChanges changes = loadChanges(project);

		List<String> pathsAdded = loadPaths(project, PathsType.PATHS_ADDED);

		List<String> pathsModified = loadPaths(project, PathsType.PATHS_MODIFIED);

		List<String> pathsCandidate = loadPaths(project, PathsType.PATHS_CANDIDATE);

		RepositoryAnalysis analysis = new RepositoryAnalysis(project);
		analysis.setChanges(changes);
		analysis.setPathsAdded(new HashSet<>(pathsAdded));
		analysis.setPathsModified(new HashSet<>(pathsModified));
		analysis.setPathsCandidate(new HashSet<>(pathsCandidate));
		return analysis;
	}

	@Override
	public void removeCrawlerFiles(Project project) throws ApplicationException {

		String filename = generateChangesCsvFilename(project);
		File file = rootLocation.resolve(filename).toFile();
		removeFile(file);

		filename = generateProjectLayersJsonFilename(project);
		file = rootLocation.resolve(filename).toFile();
		removeFile(file);

		removePathnamesFile(project, PathsType.PATHS_ALL);
		removePathnamesFile(project, PathsType.PATHS_MODIFIED);
		removePathnamesFile(project, PathsType.PATHS_CANDIDATE);
		removePathnamesFile(project, PathsType.PATHS_ADDED);

	}	

	@Override
	public boolean isLocal() {
		return true;
	}
	
	void removePathnamesFile (Project project, PathsType pathsType ) throws ApplicationException {
		String filename = this.generatePathnamesFile(project, pathsType);
		File f = rootLocation.resolve(filename).toFile();
		removeFile(f);
	}

	public static void removeFile (File f) throws ApplicationException {
		if (f.exists()) {
			if (log.isDebugEnabled()) {
				log.debug (String.format("Removing file %s", f.getAbsolutePath()));
			}
			try {
				Files.delete(f.toPath());
			} catch (IOException ioe) {
				throw new ApplicationException(CODE_CANNOT_DELETE_FILE, MessageFormat.format(MESSAGE_CANNOT_DELETE_FILE, f.getAbsolutePath()), ioe);
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug (String.format("File %s is skipped. It does not exist.", f.getAbsolutePath()));
			}
		}
	}

}
