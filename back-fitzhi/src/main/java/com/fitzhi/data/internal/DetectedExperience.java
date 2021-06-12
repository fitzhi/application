package com.fitzhi.data.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Exemplary experience detected in the repository.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@AllArgsConstructor
public @Data class DetectedExperience {

	/**
	 * identifier to isolate some special code patterns which characterize the level of a developer in a skill.
	 */
	private int idSkillLevelDetectionPatern;

	/**
	 * Number of references detected in project.
	 */
	private int count = 0;

	/**
	 * Author of a GIT commit as retrieved from the repository.
	 */
	public Author author;
}
