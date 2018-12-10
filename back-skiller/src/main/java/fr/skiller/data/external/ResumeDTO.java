package fr.skiller.data.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkill;
import fr.skiller.data.internal.ResumeSkillIdentifier;

public class ResumeDTO  {

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
	 * @param code error code
	 * @param message error message
	 */
	public ResumeDTO(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
