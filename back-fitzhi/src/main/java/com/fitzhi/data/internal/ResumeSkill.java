package com.fitzhi.data.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Resume
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Data
@NoArgsConstructor
public class ResumeSkill extends ResumeSkillIdentifier implements Comparable<ResumeSkill> {

	static final int LIMIT_LANGUAGE = 8;
	
	/**
	 * Title of the skill discovered inside the resume
	 */
	private String title;
	
	/**
	 * @param idSkill the skill identifier
	 * @param title title of the skill
	 * @param count number of occurrences of this skill in the resume
	 */
	public ResumeSkill(int idSkill, String title, long count) {
		super(idSkill, count);
		this.setTitle(title);
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
