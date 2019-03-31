/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_IO_ERROR;
import static fr.skiller.Error.MESSAGE_IO_ERROR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.ShuffleService;
import fr.skiller.data.internal.Project;
import fr.skiller.data.internal.Skill;
import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

/**
 * Implementation of DataSaver on the file system.
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
	@Value("${fileDataSaver.save_dir}")
	private String saveDir;

	/**
	 * The logger for the GitScanner.
	 */
	private Logger logger = LoggerFactory.getLogger(FileDataSaverImpl.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	ProjectHandler staffHandler;

	@Override
	public void saveProjects(Map<Integer, Project> projects) throws SkillerException {

		/**
		 * We do not save the projects in shuffle mode.
		 */
		if (shuffleService.isShuffleMode()) {
			return;
		}

		final String filename = saveDir + "projects.json";

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving %d projects into file %s.", projects.size(), filename));
			final StringBuilder sb = new StringBuilder();
			projects.values().stream()
					.forEach(project -> sb.append(project.id).append(" ").append(project.name).append(", "));
			logger.debug(sb.toString());
		}

		Path path = Paths.get(filename);

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

		final String filename = saveDir + "projects.json";

		Map<Integer, Project> projects = new HashMap<>();

		try (FileReader fr = new FileReader(new File(filename))) {
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
					.forEach(project -> sb.append(project.id).append(" ").append(project.name).append(", "));
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

		final String filename = saveDir + "staff.json";

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving %d staff members into file %s.", company.size(), filename));
			final StringBuilder sb = new StringBuilder();
			company.values().stream()
					.forEach(staff -> sb.append(staff.idStaff).append(" ").append(staff.lastName).append(", "));
			logger.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(new File(filename))) {
			fw.write(gson.toJson(company));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public Map<Integer, Staff> loadStaff() throws SkillerException {

		final String filename = saveDir + "staff.json";

		Map<Integer, Staff> theStaff = new HashMap<>();

		try (FileReader fr = new FileReader(new File(filename))) {
			Type listStaffType = new TypeToken<HashMap<Integer, Staff>>() {
			}.getType();
			theStaff = gson.fromJson(fr, listStaffType);
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Loading %d staff members from file %s.", theStaff.size(), filename));
			final StringBuilder sb = new StringBuilder();
			theStaff.values().stream()
					.forEach(staff -> sb.append(staff.idStaff).append(" ").append(staff.lastName).append(", "));
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

		final String filename = saveDir + "skills.json";

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Saving %d skills into file %s.", skills.size(), filename));
			final StringBuilder sb = new StringBuilder();
			skills.values().stream().forEach(skill -> sb.append(skill.id).append(" ").append(skill.title).append(", "));
			logger.debug(sb.toString());
		}

		try (FileWriter fw = new FileWriter(new File(filename))) {
			fw.write(gson.toJson(skills));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		}
	}

	@Override
	public Map<Integer, Skill> loadSkills() throws SkillerException {

		final String filename = saveDir + "skills.json";

		Map<Integer, Skill> skills = new HashMap<>();

		try (FileReader fr = new FileReader(new File(filename))) {
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
			skills.values().stream().forEach(skill -> sb.append(skill.id).append(" ").append(skill.title).append(", "));
			logger.debug(sb.toString());
		}
		return skills;
	}

}
