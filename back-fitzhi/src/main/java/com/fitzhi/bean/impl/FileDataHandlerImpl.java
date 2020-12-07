package com.fitzhi.bean.impl;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;
import static com.fitzhi.Global.INTERNAL_FILE_SEPARATORCHAR;
import static com.fitzhi.Error.CODE_FILE_DOES_NOT_EXIST;
import static com.fitzhi.Error.MESSAGE_FILE_DOES_NOT_EXIST;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.fitzhi.Global;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.ShuffleService;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.ProjectBuilding;
import com.fitzhi.data.internal.ProjectBuilding.YearWeek;
import com.fitzhi.data.internal.ProjectFloor;
import com.fitzhi.data.internal.ProjectLayer;
import com.fitzhi.data.internal.ProjectLayers;
import com.fitzhi.data.internal.RepositoryAnalysis;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SourceCodeDiffChange;
import com.fitzhi.data.internal.SourceControlChanges;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.SkillerException;
import com.fitzhi.source.crawler.git.SourceChange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import static com.fitzhi.Error.getStackTrace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * Implementation of DataSaver on the file system.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Slf4j
@Service
public class FileDataHandlerImpl implements DataHandler {

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
	 * Logging constant
	 */
	private static final String SAVING_FILE_S = "Saving file %s";

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
	 * Directory where the GIT change records are saved.
	 */
	private final String savedChanges = "changes-data";

	/**
	 * Directory where the different pathnames file are stored. 
	 */
	private final String pathNames = "pathnames-data";

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

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
	public void saveProjects(Map<Integer, Project> projects) throws SkillerException {

		/**
		 * We do not save the projects in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		final String filename = "projects.json";

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving %d projects into file %s.", projects.size(), filename));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.getId()).append(" ").append(project.getName()).append(", "));
			log.debug(sb.toString());
		}

		Path path = rootLocation.resolve(filename);

		try {
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				writer.write(gson.toJson(projects));
			}
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}

	}

	@Override
	public Map<Integer, Project> loadProjects() throws SkillerException {

		final String filename = "projects.json";

		Map<Integer, Project> projects = new HashMap<>();

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {
			Type listProjectsType = new TypeToken<HashMap<Integer, Project>>() {
			}.getType();
			projects = gson.fromJson(fr, listProjectsType);
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading %d projects into file %s.", projects.size(), filename));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.getId()).append(" ").append(project.getName()).append(", "));
			log.debug(sb.toString());
		}

		return projects;
	}

	@Override
	public void saveStaff(Map<Integer, Staff> company) throws SkillerException {

		/**
		 * We do not save the staff set in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		final String filename = "staff.json";

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving %d staff members into file %s.", company.size(), filename));
			final StringBuilder sb = new StringBuilder();
			company.values().stream().forEach(
					staff -> sb.append(staff.getIdStaff()).append(" ").append(staff.getLastName()).append(", "));
			log.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(company));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public Map<Integer, Staff> loadStaff() throws SkillerException {

		final String filename = "staff.json";

		Map<Integer, Staff> theStaff = new HashMap<>();

		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(rootLocation.resolve(filename).toFile()),
				"UTF-8")) {
			Type listStaffType = new TypeToken<HashMap<Integer, Staff>>() {
			}.getType();
			theStaff = gson.fromJson(isr, listStaffType);
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR,
					MessageFormat.format(MESSAGE_IO_ERROR, rootLocation.resolve(filename).toFile().getAbsoluteFile()),
					e);
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading %d staff members from file %s.", theStaff.size(), filename));
			final StringBuilder sb = new StringBuilder();
			theStaff.values().stream().forEach(
					staff -> sb.append(staff.getIdStaff()).append(" ").append(staff.getLastName()).append(", "));
			log.debug(sb.toString());
		}
		return theStaff;
	}

	@Override
	public void saveSkills(Map<Integer, Skill> skills) throws SkillerException {

		/**
		 * We do not save the skills in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		final String filename = "skills.json";

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving %d skills into file %s.", skills.size(), filename));
			final StringBuilder sb = new StringBuilder();
			skills.values().stream()
					.forEach(skill -> sb.append(skill.getId()).append(" ").append(skill.getTitle()).append(", "));
			log.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(skills));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	private void createIfNeededDirectory(String dir) throws SkillerException {

		Path path = rootLocation.resolve(dir);
		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path);
			} catch (Exception e) {
				throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, savedChanges), e);
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
		return savedChanges + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-changes.csv";
	}

	/**
	 * Generate the prohect-layers.json filename for loading and saving the
	 * {@link ProjectLayer Project layers} list.
	 * 
	 * @param project the given project
	 * @return the filename to be used.
	 */
	private String generateProjectLayersJsonFilename(Project project) {
		return savedChanges + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-project-layers.json";
	}

	@Override
	public void saveChanges(Project project, SourceControlChanges changes) throws SkillerException {

		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(savedChanges);

		final String filename = generateChangesCsvFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}
		try (Writer writer = new FileWriter(rootLocation.resolve(filename).toFile())) {

			try (CSVWriter csvWriter = new CSVWriter(writer, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
				csvWriter.writeNext(new String[] { "Commit", "Path", "Date", "Author", "Email", "diff" });

				for (String path : changes.keySet()) {
					changes.getSourceFileHistory(path).getChanges().forEach(change -> csvWriter.writeNext(new String[] {
							change.getCommitId(), path, change.getDateCommit().toString(), change.getAuthorName(),
							change.getAuthorEmail(),
							String.valueOf(change.getDiff().getLinesAdded() - change.getDiff().getLinesDeleted()) }));
				}
			}
		} catch (IOException ioe) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}
	}

	@Override
	public SourceControlChanges loadChanges(Project project) throws SkillerException {

		SourceControlChanges result = new SourceControlChanges();

		final String filename = generateChangesCsvFilename(project);

		File file = rootLocation.resolve(filename).toFile();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading file %s", file.getAbsolutePath()));
		}

		if (!file.exists()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("But, the file %s does not exist", file.getAbsolutePath()));
			}
			return null;
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
			log.error("Internal error", ioe);
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}

		return result;
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
	 * @throws SkillerException thrown if any problem occurs, most probably an {@link IOException}
	 */
	private File createResetOrCreateFile(String filename) throws SkillerException {
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
			throw new SkillerException(CODE_IO_ERROR,
					MessageFormat.format(MESSAGE_IO_ERROR, path.toFile().getAbsolutePath()), ioe);
		}
	}

	/**
	 * <p>
	 * Test <b>on the file system</b> if the given pathname is a directory in the
	 * repository.
	 * </p>
	 * <p>
	 * <font color="chocolate">We use java IO API to validate that the given path is
	 * effectively a directory.</font>
	 * </p>
	 * 
	 * @param project  the current project whose repository is analyzed. <i>We use
	 *                 this parameter to retrieve the location of the GIT local
	 *                 repository.</i>
	 * @param pathname the given pathname
	 * @return {@code true} if the pathname is a directory.
	 */
	private boolean isDirectory(final Project project, final String pathname) {

		if (pathname.indexOf(INTERNAL_FILE_SEPARATORCHAR) != -1) {
			return true;
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("Examining if %s is a directory",
					project.getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname));
		}
		return Paths.get(project.getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname).toFile()
				.isDirectory();
	}

	@Override
	public Map<Integer, Skill> loadSkills() throws SkillerException {

		final String filename = "skills.json";

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
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
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
	public void saveRepositoryDirectories(Project project, SourceControlChanges changes) throws SkillerException {

		//
		// As the method-name explains, we create the directory which will hoist the file.
		//
		createIfNeededDirectory(pathNames);

		List<String> directories = changes.keySet().stream().map(this::extractDirectory).distinct()
				.filter(path -> isDirectory(project, path)).sorted().collect(Collectors.toList());

		savePaths(project, directories, PathsType.PATHS_ALL);
	}

	@Override
	public void savePaths(Project project, List<String> paths, PathsType pathsType) throws SkillerException {

		String filename = this.generatePathnamesFile(project, pathsType);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Saving paths file %s", rootLocation.resolve(filename).toAbsolutePath()));
		}

		saveTxtFile(filename, paths);
	}

	/**
	 * Save a list of String into the given filename
	 * @param filename the filename to save the TXT file.
	 * @param lines the lines to be store on filesystem 
	 * @throws SkillerException
	 */
	private void saveTxtFile(String filename, List<String> lines) throws SkillerException {

		final File file = createResetOrCreateFile(filename);

		try (Writer writer = new FileWriter(file)) {
			for (String line : lines) {
				writer.write(line);
				writer.write(Global.LN);
			}
		} catch (IOException ioe) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}

	}

	@Override
	public List<String> loadRepositoryDirectories(Project project) throws SkillerException {

		String filename = this.generatePathnamesFile(project, PathsType.PATHS_ALL);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading the paths file %s", rootLocation.resolve(filename).toAbsolutePath()));
		}

		File file = rootLocation.resolve(filename).toFile();
		if (!file.exists()) {
			log.error(MessageFormat.format(MESSAGE_FILE_DOES_NOT_EXIST, filename));
			throw new SkillerException(CODE_FILE_DOES_NOT_EXIST, MessageFormat.format(MESSAGE_FILE_DOES_NOT_EXIST, filename));
		}
		try (Reader reader = new FileReader(file)) {
			BufferedReader br = new BufferedReader(reader);
			return br.lines().collect(Collectors.toList());
		} catch (IOException ioe) {
			log.error(getStackTrace(ioe));
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
		}
	}

	@Override
	public String generatePathnamesFile(Project project, PathsType pathsType) {
		return String.format(
				"%s/%d-%s-%s.txt", pathNames, 
				project.getId(), 
				project.getBranch().replace(" ", "_"),
				pathsType.getTypeOfPath());
	}

	@Override
	public ProjectLayers loadSkylineLayers(Project project) throws SkillerException {

		final ProjectLayers containerLayers = new ProjectLayers(project);

		final String filename = generateProjectLayersJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading file %s", rootLocation.resolve(filename)));
		}

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {

			Type typeListProjectLayer = new TypeToken<List<ProjectLayer>>() {
			}.getType();
			containerLayers.setLayers(gson.fromJson(fr, typeListProjectLayer));
			if (containerLayers.getLayers() == null) {
				// If this layers list is still null, without IOException, it means that the
				// file empty
				containerLayers.setLayers(new ArrayList<ProjectLayer>());
			}
			return containerLayers;
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public void saveSkylineLayers(Project project, ProjectLayers layers) throws SkillerException {
		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(savedChanges);

		final String filename = generateProjectLayersJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(layers.getLayers()));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
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
		return savedChanges + INTERNAL_FILE_SEPARATORCHAR + project.getId() + "-project-building.json";
	}

	@Override
	public ProjectBuilding loadProjectBuilding(Project project) throws SkillerException {

		final ProjectBuilding building = new ProjectBuilding();

		final String filename = generateProjectBuildingJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format("Loading file %s", rootLocation.resolve(filename)));
		}

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {

			Type typeListProjectFloor = new TypeToken<List<ProjectFloor>>() {
			}.getType();
			List<ProjectFloor> floors = gson.fromJson(fr, typeListProjectFloor);
			if (floors == null) {
				// If this building list is still null, without IOException, it means that the
				// file is empty.
				building.setBuilding(new HashMap<YearWeek, ProjectFloor>());
			} else {
				floors.stream().forEach(floor -> building.initWeek(floor.getIdProject(), floor.getYear(),
						floor.getWeek(), floor.getLinesActiveDevelopers(), floor.getLinesInactiveDevelopers()));
			}
			return building;
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public void saveProjectBuilding(Project project, ProjectBuilding building) throws SkillerException {
		//
		// As the method-name explains, we create the directory.
		//
		createIfNeededDirectory(savedChanges);

		final String filename = generateProjectBuildingJsonFilename(project);

		if (log.isDebugEnabled()) {
			log.debug(String.format(SAVING_FILE_S, rootLocation.resolve(filename)));
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(building.getBuilding().values()));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public boolean hasSavedSkylineLayers(Project project) {
		final String filename = generateProjectLayersJsonFilename(project);
		Path path = rootLocation.resolve(filename);
		return Files.exists(path);
	}

	@Override
	public void saveRepositoryAnalysis(Project project, RepositoryAnalysis analysis) throws SkillerException {
		
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
		this.savePaths(project, new ArrayList<String>(analysis.getPathsAdded()), PathsType.PATHS_ADDED);

		//
		// Saving the set attached to the MODIFIED paths in the analysis
		//
		this.savePaths(project, new ArrayList<String>(analysis.getPathsModified()), PathsType.PATHS_MODIFIED);

		//
		// Saving the set attached to the MODIFIED CANDIDATE in the analysis
		//
		this.savePaths(project, new ArrayList<String>(analysis.getPathsCandidate()), PathsType.PATHS_CANDIDATE);
	}

	@Override
	public RepositoryAnalysis loadRepositoryAnalysis(Project project) throws SkillerException {

		SourceControlChanges changes = this.loadChanges(project);

		return null;
	}
}
