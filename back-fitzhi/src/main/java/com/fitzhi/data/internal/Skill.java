package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Skill other = (Skill) obj;
		if (detectionTemplate == null) {
			if (other.detectionTemplate != null)
				return false;
		} else if (!detectionTemplate.equals(other.detectionTemplate))
			return false;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detectionTemplate == null) ? 0 : detectionTemplate.hashCode());
		result = prime * result + id;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	
}
