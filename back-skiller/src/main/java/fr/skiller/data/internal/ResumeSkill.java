package fr.skiller.data.internal;

/**
 * A Resume
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class ResumeSkill extends ResumeSkillIdentifier implements Comparable<ResumeSkill> {

	final static int LIMIT_LANGUAGE = 8;
	
	/**
	 * Title of the skill discovered inside the resume
	 */
	public String title;
	
	/**
	 * @param idSkill the skill identifier
	 * @param title title of the skill
	 * @param count number of occurrences of this skill in the resume
	 */
	public ResumeSkill(int idSkill, String title, long count) {
		super(idSkill, count);
		this.title = title;
	}

	/**
	 * Empty constructor.
	 */
	public ResumeSkill() {
		super();
	}

	@Override
	public int compareTo(ResumeSkill o) {
		// The first 7 skills are languages, they are inserted on top of the list.
		if (o.getIdSkill() <= LIMIT_LANGUAGE) {
			return 1;
		} else {
			return (int) (o.getCount() - this.getCount());
		}
	}

}
