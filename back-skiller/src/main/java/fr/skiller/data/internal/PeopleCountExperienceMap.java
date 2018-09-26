package fr.skiller.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class PeopleCountExperienceMap {

	public Map<String, Long> data = new HashMap<String, Long>();
	
	public void putAll(final Map<String, Long> countsSkillLevel) {
		data.putAll(countsSkillLevel);
	}
}
