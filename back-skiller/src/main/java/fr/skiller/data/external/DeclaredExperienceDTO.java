package fr.skiller.data.external;

import java.util.HashMap;
import java.util.Map;

import fr.skiller.data.internal.DeclaredExperience;

public class DeclaredExperienceDTO {

	/**
	 * Back-end code
	 */
	public int code = 0;
	
	/**
	 * Back-end message
	 */
	public String message = "";


	public Map<String, Long> experience = new HashMap<String, Long>();

	/**
	 * Empty constructor
	 */
	public DeclaredExperienceDTO() {
	}
	
	/**
	 * @param experience found in the resume.
	 */
	public DeclaredExperienceDTO(DeclaredExperience declaredExperience) {
		this.experience = declaredExperience.data();
	}

	/**
	 * @param experience found in the resume.
	 * @param code
	 * @param message
	 */
	public DeclaredExperienceDTO(DeclaredExperience declaredExperience, int code, String message) {
		this.experience = declaredExperience.data();
		this.code = code;
		this.message = message;
	}
}
