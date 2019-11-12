package fr.skiller.data.internal;

import lombok.Data;

/**
 * <p>
 * The Sonar project evaluation
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class SonarEvaluation {

	/**
	 * The evaluation processed of a Sonar project
	 */
	private int evaluation;
	
	/**
	 * The total number of lines of code saved for this project.
	 */
	private int totalNumberLinesofCode;
}
