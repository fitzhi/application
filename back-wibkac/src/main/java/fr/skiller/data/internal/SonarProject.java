package fr.skiller.data.internal;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * Class representing an entry of a project on Sonar
 *</p>
 * 
 * @author Frédéric VIDAL
 *
 */
public @Data class SonarProject {

	/**
	 * Key of a Sonar entry
	 */
	String key;

	/**
	 * Name of a Sonar entry
	 */
	String name;
	
	/**
	 * List of Sonar metrics chosen to evaluate this project.
	 */
	private List<ProjectSonarMetricValue> projectSonarMetricValues = new ArrayList<>();

	/**
	 * Statistics retrieved from the Sonar instance.
	 */
	private List<FilesStats> projectFilesStats = new ArrayList<>();
	
	
	public SonarProject() {
		// Empty constructor for serialization / de-serialization purpose
	}

	/**
	 * Sonar entry.
	 * @param key identifier of the sonar project entry.
	 * @param name name of the sonar project entry.
	 */
	public SonarProject(String key, String name) {
		this(key, name, new ArrayList<ProjectSonarMetricValue>());
	}

	/**
	 * Sonar entry.
	 * @param key identifier of the sonar project entry.
	 * @param name name of the sonar project entry.
	 */
	public SonarProject(String key, String name, List<ProjectSonarMetricValue> projectSonarMetrics) {
		super();
		this.key = key;
		this.name = name;
		this.projectSonarMetricValues = projectSonarMetrics;
	}
	
}
