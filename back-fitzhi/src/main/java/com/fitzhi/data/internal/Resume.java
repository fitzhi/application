package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Skills extracted from the resume or CV file.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Data
public class Resume {

	private List<ResumeSkillIdentifier> experiences = new ArrayList<ResumeSkillIdentifier>();

	/**
	 * Store an experience detected within the resume.
	 * 
	 * @param idSkill the skill identifier
	 * @param value the count number of presence of this skill in the resume
	 */
	public void put(int idSkill, Long value) {
		experiences.add(new ResumeSkillIdentifier(idSkill, value));
	}

}
