package fr.skiller.controller.in;

import lombok.Data;

/**
 * <p>
 * Parameter sent to the controller in order to obtain the sunburst data.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class SettingsGeneration {
	/**
	 * Project identifier.
	 */
	private int idProject;

	/**
	 * Starting date of investigation.
	 */
	private long startingDate;

	/**
	 * selected staff identifier.
	 */
	private int idStaffSelected;

	/**
	 * ParamSunburst.
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
	 * @param idProject       project identifier.
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
