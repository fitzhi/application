package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * The abacus used to evaluate the level of an experience in conjunction with the detected count value in the repository/
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ExperienceAbacus {


	public ExperienceAbacus(final int idAbacus, final int idExperienceDetectionTemplate) {
		this.idAbacus = idAbacus;
		this.idExperienceDetectionTemplate = idExperienceDetectionTemplate;
	}

	/**
	 * This object identifier.
	 */
	private final int idAbacus;

	/**
	 * The experience detection template
	 */
	private final int idExperienceDetectionTemplate;

	/**
	 * The level in the skill corresponding to this codePattern.
	 */
	private int level;

	/**
	 * The value to be reached to obtain this level.
	 */
	private int value;

}
