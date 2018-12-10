/**
 * 
 */
package fr.skiller.data.internal;

/**
 * One line in the resume.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ResumeSkillIdentifier {
	/**
	 * A skill discovered inside the resume
	 */
	public int idSkill;
	
	/**
	 * Number of occurrences of this skill inside the resume.
	 */
	public long count;
	
	/**
	 * @param idSkill the skill identifier
	 * @param count number of occurrences of this skill in the resume
	 */
	public ResumeSkillIdentifier(int idSkill, long count) {
		super();
		this.idSkill = idSkill;
		this.count = count;
	}

	/**
	 * Empty constructor.
	 */
	public ResumeSkillIdentifier() {
		super();
	}

	
}
