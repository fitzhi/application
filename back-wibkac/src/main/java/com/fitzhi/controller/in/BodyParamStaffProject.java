package com.fitzhi.controller.in;

import lombok.Data;

/**
 * <p>
 * Parameters used to add or remove a project from a staff member.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamStaffProject {
	
	/**
	 * The staff identifier.
	 */
	private int idStaff;
	
	/**
	 * The project identifier.
	 */
	private int idProject;

	public BodyParamStaffProject() {
		// Empty constructor declared for serialization / deserialization purpose 
	}

}
