/**
 * 
 */
package com.fitzhi.data.source.importance;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 *  Simple file size evaluator.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class FileSizeImportance implements AssessorImportance {

	@Override
	public long getImportance(Project project, String path, ImportanceCriteria criteria) throws ApplicationException {
		try {
			return Files.size(Paths.get(project.getLocationRepository() + "/" + path));
		} catch (IOException ioe) {
			throw new ApplicationException(
				CODE_IO_ERROR, 
				MessageFormat.format(MESSAGE_IO_ERROR, Paths.get(project.getLocationRepository() + "/" + path)),
				ioe);
		}
	}

}
