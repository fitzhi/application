/**
 * 
 */
package fr.skiller.data.source.importance;

import static fr.skiller.Error.CODE_IO_ERROR;
import static fr.skiller.Error.MESSAGE_IO_ERROR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;

/**
 * <p>
 *  Simple file size evaluator.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class FileSizeImportance implements AssessorImportance {

	@Override
	public long getImportance(Project project, String path, ImportanceCriteria criteria) throws SkillerException {
		try {
			return Files.size(Paths.get(project.getLocationRepository() + "/" + path));
		} catch (IOException ioe) {
			throw new SkillerException(CODE_IO_ERROR, MESSAGE_IO_ERROR, ioe);
		}
	}

}
