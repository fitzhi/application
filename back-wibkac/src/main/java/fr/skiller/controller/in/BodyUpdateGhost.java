package fr.skiller.controller.in;

import lombok.Data;

/**
 * <p>
 * Internal container hosting all possible parameters required to manage a ghost
 * of a project.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyUpdateGhost {
	
	public BodyUpdateGhost() {
		// Empty constructor declared for serialization / deserialization purpose 
	}

	/**
	 * The project identifier
	 */
	private int idProject;
	
	/**
	 * the ghost's pseudo
	 */
	private String pseudo;
	
	/**
	 * The staff's identifier
	 */
	private int idStaff;
	
	/**
	 * Status technical or not of the ghost.s
	 */
	private boolean technical;
}
