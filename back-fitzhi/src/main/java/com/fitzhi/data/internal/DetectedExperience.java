package com.fitzhi.data.internal;

import static com.fitzhi.Global.UNKNOWN;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Exemplary experience detected in the repository.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
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
	 * Staff identifier associated with this author.
	 */
	public int idStaff = UNKNOWN;

	/**
	 * Increment the {@code count} field and return the new value.
	 * @return the new value of {@code count}
	 */
	public int inc() {
		this.count++;
		return this.count;
	}
}
