/**
 * 
 */
package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * Knowledge background of a developer, technical expert, any staff member of the company.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class Experience implements Serializable {

	/**
	 * For serialization purpose.
	 */
	private static final long serialVersionUID = -8716755186690093914L;

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
		this.setId(id);
		this.setLevel(level);
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
