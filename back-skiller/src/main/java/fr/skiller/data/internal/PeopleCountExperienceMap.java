package fr.skiller.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class PeopleCountExperienceMap {

	public Map<String, Long> data = new HashMap<String, Long>();
	
	/**
	 * @param countsSkillLevel additional map to be stored in the data object
	 */
	public void putAll(final Map<String, Long> countsSkillLevel) {
		data.putAll(countsSkillLevel);
	}

	/**
	 * Retrieved the number of employees with qualification (skill + level)
	 * @param experience the passed experience in this format <code>idSkill</code>-<code>level</code>, as 1-1
	 * @return the count number for this experience
	 */
	public Long get(final String experience) {
		return data.get(experience);
	}

}
