/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_IO_ERROR;
import static fr.skiller.Error.MESSAGE_IO_ERROR;
import static fr.skiller.Global.INTERNAL_FILE_SEPARATORCHAR;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;

import fr.skiller.Global;
import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.ShuffleService;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.git.SCMChange;
import static fr.skiller.Global.INTERNAL_FILE_SEPARATORCHAR;

/**
 * <p>Implementation of DataSaver on the file system.</p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
public class FileDataSaverImpl implements DataSaver {

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
	 * The logger for the GitScanner.
	 */
	private Logger logger = LoggerFactory.getLogger(FileDataSaverImpl.class.getCanonicalName());

	/**
	 * Directory to save the changes file.s
	 */
	private final String SAVED_CHANGES = "saved-changes";

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

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving %d projects into file %s.", projects.size(), filename));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.getId()).append(" ").append(project.getName()).append(", "));
			logger.debug(sb.toString());
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

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Loading %d projects into file %s.", projects.size(), filename));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.getId()).append(" ").append(project.getName()).append(", "));
			logger.debug(sb.toString());
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

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving %d staff members into file %s.", company.size(), filename));
			final StringBuilder sb = new StringBuilder();
			company.values().stream()
					.forEach(staff -> sb.append(staff.getIdStaff()).append(" ").append(staff.getLastName()).append(", "));
			logger.debug(sb.toString());
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

		try (FileReader fr = new FileReader(rootLocation.resolve(filename).toFile())) {
			Type listStaffType = new TypeToken<HashMap<Integer, Staff>>() {
			}.getType();
			theStaff = gson.fromJson(fr, listStaffType);
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, rootLocation.resolve(filename).toFile().getAbsoluteFile()), e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Loading %d staff members from file %s.", theStaff.size(), filename));
			final StringBuilder sb = new StringBuilder();
			theStaff.values().stream()
					.forEach(staff -> sb.append(staff.getIdStaff()).append(" ").append(staff.getLastName()).append(", "));
			logger.debug(sb.toString());
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

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving %d skills into file %s.", skills.size(), filename));
			final StringBuilder sb = new StringBuilder();
			skills.values().stream().forEach(skill -> sb.append(skill.getId()).append(" ").append(skill.getTitle()).append(", "));
			logger.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(rootLocation.resolve(filename).toFile())) {
			fw.write(gson.toJson(skills));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	private void createIfNeededDirectorySavedChanges() throws SkillerException { 

		Path dir = rootLocation.resolve(SAVED_CHANGES);
		if (Files.notExists(dir)) {
			try {
				Files.createDirectories(dir);
			} catch (Exception e) {
				throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, SAVED_CHANGES), e);
			}
		}

	}

	@Override
	public void saveChanges(Project project, List<SCMChange> changes) throws SkillerException {
	
		createIfNeededDirectorySavedChanges();

		final String filename = SAVED_CHANGES + INTERNAL_FILE_SEPARATORCHAR + project.getName() + "-changes.csv";
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving file %s", rootLocation.resolve(filename)));
		}
		try (Writer writer = new FileWriter(rootLocation.resolve(filename).toFile())) {

	        try (CSVWriter csvWriter = new CSVWriter(writer,
	                ';',
	                CSVWriter.DEFAULT_QUOTE_CHARACTER,
	                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                CSVWriter.DEFAULT_LINE_END)) {
		        csvWriter.writeNext(new String[] {"Commit", "Path", "Date", "Author", "Email"});
		        changes.stream().forEach(change -> csvWriter.writeNext(new String[]{
		        			change.getCommitId(),
		        			change.getPath(),
		        			change.getDateCommit().toString(),
		        			change.getAuthorName(),
		        			change.getAuthorEmail()}));
		    }
        } catch (IOException ioe) {
        	throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
        }
	}
	
	/**
	 * Extract the directory path from the file path. 
	 * @param pathFilename path filename
	 * @return the path of the directory
	 */
	private String extractDirectory (String pathFilename) {
		int lastIndexOf = pathFilename.lastIndexOf('/');
		if (lastIndexOf == -1) {
			return pathFilename;
		} else {
			return pathFilename.substring(0, lastIndexOf);
		}
	}

	/**
	 * Reset or create a new file
	 * @param filename the current filename
	 * @return a new file
	 * @throws SkillerException
	 */
	private File createResetOrCreateFile (String filename) throws SkillerException {
		Path path = rootLocation.resolve(filename);
		try {
			if (path.toFile().exists()) {
				Files.delete(path);
			}
			Path newPath = Files.createFile(path);
			return newPath.toFile();
	    } catch (IOException ioe) {
	    	throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, path.toFile().getAbsolutePath()), ioe);
	    }
	}
	
	/**
	 * <p>Test if the pathname is a directory in the repository.</p>
	 * @param project the current project whose repository is crawled.
	 * @param pathname the given pathname
	 * @return {@code true} if the pathname is a directory.
	 */
	private boolean isDirectory (final Project project, final String pathname) {
		
		if (pathname.indexOf(INTERNAL_FILE_SEPARATORCHAR) != -1) {
				return true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Examining if %s is a directory", project.getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname));
		}
		return Paths.get(project.getLocationRepository() + INTERNAL_FILE_SEPARATORCHAR + pathname).toFile().isDirectory();
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

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Loading %d skills from file %s.", skills.size(), filename));
			final StringBuilder sb = new StringBuilder();
			skills.values().stream().forEach(skill -> sb.append(skill.getId()).append(" ").append(skill.getTitle()).append(", "));
			logger.debug(sb.toString());
		}
		return skills;
	}
	
	@Override
	public void saveRepositoryDirectories(Project project, List<SCMChange> changes) throws SkillerException {

		final String filename = "project-" + project.getId() + "-pathnames.txt";

		List<String> directories = changes.stream()
				.map(SCMChange::getPath)
				.distinct()
				.map(this::extractDirectory)
				.distinct()
				.filter(path -> isDirectory(project, path))
				.sorted()
				.collect(Collectors.toList());

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving paths file %s", rootLocation.resolve(filename)));
		}		
		
		File file = createResetOrCreateFile(filename);
		
		try (Writer writer = new FileWriter(file)) {
			for (String dir : directories) {
				writer.write(dir);
				writer.write(Global.LN);
			}
        } catch (IOException ioe) {
        	throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
        }
	}


	@Override
	public List<String> loadRepositoryDirectories(Project project) throws SkillerException {
		
		final String filename = "project-" + project.getId() + "-pathnames.txt";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Loading the paths file %s", rootLocation.resolve(filename)));
		}

		File file = rootLocation.resolve(filename).toFile();
		try (Reader reader = new FileReader(file)) {
			BufferedReader br = new BufferedReader(reader);
			return br.lines().collect(Collectors.toList());
        } catch (IOException ioe) {
        	throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), ioe);
        }
		
	}


}
