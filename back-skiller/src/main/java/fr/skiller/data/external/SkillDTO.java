package fr.skiller.data.external;

import fr.skiller.data.internal.Skill;

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

	public Skill skill;

	/**
	 * @param skill
	 */
	public SkillDTO(Skill skill) {
		super();
		this.skill = skill;
	}

	/**
	 * @param skill
	 * @param code
	 * @param message
	 */
	public SkillDTO(Skill skill, int code, String message) {
		super();
		this.skill = skill;
		this.code = code;
		this.message = message;
	}

	/**
	 * Empty constructor.
	 */
	public SkillDTO() { }
}
