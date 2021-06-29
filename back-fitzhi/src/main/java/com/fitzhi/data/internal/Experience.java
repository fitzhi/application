package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * Technical background of a developer, an expert, or any staff member of the company.
 * An experience is a level obtained in a skill. We manage 2 kinds of level :
 * <ul>
 * <li>the level attributed by a user (<em>it's a human appreciation of a skill</em>)</li>
 * <li>the system level (<em>The level processed by the system.</em>). 
 * This appreciation is based <b>only</b> on the activity detected in the system. 
 * If James Gosling executes one single line commit on a single file, he'll be considered as a junior Java developer.</li>
 * </ul>
 * </p>
 * <br/>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Experience implements Serializable {

	/**
	 * For serialization purpose.
	 */
	private static final long serialVersionUID = -8716755186690093914L;

	/**
	 * Identifier of the experience identical with the identifier of the skill
	 */
	private int id;
	
	/**
	 * Level given and forced by a user, on this experience.
	 * This is the evaluation given by a pair.
	 */
	private int level;

	/**
	 * The level in this experience has been given by a pair.
	 * 
	 * The level can be given, or processed.
	 */
	private boolean forced = false;

	/**
	 * Level evaluated by the system.
	 */
	private int systemLevel = -1;

	/**
	 * Empty constructor.
	 */
	public Experience() {
		super();
	}

	/**
	 * Constructor with parameters.
	 * @param id the id of the skill in an experience
	 * @param level the degree of knowledge obtained by a developer on this skill
	 */
	public Experience(final int id, final int level) {
		this (id, level, -1);
	}
	
	/**
	 * Constructor with parameters.
	 * @param idSkill the Skill identifier
	 * @param level the level of knowledge given by a pair
	 * @param systemLevel the System level processed by Hal
	 */
	public Experience(final int idSkill, final int level, final int systemLevel) {
		this.setId(idSkill);
		this.setLevel(level);
		this.setSystemLevel(systemLevel);
	}

	/**
	 * This is a key pattern executed to create the key of experience used into map of data.
	 * Theses Maps are used for data exchange with the Angular application.
	 * @return key : constructed key
	 */
	public String key() {
		return getId() + "-" + getLevel();
	}

}
