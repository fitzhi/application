package com.fitzhi.bean;

import java.util.List;

import com.fitzhi.data.internal.ProjectSonarMetricValue;
import com.fitzhi.exception.ApplicationException;

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
	 * Load from the referential the default list of {@link ProjectSonarMetricValue} declared in this server
	 * </p>
	 *  
	 * @return the resulting list or {@code null} if an error occurs.
	 * @throws ApplicationException exception thrown if any problem occurs
	 */
	List<ProjectSonarMetricValue> getDefaultProjectSonarMetrics() throws ApplicationException;
}
