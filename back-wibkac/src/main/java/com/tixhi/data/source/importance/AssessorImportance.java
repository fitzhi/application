package com.tixhi.data.source.importance;

import com.tixhi.data.internal.Project;
import com.tixhi.exception.SkillerException;
import com.tixhi.source.crawler.git.SCMChange;

/**
 * <p>
 * This interface give access to the technical importance of a source file.<br/>
 * This importance relies on :
 * <ul>
 * <li>the size of a file</li>
 * <li>the complexity</li>
 * <li>any another criteria coming soon on a theater near you</li>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface AssessorImportance {

	/**
	 * <p>Evaluate the importance of a change.</p>
	 * <p>
	 * <i>
	 * The first natural implementation of this interface is the file size of a source. 
	 * As large is a file, as important is this file.
	 * </i>
	 * </p>
	 * @param project the current project actually crawled.
	 * @param path the given path whose importance has to be evaluated
	 * @param criteria the criteria of importance
	 * @return the evaluated importance
	 * @throws SkillerException thrown if any exception occurs during the evaluation
	 */
	long getImportance(Project project, String path, ImportanceCriteria criteria) throws SkillerException;
}
