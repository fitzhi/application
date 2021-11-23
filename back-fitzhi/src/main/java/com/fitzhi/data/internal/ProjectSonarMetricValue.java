package com.fitzhi.data.internal;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * <p>
 * This class represents a metric key, its weight in the evaluation of a project, and the value obtained.
 * </p>
 * You might have an instance with these 2 fields
 * <ul>
 * <li>{@code "metric": "bugs"}</li>
 * <li>{@code "weight": 40}</li>
 * <li>{@code "value": 15}</li>
 * </ul>
 * which means the metric note count as 30% in the global note.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class ProjectSonarMetricValue implements Serializable {

	/**
	 * serialVersionUID for serialization purpose.s
	 */
	private static final long serialVersionUID = -7490734232699346295L;

	/**
	 * Key of the metric
	 */
	String key;
	
	/**
	 * Weight of this metric in the global evaluation.
	 */
	int weight;
	
	/**
	 * Value of the evaluation for this metric.
	 */
	int value;
	
	
	public ProjectSonarMetricValue() {
		// Empty constructor for serialization / de-serialization purpose		
	}

	/**
	 * Constructor.
	 * @param key the given metric key
	 * @param weight the proportion of this metric in the global evaluation.
	 * @param value of the evaluation
	 */
	public ProjectSonarMetricValue(String key, int weight, int value) {
		super();
		this.key = key;
		this.weight = weight;
		this.value = value;
	}
	
	
}
