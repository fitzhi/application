package fr.skiller.data.internal;

import lombok.Data;

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
