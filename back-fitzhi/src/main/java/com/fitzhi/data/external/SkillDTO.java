package com.fitzhi.data.external;

import com.fitzhi.data.internal.Skill;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>FIXME one day : I did not find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class SkillDTO extends BaseDTO {

	private Skill skill;

	/**
	 * @param skill
	 */
	public SkillDTO(Skill skill) {
		super();
		this.setSkill(skill);
	}

	/**
	 * @param skill
	 * @param code
	 * @param message
	 */
	public SkillDTO(Skill skill, int code, String message) {
		super();
		this.setSkill(skill);
		this.code = code;
		this.message = message;
	}

	/**
	 * Empty constructor.
	 */
	public SkillDTO() { }

	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	/**
	 * @param skill the skill to set
	 */
	private void setSkill(Skill skill) {
		this.skill = skill;
	}
}
