package com.fitzhi.controller.in;

import lombok.Data;

/**
 * <p>
 * This object is an envelop for parameters sent to the controller in order to obtain the sunburst data.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class SettingsGeneration {
	
	/**
	 * Project identifier.
	 */
	private int idProject;

	/**
	 * Project repository URL 
	 * <em>This url set by the <code>Github action</code> will be used to retrieve the corresponding Fitzhi project.
	 */
	private String urlRepository = null;

	/**
	 * Branch of the repository URL to be used. 
	 * <em>This url set by the <code>Github action</code> will be used to retrieve the corresponding Fitzhi project.
	 */
	private String branch = null;

	/**
	 * Starting date of investigation.
	 */
	private long startingDate;

	/**
	 * selected staff identifier.
	 */
	private int idStaffSelected;

	/**
	 * Empty construction of this object.
	 */
	public SettingsGeneration() {
	}

	/**
	 * @param idProject project identifier
	 */
	public SettingsGeneration(final int idProject) {
		this.idProject = idProject;
	}

	/**
	 * @param urlRepository the project repository URL 
	 * <em>This url will be given by the <code>Github action</code> to the slave, in order to to retrieve the corresponding Fitzhi project</em>.
	 */
	public SettingsGeneration(String urlRepository) {
		this.urlRepository = urlRepository;
	}

	/**
	 * @param idProject project identifier.
	 * @param idStaffSelected staff identifier selected.
	 */
	public SettingsGeneration(final int idProject, final int idStaffSelected) {
		this.idProject = idProject;
		this.setIdStaffSelected(idStaffSelected);
	}

	/**
	 * @return {@code true} if the repository requires personalization,
	 *         {@code false} otherwise.
	 */
	public boolean requiresPersonalization() {
		return (getIdStaffSelected() > 0 || getStartingDate() > 0);
	}

}
