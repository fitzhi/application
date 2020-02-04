package com.fitzhi.controller.in;

import java.util.List;

import com.fitzhi.controller.ProjectSonarController;
import com.fitzhi.data.internal.ProjectSonarMetricValue;

import lombok.Data;

/**
 * <p>
 * Parameters passed to the controller method {@link ProjectSonarController#updateMetricValues(fr.skiller.controller.BodyParameProjectSonarMetricValues)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class BodyParamProjectSonarMetricValues {

	/**
	 * The given project identifier
	 */
	private int idProject;

	/**
	 * The given Sonar key
	 */
	private String sonarKey;
	
	/**
	 * Sonar metric values and their weight passed to method updateMetricValues.
	 */
	private List<ProjectSonarMetricValue> metricValues;
	
}
