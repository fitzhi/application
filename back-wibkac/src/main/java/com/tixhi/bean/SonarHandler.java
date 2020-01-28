package com.tixhi.bean;

import java.util.List;

import com.tixhi.data.internal.ProjectSonarMetricValue;
import com.tixhi.exception.SkillerException;

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
	 * @throws SkillerException exception thrown if any problem occurs
	 */
	List<ProjectSonarMetricValue> getDefaultProjectSonarMetrics() throws SkillerException;
}
