package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * Class representing a skill in Fitzhì.
 * <br/>
 * It might be {@code Java}, or {@code Spring}, or {@code Hadoop}
 * <br/>
 * Each skill has a non mandatory {@link SkillDetectionTemplate detection template}  available 
 * to retrieve that skill inside a repository.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@EqualsAndHashCode
 public @Data class Skill implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1549084879092445214L;

	private int id = 0;
	
	private String title = "";

	private SkillDetectionTemplate detectionTemplate;

	/**
	 * Empty constructor for serialization purpose.
	 */
	public Skill() {
	}
	
	/**
	 * Public construction of a Skill without detectionPattern.
	 * @param id The unique identifier of this skill
	 * @param title the title of this skill
	 */
	public Skill(int id, String title) {
		this(id, title, null);
	}
	
	/**
	 * Public construction of a Skill
	 * @param id The skill identifier
	 * @param title the title of this skill
	 * @param detectionTemplate detection template for this skill in the repository
	 */
	public Skill(int id, String title, SkillDetectionTemplate detectionTemplate) {
		super();
		this.id = id;
		this.title = title;
		this.detectionTemplate = detectionTemplate;
	}
	
}
