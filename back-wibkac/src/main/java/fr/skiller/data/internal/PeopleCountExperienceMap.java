package fr.skiller.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class PeopleCountExperienceMap {

	private final Map<String, Long> data = new HashMap<>();
	
	/**
	 * @param countsSkillLevel additional map to be stored in the data object
	 */
	public void putAll(final Map<String, Long> countsSkillLevel) {
		getData().putAll(countsSkillLevel);
	}

	/**
	 * Retrieved the number of employees with qualification (skill + level)
	 * @param experience the passed experience in this format <code>idSkill</code>-<code>level</code>, as 1-1
	 * @return the count number for this experience
	 */
	public Long get(final String experience) {
		return getData().get(experience);
	}

	/**
	 * @return the data
	 */
	public Map<String, Long> getData() {
		return data;
	}

}
