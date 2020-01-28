package com.tixhi.data.external;

import com.tixhi.data.internal.Project;

/**
 * <p>This class is used as a Data Transfer Object between the spring boot
 * application and the Angular front.</p> 
 * <p><i>In the future, one day, when I will be a better developer : I will find a way to use HTTP headers for transferring additional information <b>in the POST request</b>. 
 * So these data are embedded in the data transfer object.</i></p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class ProjectDTO extends BaseDTO {

	private Project project;

	/**
	 * @param project
	 */
	public ProjectDTO(Project project) {
		super();
		this.setProject(project);
	}

	/**
	 * @param project
	 * @param code
	 * @param message
	 */
	public ProjectDTO(Project project, int code, String message) {
		super();
		this.setProject(project);
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the project embedded.
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the passed project.
	 */
	public void setProject(Project project) {
		this.project = project;
	}

}
