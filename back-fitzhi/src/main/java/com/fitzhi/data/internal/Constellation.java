package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/** FRVI8611
 * <p>
 * This object is called &laquo; <strong>Constellation</strong> &raquo; 
 * because its representation on the Angular front-end is a serie of &#x2605; stars.
 * </p>
 * <p>A constellation in fact, represents the number of units of skills registered for a skill.
 * Each &#x2605; represents a &laquo; unit of skill &raquo;. 
 * E.g., a Java expert (5 &#x2605;) brings 5 ​​units to the count, when  one novice developer (1 &#x2605;) brings one.
 * </p>
 * <p>
 * Every month, a constellation will be saved in Fitzhì.
 * </p>
 */
@AllArgsConstructor(staticName = "of")
public @Data class Constellation implements Serializable {

	/**
	 * Identifier of the skill corresponding to the constellation.
	 */
	int idSkill;

	/**
	 * Number of internal stars counted for this skill.
	 */
	private int starsNumber;
	
	/**
	 * Number of internal AND EXTERNAL stars counted for this skill.
	 */
	private int starsNumberWithExternal;
}
