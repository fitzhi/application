package com.fitzhi.data.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;

/**
 * <p>Map of detected experiences.</p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@EqualsAndHashCode
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
		return key(detectedExperience.getIdExperienceDetectionTemplate(), detectedExperience.getAuthor());
	}

	/**
	 * Generate the key indexing this map.
	 * @param idExperienceDetectionTemplate the identifier of the experience
	 * @param author the author detected with this experience
	 * @return the resulting key.
	 */
	public final static String key(int idExperienceDetectionTemplate, Author author) {
		return String.format("%s-%s-%d", 
			author.getName(), 
			(author.getEmail() == null) ? "" : author.getEmail(),
			idExperienceDetectionTemplate);
	}

	/**
	 * Update the collection with a new {@link DetectedExperience}. 
	 * The purpose of this method is to register a detected experience which count <b>ONE BY ONE</b> for the global experience.
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
	public DetectedExperience inc(DetectedExperience detectedExperience) {
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
	 * Update the collection with a new {@link DetectedExperience}.
	 * The purpose of this method is to register a detected experience which count <b>BY ITS VALUE</b> the global experience.
	 * <p><ul>
	 * <li>
	 * If this is a new author for this experience, we add a new record.
	 * </li>
	 * <li>
	 * If a record already exists, we add to the current count value, the given one
	 * </li>
	 * </ul></p>
	 * 
	 * <br/>
	 * 
	 * @param detectedExperience the given {@link DetectedExperience detectedExperience}
	 * @return the corresponding detected experience
	 */
	public DetectedExperience add(DetectedExperience detectedExperience) {
		DetectedExperience de = map.get(key(detectedExperience));
		if (de == null) {
			this.map.put(key(detectedExperience), detectedExperience);
			return detectedExperience;
		} else {
			// We increment the count value.
			de.add(detectedExperience.getCount());
			return de;
		}
	}

	/**
	 * Retrieve the item associated with the key generated with the given {@link DetectedExperience detectedExperience}
	 * @param detectedExperience the given detectedExperience
	 * @return the retrieved {@link DetectedExperience detectedExperience}, or {@code null} if none exists.
	 */
	public DetectedExperience get(DetectedExperience detectedExperience) {
		return get(MapDetectedExperiences.key(detectedExperience));
	}

	/**
	 * Retrieve the item associated with the key generated with the given {@link DetectedExperience detectedExperience}
	 * @param key the given key in the Map
	 * @return the retrieved {@link DetectedExperience detectedExperience}, or {@code null} if none exists.
	 */
	public DetectedExperience get(String key) {
		return this.map.get(key);
	}

	/**
	 * @return the values contained in this map of experiences.
	 */
	public Collection<DetectedExperience> content() {
		return map.values();
	}
}

