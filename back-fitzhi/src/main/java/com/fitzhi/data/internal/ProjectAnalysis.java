package com.fitzhi.data.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>
 * This class contains the resulting analysis of a project.
 * This result is processed by a slave of Fitzhi and sent to the main application.
 * The project will be updated there with these data.
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ProjectAnalysis {

	/**
	 * The project identifier
	 */
	private int id;
	
	/**
	 * Map of ProjectSkill indexed by {@code idSkill} detected or declared for this project.
	 */
	private Map<Integer, ProjectSkill> skills = new HashMap<>();
	
	/**
	 * List of committer {@link com.fitzhi.data.internal.Ghost ghosts} identified.<br/>
	 * These ghosts are known by their pseudos <br/>
	 * Either they are missing from the staff collection, or they are technical.
	 */
	private List<Ghost> ghosts = new ArrayList<>();
	
	/**
	 * Staff evaluation : representing the percentage of active developers able to maintain the project.
	 */
	private int staffEvaluation = -1;
	
	/**
	 * "ordered" list by weight of ecosystems detected on the repository by either this application, or Sonar
	 */
	private List<Integer> ecosystems = new ArrayList<>();
	
	/**
	 * Empty constructor.
	 */
	public ProjectAnalysis() { }
	
	/**
	 * @param id Project identifier
	 */
	public ProjectAnalysis(int id) {
		this.id = id;
	}

	/**
	 * @param project Project the given project
	 */
	public ProjectAnalysis(Project project) {
		id = project.getId();
		skills = project.getSkills();
		ghosts = project.getGhosts();
		staffEvaluation = project.getStaffEvaluation();
		ecosystems = project.getEcosystems();
	}

}
