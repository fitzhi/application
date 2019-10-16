package fr.skiller.data.internal;

import lombok.Data;

/**
 * 
 * <p>
 * This class represents a metric key and its weight in the evaluation of a project.
 * </p>
 * <p>
 * You might have an instance with these 2 fields
 * <ul>
 * <li>{@code "metric": "bugs"}</li>
 * <li>{@code "value": 30}</li>
 * </ul>
 * which means the metric note count as 30% in the global note.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class ProjectSonarMetric {

	/**
	 * Key of the metric
	 */
	String key;
	
	/**
	 * Weight of this metric in the global evaluation.
	 */
	int weight;
	
	public ProjectSonarMetric() {
		// Empty constructor for serialization / de-serialization purpose		
	}

	/**
	 * Constructor.
	 * @param key the given metric key
	 * @param weight the proportion of this metric in the global evaluation.
	 */
	public ProjectSonarMetric(String key, int weight) {
		super();
		this.key = key;
		this.weight = weight;
	}
	
	
}
