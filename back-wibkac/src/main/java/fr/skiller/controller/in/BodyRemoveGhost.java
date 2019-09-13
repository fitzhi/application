package fr.skiller.controller.in;

/**
 * <p>
 * Internal container hosting all possible parameters required to remove a ghost from a project.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL 
 */
public class BodyRemoveGhost {
	
	public BodyRemoveGhost() { }
	
	/**
	 * the project identifier
	 */
	public int idProject;
	
	/**
	 * The ghost's pseudo
	 */
	public String pseudo;
}
