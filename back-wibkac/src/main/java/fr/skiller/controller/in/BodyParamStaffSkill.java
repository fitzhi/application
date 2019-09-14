package fr.skiller.controller.in;

import lombok.Data;

/**
 * <p>
 * Internal Parameters class containing all possible parameters necessaries
 * for add/remove a skill from a staff member.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamStaffSkill {
	
	private int idStaff;
	private int idSkill;
	private int level;
	private String formerSkillTitle;
	private String newSkillTitle;

	public BodyParamStaffSkill() {
		// Empty constructor declared for serialization / deserialization purpose 		
	}
	
}
