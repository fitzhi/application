package com.tixhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * The Sonar project evaluation
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class SonarEvaluation implements Serializable {

	/**
	 * serialVersionUID for serialization purpose.
	 */
	private static final long serialVersionUID = -2735161732833658364L;

	/**
	 * The evaluation processed of a Sonar project
	 */
	private int evaluation;
	
	/**
	 * The total number of lines of code saved for this project.
	 */
	private int totalNumberLinesOfCode;
}
