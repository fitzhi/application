package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Skills extracted from the resume or CV file.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class Resume {

	private List<ResumeSkill> experience = new ArrayList<ResumeSkill>();

	/**
	 * @return the data inside the object.
	 */
	public List<ResumeSkill> data() {
		return experience;
	}

	/**
	 * Store an experience detected within the resume.
	 * 
	 * @param skill
	 * @param value
	 */
	public void put(String skill, Long value) {
		experience.add(new ResumeSkill(skill, value));
	}

}
