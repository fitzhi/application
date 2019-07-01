package fr.skiller.data.source.importance;

import fr.skiller.data.internal.Project;
import fr.skiller.exception.SkillerException;
import fr.skiller.source.crawler.git.SCMChange;

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
	 * Evaluate the importance of a change
	 * @param project the current project actually crawled.
	 * @param change the given change whose importance has to be evaluated
	 * @param criteria the criteria of importance
	 * @return the importance
	 * @throws SkillerException thrown if any exception occurs during the evaluation
	 */
	long getImportance(Project project, SCMChange change, ImportanceCriteria criteria) throws SkillerException;
}
