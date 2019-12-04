export class SettingsGeneration {
	/**
	 * Project identifier.
	 */
	public idProject: number;

	/**
	 * Starting date of investigation; Number of milliseconds since EPOC.
	 */
	public startingDate: number;

	/**
	 * selected staff identifier.
	 */
	public idStaffSelected: number;

	/**
	 * @param idProject Project identifier.
	 * @param startingDate Starting date of investigation; Number of milliseconds since EPOC.
	 * @param idStaffSelected selected staff identifier.
	 */
	public constructor(idProject: number, startingDate: number, idStaffSelected: number) {
		this.idProject = idProject;
		this.startingDate = startingDate;
		this.idStaffSelected = idStaffSelected;
	}

}
