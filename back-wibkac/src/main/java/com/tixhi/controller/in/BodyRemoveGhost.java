package com.tixhi.controller.in;

import lombok.Data;

/**
 * <p>
 * Internal container hosting all possible parameters required to remove a ghost from a project.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL 
 */
public @Data class BodyRemoveGhost {
	
	public BodyRemoveGhost() {
		// Empty constructor declared for serialization / deserialization purpose 
	}
	
	/**
	 * the project identifier
	 */
	private int idProject;
	
	/**
	 * The ghost's pseudo
	 */
	private String pseudo;
}
