package fr.skiller.data.external;

import fr.skiller.data.internal.Collaborator;
import fr.skiller.data.internal.Project;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>FIXME one day : I did not find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class ProjectDTO {

	/**
	 * Back-end code
	 */
	public int code = 0;
	/**
	 * Back-end message
	 */
	public String message = "";

	public Project project;

	/**
	 * @param staff
	 */
	public ProjectDTO(Project project) {
		super();
		this.project = project;
	}

	/**
	 * @param staff
	 * @param code
	 * @param message
	 */
	public ProjectDTO(Project project, int code, String message) {
		super();
		this.project = project;
		this.code = code;
		this.message = message;
	}

}
