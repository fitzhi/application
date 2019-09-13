package fr.skiller.controller.in;

/**
 * <p>
 * Internal container hosting all possible parameters required to manage a ghost
 * of a project.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class BodyUpdateGhost {
	
	public BodyUpdateGhost() {
	}

	/**
	 * The project identifier
	 */
	public int idProject;
	
	/**
	 * the ghost's pseudo
	 */
	public String pseudo;
	
	/**
	 * The staff's identifier
	 */
	public int idStaff;
	
	/**
	 * Status technical or not of the ghost.s
	 */
	public boolean technical;
}
