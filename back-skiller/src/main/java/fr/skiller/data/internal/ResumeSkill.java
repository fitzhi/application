/**
 * 
 */
package fr.skiller.data.internal;

/**
 * One line in the resume.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ResumeSkill {
	/**
	 * A skill discovered inside the resume
	 */
	public String skill;
	
	/**
	 * Number of occurrences of this skill inside the resume.
	 */
	public long occurrence;
	
	/**
	 * @param skill
	 * @param occurrence
	 */
	public ResumeSkill(String skill, long occurrence) {
		super();
		this.skill = skill;
		this.occurrence = occurrence;
	}

	/**
	 * Empty constructor.
	 */
	public ResumeSkill() {
		super();
	}

	
}
