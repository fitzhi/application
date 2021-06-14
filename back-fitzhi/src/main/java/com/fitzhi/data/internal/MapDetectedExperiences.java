package com.fitzhi.data.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Map of detected experiences.</p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class MapDetectedExperiences {

	final Map<String, DetectedExperience> map = new HashMap<>();
	
	public static MapDetectedExperiences of() {
		return new MapDetectedExperiences();
	}

	/**
	 * Generate the key indexing this map.
	 * @param detectedExperience the detectedExperience whose key has to be processed
	 * @return the resulting key.
	 */
	public final static String key(DetectedExperience detectedExperience) {
		return String.format("%s-%s-%d", 
			detectedExperience.getAuthor().getName(), 
			(detectedExperience.getAuthor().getEmail() == null) ? "" : detectedExperience.getAuthor().getEmail(),
			detectedExperience.getIdExperienceDetectionTemplate());
	}

	/**
	 * Update the collection with a new {@link DetectedExperience}.
	 * <p><ul>
	 * <li>
	 * If this is a new author for this experience, we add a new record.
	 * </li>
	 * <li>
	 * If a record already exists, we increment by one the counter in the corresponding object.
	 * </li>
	 * </ul></p>
	 * 
	 * <br/>
	 * 
	 * @param detectedExperience the given {@link DetectedExperience detectedExperience}
	 * @return the corresponding detected experience
	 */
	public DetectedExperience add(DetectedExperience detectedExperience) {
		DetectedExperience de = map.get(MapDetectedExperiences.key(detectedExperience));
		if (de == null) {
			// We initialize the count to 1 if the given detectedExperience is not initialized.
			if (detectedExperience.getCount() == 0) {
				detectedExperience.setCount(1);
			}
			this.map.put(MapDetectedExperiences.key(detectedExperience), detectedExperience);
			return detectedExperience;
		} else {
			// We increment the count value.
			de.inc();
			return de;
		}
	}

	/**
	 * Retrieve the item associated with the key generated with the given {@link DetectedExperience detectedExperience}
	 * @param detectedExperience the given detectedExperience
	 * @return the retrieved {@link DetectedExperience detectedExperience}, or {@code null} if none exists.
	 */
	public DetectedExperience get(DetectedExperience detectedExperience) {
		return this.map.get(MapDetectedExperiences.key(detectedExperience));
	}
}
