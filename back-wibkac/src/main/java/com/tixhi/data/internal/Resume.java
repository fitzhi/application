package com.tixhi.data.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Skills extracted from the resume or CV file.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Resume {

	private List<ResumeSkillIdentifier> experience = new ArrayList<>();

	/**
	 * @return the data inside the object.
	 */
	public List<ResumeSkillIdentifier> data() {
		return experience;
	}

	/**
	 * Store an experience detected within the resume.
	 * 
	 * @param idSkill the skill identifier
	 * @param value the count number of presence of this skill in the resume
	 */
	public void put(int idSkill, Long value) {
		experience.add(new ResumeSkillIdentifier(idSkill, value));
	}

}
