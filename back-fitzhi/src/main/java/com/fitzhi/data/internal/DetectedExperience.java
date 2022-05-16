package com.fitzhi.data.internal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Exemplary experience detected in the repository.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@ToString
public @Data class DetectedExperience {

	/**
	 * identifier to isolate some special code patterns which characterize the level of a developer in a skill.
	 */
	private final int idExperienceDetectionTemplate;

	/**
	 * The Projecty where this experience has been decteted for the author.
	 */
	private final int idProject;

	/**
	 * Author of a GIT commit as retrieved from the repository.
	 */
	@NotNull private final Author author;
	
	/**
	 * Number of references detected in project.
	 */
	private int count = 0;

	/**
	 * Increment the {@code count} field and return the new value.
	 * @return the new value of {@code count}
	 */
	public int inc() {
		this.count++;
		return this.count;
	}

	/**
	 * Add a value to the {@code count} field and return the new count value.
	 * @param value the value to be added to the current count.
	 * @return the current value
	 */
	public int add(final int value) {
		this.count += value;
		return this.count;
	}

	/**
	 * Generate and return a key usefukl to aggregate the global experiences
	 * @return
	 */
	public AuthorExperienceTemplate getKeyAggregateExperience() {
		return AuthorExperienceTemplate.of(idExperienceDetectionTemplate, author);
	}
}
