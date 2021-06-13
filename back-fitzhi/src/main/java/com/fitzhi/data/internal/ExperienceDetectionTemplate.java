package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * This object contains the setting which permits to isolate some special code patterns,
 * which characterize the level of a developer in a skill.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ExperienceDetectionTemplate {

	/**
	 * This object identifier.
	 */
	private int idEDT;

	/**
	 * The Skill identifier.
	 */
	private int idSkill;

	/**
	 * The level in the skill corresponding to this codePattern.
	 */
	private int level;

	/**
	 * Patten to be used to detect the file associated to the skill
	 */
	private String filePattern;

	/**
	 * Patten to be used to detect a particular level in a skill.
	 */
	private String codePattern;

	/**
	 * Type of code pattern.
	 */
	private TypeCode typeCode;
	
}
