package fr.skiller.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Skills extracted from the resume or CV file.
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class DeclaredExperience  {

	private Map<String, Long> experience = new HashMap<String, Long>();

	/**
	 * @return the data inside the object.
	 */
	public Map<String, Long> data() {
		return experience;
	}
	
	/**
	 * add an experience in the Map.
	 * @param skill
	 * @param value
	 * @return
	 */
	public Long put(String skill, Long value) {
		return experience.put(skill, value);
	} 
	
	
}
