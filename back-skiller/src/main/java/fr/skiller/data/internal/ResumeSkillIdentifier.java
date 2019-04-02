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
	private int idSkill;
	
	/**
	 * Number of occurrences of this skill inside the resume.
	 */
	private long count;
	
	/**
	 * @param idSkill the skill identifier
	 * @param count number of occurrences of this skill in the resume
	 */
	public ResumeSkillIdentifier(int idSkill, long count) {
		super();
		this.setIdSkill(idSkill);
		this.setCount(count);
	}

	/**
	 * Empty constructor.
	 */
	public ResumeSkillIdentifier() {
		super();
	}

	/**
	 * @return the idSkill
	 */
	public int getIdSkill() {
		return idSkill;
	}

	/**
	 * @param idSkill the idSkill to set
	 */
	public void setIdSkill(int idSkill) {
		this.idSkill = idSkill;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}

	
}
