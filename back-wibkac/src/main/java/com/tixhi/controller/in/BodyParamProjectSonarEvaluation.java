package com.tixhi.controller.in;

import com.tixhi.controller.ProjectSonarController;
import com.tixhi.data.internal.SonarEvaluation;

import lombok.Data;

/**
 * <p>
 * Parameters passed to the controller method {@link ProjectSonarController#saveEvaluation(BodyParamProjectSonarEvaluation)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class BodyParamProjectSonarEvaluation {

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
	private SonarEvaluation sonarEvaluation;
	
}
