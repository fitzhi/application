package fr.skiller.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class PeopleCountExperienceMap {

	public Map<String, Long> peopleCountExperience = new HashMap<String, Long>();
	
	public void putAll(final Map<String, Long> countsSkillLevel) {
		peopleCountExperience.putAll(countsSkillLevel);
	}
}
