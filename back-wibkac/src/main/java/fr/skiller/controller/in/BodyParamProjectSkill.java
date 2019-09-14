package fr.skiller.controller.in;

import lombok.Data;

/**
 * <p>
 * Parameters passed to the controller inside the body.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class BodyParamProjectSkill {

	public BodyParamProjectSkill() {
		// Empty constructor declared for serialization / deserialization purpose 
	}

	/**
	 * The given project identifier
	 */
	private int idProject;

	/**
	 * The given skill identifier
	 */
	private int idSkill;
}
