package fr.skiller.bean;

import java.util.List;

import fr.skiller.data.internal.ProjectSonarMetric;
import fr.skiller.exception.SkillerException;

/**
 * <p>
 * Interface in charge of handling the Sonar data
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface SonarHandler {

	/**
	 * <p>
	 * Load from the referential the default list of {@link ProjectSonarMetric} declared in this server
	 * </p>
	 *  
	 * @return the resulting list or {@code null} if an error occurs.
	 * @throws SkillerException exception thrown if any problem occurs
	 */
	List<ProjectSonarMetric> getDefaultProjectSonarMetrics() throws SkillerException;
}
