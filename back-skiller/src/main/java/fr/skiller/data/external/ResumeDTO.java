package fr.skiller.data.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkill;

public class ResumeDTO {

	/**
	 * Back-end code
	 */
	public int code = 0;
	
	/**
	 * Back-end message
	 */
	public String message = "";


	public List<ResumeSkill> experience = new ArrayList<ResumeSkill>();

	/**
	 * Empty constructor
	 */
	public ResumeDTO() {
	}
	
	/**
	 * @param experience found in the resume.
	 */
	public ResumeDTO(Resume declaredExperience) {
		this.experience = declaredExperience.data();
	}

	/**
	 * @param experience found in the resume.
	 * @param code error code
	 * @param message error message
	 */
	public ResumeDTO(Resume declaredExperience, int code, String message) {
		this.experience = declaredExperience.data();
		this.code = code;
		this.message = message;
	}
}
