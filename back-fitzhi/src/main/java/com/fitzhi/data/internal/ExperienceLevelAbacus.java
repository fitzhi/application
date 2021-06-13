package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * The abacus used to evaluate the level of an experience in conjunction with the detected count value in the repository/
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ExperienceLevelAbacus {

	/**
	 * This object identifier.
	 */
	private final int idELA;

	/**
	 * The experience detection template
	 */
	private final int idExperienceDetectionTemplate;

	/**
	 * The Skill identifier.
	 */
	private int idSkill;

	/**
	 * The level in the skill corresponding to this codePattern.
	 */
	private int level;

	/**
	 * The value to be reached to obtain this level.
	 */
	private int value;

}
