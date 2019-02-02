/**
 * 
 */
package fr.skiller.data.external;

import java.util.ArrayList;
import java.util.List;

import fr.skiller.data.internal.Pseudo;
import fr.skiller.exception.SkillerException;


/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class PseudoListDTO extends BaseDTO {

	/**
	 * Identifier of project;
	 */
	public final int idProject;
	
	/**
	 * The list of unknown pseudos IN and OUT.
	 */
	public final List<Pseudo> unknowns;

	/**
	 * @param idProject project identifier
	 * @param unknowns list of "consolidated" pseudos
	 */
	public PseudoListDTO(final int idProject, final List<Pseudo> unknowns) {
		super();
		this.idProject = idProject;
		this.unknowns = unknowns;
	}

	/**
	 * @param idProject current project identifier.
	 * @param e exception thrown by the controller
	 */
	public PseudoListDTO(final int idProject, SkillerException e) {
		super(e.errorCode, e.errorMessage);
		unknowns = new ArrayList<Pseudo>();
		this.idProject = idProject;
	}
	
}
