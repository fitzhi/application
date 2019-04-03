package fr.skiller.data.external;

import java.util.ArrayList;
import java.util.List;

import fr.skiller.data.internal.ResumeSkill;

public class ResumeDTO extends BaseDTO {

	public final List<ResumeSkill> experience = new ArrayList<>();

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
