package fr.skiller.data.external;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ContributorView {
	
	/**
	 * the staff identifier.
	 */
	int idStaff;
	
	/**
	 * the full name of this developer <i>(first name + last name)</i>.
	 */
	String fullname;
	
	/**
	 * {@code true} if this staff member is in activity in the company, {@code false} otherwise
	 */
	boolean active;
	
	/**
	 * {@code true} if this staff member is external from the company, {@code false} otherwise
	 */
	boolean external;
	
	/**
	 * Date of the latest commit.
	 */
	Date firstCommit;
	
	/**
	 * Date of the latest commit.
	 */
	Date lastCommit;
	
	/**
	 * @return number of commit submitted by the developer inside the project.
	 */
	int numberOfCommits;
	
	/**
	 * @return number of files modifier by the developer inside the project.
	 */
	int numberOfFiles;
	
	/**
	 * @param idStaff staff identifier
	 * @param fullname complete name of the developer <i>(first name + last name)</i>
	 * @param firstCommit  Date of the <b>FIRST</b> commit for this developer.
	 * @param active {@code true} if this staff member is in activity in the staff, {@code false} otherwise
	 * @param external {@code true} if this staff member belongs to the company, {@code false} otherwise
	 * @param lastCommit  Date of the <b>LATEST</b> commit.
	 * @param numberOfCommits number of commit submitted by the developer inside the project.
	 * @param numberOfFiles number of files modifier by the developer inside the project.
	 */
	public ContributorView(final int idStaff, final String fullname, final boolean active, final boolean external, final Date firstCommit, final Date lastCommit, 
			final int numberOfCommits, final int numberOfFiles) {
		this.idStaff = idStaff;
		this.fullname = fullname;
		this.active = active;
		this.external = external;
		this.firstCommit = firstCommit;
		this.lastCommit = lastCommit;
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}
}

/**
 * Class in charge of transferring the developers involved in a project 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectContributorDTO extends BaseDTO {

	/**
	 * Project identifier.
	 */
	int idProject;
	
	/**
	 * List of contributors for the project.
	 */
	List<ContributorView> contributors;
	
	/**
	 * @param code code error thrown by the back end.
	 * @param message message associated to the code.
	 * @param idProject the project identifier.
	 * @param contributors list of contributors.
	 */
	public ProjectContributorDTO(final int code, final String message, final int idProject, final List<ContributorView> contributors) {
		super();
		this.code = code;
		this.message = message;
		this.idProject = idProject;
		this.contributors = contributors;
	}

	/**
	 * @param idProject the project identifier.
	 */
	public ProjectContributorDTO(final int idProject) {
		this.idProject = idProject;
		contributors = new ArrayList<>();
	}
	
	/**
	 * @param idStaff staff identifier
	 * @param fullname complete name of the developer <i>(first name + last name)</i>
	 * @param active {@code true} if this staff member is in activity in the staff, {@code false} otherwise
	 * @param external {@code true} if this staff member belongs to the company, {@code false} otherwise
	 * @param firstCommit  Date of the <b>FIRST</b> commit for this developer.
	 * @param lastCommit  Date of the <b>LATEST</b> commit.
	 * @param numberOfCommits number of commit submitted by the developer inside the project.
	 * @param numberOfFiles number of files modifier by the developer inside the project.
	 */
	public void addContributor(final int idStaff, final String fullname, final boolean active, final boolean external, 
			final Date firstCommit, final Date lastCommit, final int numberOfCommits, final int numberOfFiles) {
		contributors.add(new ContributorView(idStaff, fullname, active, external, firstCommit, lastCommit, numberOfCommits, numberOfFiles));
	}
	
}
