/**
 * 
 */
package com.fitzhi.data.source.importance;

import static com.fitzhi.Error.CODE_IO_ERROR;
import static com.fitzhi.Error.MESSAGE_IO_ERROR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fitzhi.data.internal.Project;
import com.fitzhi.exception.SkillerException;

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
