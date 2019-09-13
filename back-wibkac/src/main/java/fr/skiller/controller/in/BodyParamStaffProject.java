package fr.skiller.controller.in;

/**
 * <p>
 * Parameters used to add or remove a project from a staff member.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class BodyParamStaffProject {
	
	public BodyParamStaffProject() {}
	
	/**
	 * The staff identifier.
	 */
	public int idStaff;
	
	/**
	 * The project identifier.
	 */
	public int idProject;

	@Override
	public String toString() {
		return "ParamStaffProject [idStaff=" + idStaff + ", idProject=" + idProject + "]";
	}

}
