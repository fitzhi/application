package fr.skiller.data.external;

import fr.skiller.data.internal.Collaborator;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>FIXME one day : I did not find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class StaffDTO {

	/**
	 * Back-end code
	 */
	public int code = 0;
	/**
	 * Back-end message
	 */
	public String message = "";

	public Collaborator staff;

	/**
	 * @param staff
	 */
	public StaffDTO(Collaborator staff) {
		super();
		this.staff = staff;
	}

	/**
	 * @param staff
	 * @param code
	 * @param message
	 */
	public StaffDTO(Collaborator staff, int code, String message) {
		super();
		this.staff = staff;
		this.code = code;
		this.message = message;
	}

}
