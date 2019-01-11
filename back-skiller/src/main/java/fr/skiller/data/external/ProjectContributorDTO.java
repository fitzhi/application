package fr.skiller.data.external;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ContributorView {
	
	/**
	 * the staff identifier.
	 */
	public int idStaff;
	
	/**
	 * the full name of this developer <i>(first name + last name)</i>.
	 */
	public String fullname;
	
	/**
	 * Date of the latest commit.
	 */
	public String lastCommit;
	
	/**
	 * @return number of commit submitted by the developer inside the project.
	 */
	public int numberOfCommits;
	
	/**
	 * @return number of files modifier by the developer inside the project.
	 */
	public int numberOfFiles;
	
	/**
	 * Date pattern 
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm");
	
	/**
	 * @param idStaff staff identifier
	 * @param fullname complete name of the developer <i>(first name + last name)</i>
	 * @param lastCommit  Date of the latest commit.
	 * @param numberOfCommits number of commit submitted by the developer inside the project.
	 * @param numberOfFiles number of files modifier by the developer inside the project.
	 */
	public ContributorView(int idStaff, String fullname, Date lastCommit, int numberOfCommits, int numberOfFiles) {
		this.idStaff = idStaff;
		this.fullname = fullname;
		this.lastCommit = sdf.format(lastCommit);
		this.numberOfCommits = numberOfCommits;
		this.numberOfFiles = numberOfFiles;
	}
}

/**
 * Class in charge of transferring the developers involved in a project 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ProjectContributorDTO {

	/**
	 * Back-end code error.
	 */
	public int code = 0;
	
	/**
	 * Back-end message.
	 */
	public String message = "";

	/**
	 * Project identifier.
	 */
	public int idProject;
	
	/**
	 * List of contributors for the project.
	 */
	public List<ContributorView> contributors;
	
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
		contributors = new ArrayList<ContributorView>();
	}
	
	/**
	 * @param idStaff staff identifier
	 * @param fullname complete name of the developer <i>(first name + last name)</i>
	 * @param lastCommit  Date of the latest commit.
	 * @param numberOfCommits number of commit submitted by the developer inside the project.
	 * @param numberOfFiles number of files modifier by the developer inside the project.
	 */
	public void addContributor(final int idStaff, final String fullname, final Date lastCommit, final int numberOfCommits, final int numberOfFiles) {
		contributors.add(new ContributorView(idStaff, fullname, lastCommit, numberOfCommits, numberOfFiles));
	}
	
}
