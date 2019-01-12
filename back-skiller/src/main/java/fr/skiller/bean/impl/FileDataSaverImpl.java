/**
 * 
 */
package fr.skiller.bean.impl;

import static fr.skiller.Error.CODE_IO_ERROR;
import static fr.skiller.Error.MESSAGE_IO_ERROR;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
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
import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * Implementaion of DataSaver on the file system.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
public class FileDataSaverImpl implements DataSaver {
	
	/**
	 * Directory where data will be saved.
	 */
	@Value("${fileDataSaver.save_dir}")
	private String save_dir;
	
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
	
	@Override
	public void save(Map<Integer, Project> projects) throws SkillerException {
		
		final String filename = save_dir+"projects.json";
		
		if (logger.isDebugEnabled()) {
			logger.debug("Saving " + projects.size() + " projects into file " + filename + ".");
			final StringBuilder sb = new StringBuilder();
			projects.values().stream().forEach(project -> sb.append(project.id).append(" ").append(project.name).append(", "));
			logger.debug(sb.toString());
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(filename));
			fw.write(gson.toJson(projects));
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		} finally {
			if (fw != null) try { fw.close();} catch (final Exception e) { /* Exception muffled */ }
		}
	}

	@Override
	public Map<Integer, Project> load() throws SkillerException {

		final String filename = save_dir+"projects.json";
		
		Map<Integer, Project> projects = new HashMap<Integer, Project>();
		
		FileReader fr = null;
		try {
			fr = new FileReader(new File(filename));
			Type listProjectsType = new TypeToken<HashMap<Integer, Project>>() {
			}.getType();
			projects = gson.fromJson(fr, listProjectsType);
		} catch (final Exception e) {
			throw new SkillerException(CODE_IO_ERROR, MessageFormat.format(MESSAGE_IO_ERROR, filename), e);
		} finally {
			if (fr != null) try { fr.close();} catch (final Exception e) { /* Exception muffled */ }
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Loading " + projects.size() + " projects into file " + filename + ".");
			final StringBuilder sb = new StringBuilder();
			projects.values().stream().forEach(project -> sb.append(project.id).append(" ").append(project.name).append(", "));
			logger.debug(sb.toString());
		}
		return projects;
	}
}
