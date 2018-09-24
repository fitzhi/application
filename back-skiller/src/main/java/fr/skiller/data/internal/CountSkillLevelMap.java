package fr.skiller.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class CountSkillLevelMap {

	public Map<String, Long> counts = new HashMap<String, Long>();
	
	public void putAll(final Map<String, Long> countsSkillLevel) {
		counts.putAll(countsSkillLevel);
	}
}
