package fr.skiller.controller.in;

/**
 * <p>
 * Internal Parameters class containing all possible parameters necessaries
 * for add/remove a skill from a staff member.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class BodyParamStaffSkill {
	public int idStaff;
	public int idSkill;
	public int level;
	public String formerSkillTitle;
	public String newSkillTitle;

	@Override
	public String toString() {
		return "ParamSkillProject [idStaff=" + idStaff + ", idSkill=" + idSkill + ", level=" + level
				+ ", formerSkillTitle=" + formerSkillTitle + ", newSkillTitle=" + newSkillTitle + "]";
	}
}
